package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Official
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.GoogleApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeViewModel(application: Application): AndroidViewModel(application) {



    private val _status = MutableLiveData<GoogleApiStatus>()
    val status: LiveData<GoogleApiStatus>
        get() = _status


    var addressLive = MutableLiveData<Address>()
    var line1 = MutableLiveData<String>()
    var line2 = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>("Alabama")
    var postal = MutableLiveData<String>()
    val selectedStatePosition = MutableLiveData<Int>(-1)
   val b = getApplication<Application>().resources.getStringArray(R.array.states)

    private var _representativeList = MutableLiveData<List<Representative>>()
    val representativeList:LiveData<List<Representative>> get() = _representativeList
    init {
        Log.i("STATUSREP","${status.value}")
    }



    fun findRepresentative(){
        viewModelScope.launch {
            _status.value= GoogleApiStatus.LOADING
            _findRepresentatives()
        }
    }
    private suspend fun  _findRepresentatives() {
        withContext(Dispatchers.IO){
            try {
                val (offices, officials) = CivicsApi.retrofitService.getRepresentative("${line1.value} ${line2.value}, ${city.value}, ${state.value}, ${postal.value}").await()
                //Log.i("Reponse","Representative Response ${listResult}")
                val list = offices.flatMap { office -> office.getRepresentatives(officials)}
                _representativeList.postValue(list)
                _status.postValue(GoogleApiStatus.DONE)
                Log.i("STATUSREP","suspend success ${status.value}")
                Log.i("STATUSREP","suspend _satus success ${_status.value}")

                Log.i("Reponse","Representative Response ${_representativeList.value?.size}")
                Log.i("STATUSREP","suspend _satus failed ${_status.value}")

            } catch (e: Exception) {
                Log.i("Reponse","Failure ${e.message}")
                _status.postValue(GoogleApiStatus.ERROR)
                Log.i("STATUSREP","suspend failed ${status.value}")
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */


    private val _clickGetMyLocation = MutableLiveData<Boolean>(false)
    val clickGetMyLocatin :LiveData<Boolean> get() = _clickGetMyLocation
    private val _clickFindRepresentative = MutableLiveData<Boolean>(false)
    val clickFindRepresentative :LiveData<Boolean> get() = _clickFindRepresentative
    fun onBtnGetMyLocationClicked(){
        if (_clickGetMyLocation.value == true){_clickGetMyLocation.value =false}
        else{_clickGetMyLocation.value =true}
    }
    fun onBtnGetRepresentativeClick(){
        _clickFindRepresentative.value = true
            state.value = b.get(selectedStatePosition.value!!)
        findRepresentative()

    }
    fun onBtnGetRepresentativeClicked(){
        _clickFindRepresentative.value = false
    }
    fun setAddress(address: Address){
        line1.value = address.line1
        line2.value = address.line2
        city.value = address.city
        state.value = address.state
        postal.value = address.zip
        addressLive.value = address

    }



}
