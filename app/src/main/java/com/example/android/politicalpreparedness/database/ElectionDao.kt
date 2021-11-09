package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert
    fun insert(election: Election)

    @Query("SELECT * FROM   ELECTION_TABLE")
    fun getAllElection():LiveData<List<Election>>

    @Query("SELECT * FROM   ELECTION_TABLE WHERE id=:id")
    fun getElection(id:Int):LiveData<Election>

    @Delete
    fun deleteElection(election: Election)


    @Query("DELETE FROM election_table")
    fun deleteAllElection()

}