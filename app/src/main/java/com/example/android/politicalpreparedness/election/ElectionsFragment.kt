package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener


class ElectionsFragment: Fragment() {
    private lateinit var binding : FragmentElectionBinding
    private lateinit var viewModel: ElectionsViewModel


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election,container,false)
        binding.lifecycleOwner =this

        //View Model
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(application,dataSource)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ElectionsViewModel::class.java)
        binding.viewModel = viewModel

        //Adapters
        val adapterN = ElectionListAdapter(ElectionListener { viewModel.onNavigateToVoterInfo(it)})
        val adapterL = ElectionListAdapter(ElectionListener { viewModel.onNavigateToVoterInfo(it)})
        binding.nwkElectionRc.adapter = adapterN
        binding.lclElectionRc.adapter = adapterL

        //Observers
        // Up comming Election
        viewModel.upCommingElection.observe(viewLifecycleOwner, Observer {
            adapterN.submitList(it)
        })
        //Saved Election
        viewModel.savedElection.observe(viewLifecycleOwner, Observer {
            adapterL.submitList(it)
        })
        //Navigate to Voter Info Fragment
        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment2(it.id,it.division))
                viewModel.onNavigatedToVoterInfo()
            }
        })





        return binding.root

    }


}