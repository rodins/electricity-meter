package com.sergeyrodin.electricitymeter.meterdata.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataInputBinding

class MeterDataInputFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMeterDataInputBinding.inflate(inflater, container, false)
        val viewModel by viewModels<MeterDataInputViewModel>()

        binding.meterDataInputViewModel = viewModel

        viewModel.saveDataEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(R.id.meterDataListFragment)
        })

        return binding.root
    }
}