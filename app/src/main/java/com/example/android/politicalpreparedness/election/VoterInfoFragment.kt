package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {
    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        //View Model
        val application = requireNotNull(this.activity).application
        var id = arguments?.let { VoterInfoFragmentArgs.fromBundle(it).argElectionId }
        var division = arguments?.let { VoterInfoFragmentArgs.fromBundle(it).argDivision }
        val dataSource2 =ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = VoterInfoViewModelFactory(application,dataSource2,id!!,division!!)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory).get(VoterInfoViewModel::class.java)
        binding.viewModel = viewModel

        //Observers
        viewModel.ballotClick.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                loadUrl(viewModel.state.value?.electionAdministrationBody?.ballotInfoUrl)
                viewModel.onBalootClicked()
            }
        })
        viewModel.voteLocationClick.observe(viewLifecycleOwner, Observer {
            if (it == true) {
               loadUrl(viewModel.state.value?.electionAdministrationBody?.votingLocationFinderUrl)
                viewModel.onVoteLocationClicked()
            }
        })
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        return binding.root
    }
    fun loadUrl(url:String?){
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireContext().startActivity(intent)

    }

}