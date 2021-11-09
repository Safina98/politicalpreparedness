package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.GoogleApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ElectionsViewModel(application: Application,
                         private val dataSource: ElectionDao
): AndroidViewModel(application) {

    // Variables
    private val _status = MutableLiveData<GoogleApiStatus>()
    val status: LiveData<GoogleApiStatus> get() = _status
    private val _upCommingElection = MutableLiveData<List<Election>>()
    val upCommingElection : LiveData<List<Election>> get() = _upCommingElection
    val savedElection: LiveData<List<Election>> = dataSource.getAllElection()
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo:LiveData<Election> get() = _navigateToVoterInfo


    init {
        //Load Data From API
        viewModelScope.launch {
            _status.value= GoogleApiStatus.LOADING
            newAPI()
        }
    }

    suspend fun  newAPI() {
        withContext(Dispatchers.IO){
            try {
                var listResult = CivicsApi.retrofitService.getElections().await()
                _upCommingElection.postValue( listResult.elections)
                _status.postValue(GoogleApiStatus.DONE)

            } catch (e: Exception) {
                _status.postValue(GoogleApiStatus.ERROR)
            }
        }
    }



// Navigate to Voter Info
    fun onNavigateToVoterInfo(election: Election){
        _navigateToVoterInfo.value = election
    }
    fun onNavigatedToVoterInfo(){
        _navigateToVoterInfo.value = null
    }


}