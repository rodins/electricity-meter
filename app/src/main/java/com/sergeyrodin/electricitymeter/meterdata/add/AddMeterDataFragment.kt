package com.sergeyrodin.electricitymeter.meterdata.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.databinding.AddMeterDataFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMeterDataFragment : Fragment() {

    private val viewModel by viewModels<AddMeterDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddMeterDataFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.saveMeterDataEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                AddMeterDataFragmentDirections.actionAddMeterDataFragmentToMeterDataListFragment()
            )
        })

        return binding.root
    }

}