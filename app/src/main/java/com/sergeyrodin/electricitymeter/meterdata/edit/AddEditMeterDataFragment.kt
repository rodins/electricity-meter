package com.sergeyrodin.electricitymeter.meterdata.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.databinding.AddEditMeterDataFragmentBinding
import com.sergeyrodin.electricitymeter.utils.hideKeyboard

class AddEditMeterDataFragment : Fragment() {

    private lateinit var viewModel: AddEditMeterDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddEditMeterDataFragmentBinding.inflate(inflater, container, false)
        val dataSource = (requireContext().applicationContext as ElectricityMeterApplication).meterDataSource
        val args by navArgs<AddEditMeterDataFragmentArgs>()
        val viewModelFactory = AddEditMeterDataViewModelFactory(dataSource, args.meterDataId)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(AddEditMeterDataViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.saveMeterDataEvent.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard(requireActivity())
            findNavController().navigate(
                AddEditMeterDataFragmentDirections.actionAddEditMeterDataFragmentToMeterDataListFragment()
            )
        })

        return binding.root
    }

}