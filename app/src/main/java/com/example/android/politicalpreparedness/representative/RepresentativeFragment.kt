package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.GoogleApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentRepresentativeBinding
     private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 33
    }
    private lateinit var viewModel: RepresentativeViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)


        //View Model
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner =this
        val viewModelFactory = RepresentativeViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //Adapter
        val adapter = RepresentativeListAdapter()
        binding.representativeRc.adapter = adapter

        //Observers
        viewModel.clickGetMyLocatin.observe(viewLifecycleOwner, Observer {
            if (it==true){
                if (checkLocationPermissions()) { getLocation() }
                viewModel.onBtnGetMyLocationClicked()
            }
        })
        viewModel.clickFindRepresentative.observe(viewLifecycleOwner, Observer {
            if (it==true){
                hideKeyboard()
                viewModel.onBtnGetRepresentativeClicked()
            }
        })

        viewModel.representativeList.observe(viewLifecycleOwner, Observer{
            adapter.submitList(it)
        })
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        mFusedLocationClient.lastLocation.addOnSuccessListener {
            if (it!=null){viewModel.setAddress(geoCodeLocation(it))}else{
                Snackbar.make(binding.root,requireContext().getString(R.string.get_location_failed),Snackbar.LENGTH_LONG).show()
            }
        }
        viewModel.findRepresentative()
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
        return address
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0]==PackageManager.PERMISSION_DENIED){
            Snackbar.make(binding.root,requireContext().getString(R.string.get_location_denied),Snackbar.LENGTH_LONG).show()
        }else{
            getLocation()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {

        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }




}