package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataListBinding

class MeterDataListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMeterDataListBinding.inflate(inflater, container, false)

        val viewModel by viewModels<MeterDataListViewModel>{
            MeterDataListViewModelFactory(DataHolder.data)
        }

        binding.meterDataListViewModel = viewModel

        val adapter = MeterDataAdapter()
        adapter.data = viewModel.dataToDisplay
        binding.dataList.adapter = adapter

        viewModel.addDataEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                R.id.action_meterDataListFragment_to_meterDataInputFragment
            )
        })
        return binding.root
    }
}