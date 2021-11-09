package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Division


class VoterInfoViewModelFactory(val application: Application,
                                val electionDao: ElectionDao,
                                val id:Int,
                                val division: Division
                                ): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(application,electionDao,id,division) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}