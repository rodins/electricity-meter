package com.sergeyrodin.electricitymeter.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.databinding.MeterDataHistoryFragmentBinding

class MeterDataHistoryFragment : Fragment() {

    private val args: MeterDataHistoryFragmentArgs by navArgs()
    private val viewModel by viewModels<MeterDataHistoryViewModel> {
        MeterDataHistoryViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource,
            args.paidDateId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MeterDataHistoryFragmentBinding.inflate(inflater, container, false)
        val adapter = MeterDataHistoryAdapter()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.dataList.adapter = adapter

        viewModel.dataToDisplay.observe(viewLifecycleOwner, { meterData ->
            adapter.data = meterData
        })

        return binding.root
    }

}