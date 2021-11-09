package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.GoogleApiStatus
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class VoterInfoViewModel(application: Application,
                         private val dataSource: ElectionDao,
                         private val id:Int,
                         private val division: Division
                         ) : AndroidViewModel(application) {

    //variables
    private val _status = MutableLiveData<GoogleApiStatus>()
    val status: LiveData<GoogleApiStatus> get() = _status

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse:LiveData<VoterInfoResponse> get() = _voterInfoResponse

    private val _state = MutableLiveData<State>()
    val state:LiveData<State> get() = _state

    private val _correspondenceAddress = MutableLiveData<String>()
    val correspondenceAddress:LiveData<String> get() = _correspondenceAddress

    val isSaved:LiveData<Boolean> = Transformations.map(dataSource.getElection(id)){ if (it==null){false } else{true} }

    private val _voteLocationClick = MutableLiveData<Boolean>(false)
    val voteLocationClick :LiveData<Boolean> get() = _voteLocationClick

    private val _ballotClick = MutableLiveData<Boolean>(false)
    val ballotClick :LiveData<Boolean> get() = _ballotClick

    init {
        //Load Api
        viewModelScope.launch {
            _status.value= GoogleApiStatus.LOADING
            getVoterInfo()
        }
    }
    suspend fun  getVoterInfo() {
        withContext(Dispatchers.IO){
            try {
                var listResult = CivicsApi.retrofitService.getVoterInfo( "${division.state},${division.country}",id,false).await()
                _voterInfoResponse.postValue(listResult)
                _state.postValue(listResult.state?.get(0))
                _correspondenceAddress.postValue(
                        "${_state.value?.electionAdministrationBody?.correspondenceAddress?.line1?:""} " +
                                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.line2?:""}\n" +
                                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.city?:""}\n" +
                                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.state?:""}")

                _status.postValue(GoogleApiStatus.DONE)
            } catch (e: Exception) {
                _status.postValue(GoogleApiStatus.ERROR)
            }
        }
    }

    //On url Click
    fun onVoteLocationClick(){ _voteLocationClick.value = true }
    fun onVoteLocationClicked(){_voteLocationClick.value = false}
    fun onBallotClick(){_ballotClick.value = true}
    fun onBalootClicked(){_ballotClick.value = false}



    fun saveOrRemoveElection(){
        viewModelScope.launch {
            if (isSaved.value ==true){
                deleteElection(voterInfoResponse.value!!.election)
            }else{
                saveElection(voterInfoResponse.value!!.election)
            }
        }
    }
    suspend fun saveElection(election: Election){
        withContext(Dispatchers.IO){
            dataSource.insert(election)
        }
    }
    suspend fun deleteElection(election: Election){
        withContext(Dispatchers.IO){
            dataSource.deleteElection(election)
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}