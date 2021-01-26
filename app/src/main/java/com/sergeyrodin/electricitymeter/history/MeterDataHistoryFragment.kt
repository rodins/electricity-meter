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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeterDataHistoryFragment : Fragment() {

    private val args: MeterDataHistoryFragmentArgs by navArgs()
    private val viewModel by viewModels<MeterDataHistoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MeterDataHistoryFragmentBinding.inflate(inflater, container, false)
        val adapter = MeterDataHistoryAdapter()

        viewModel.start(args.paidDateId)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.meterDataList.dataList.adapter = adapter

        viewModel.calculator.dataToDisplay.observe(viewLifecycleOwner, { meterData ->
            adapter.data = meterData
        })

        return binding.root
    }

}