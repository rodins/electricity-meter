package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.PaidListFragmentBinding

class PaidListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = PaidListFragmentBinding.inflate(inflater, container, false)
        val viewModelFactory = PaidListViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource
        )
        val viewModel = ViewModelProvider(this, viewModelFactory).get(PaidListViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val adapter = PaidListAdapter(PaidDateClickListener { id ->
            viewModel.onItemClick(id)
        })
        binding.dateItems.adapter = adapter

        viewModel.paidDates.observe(viewLifecycleOwner, Observer{
            adapter.data = it
        })

        viewModel.itemClickEvent.observe(viewLifecycleOwner, EventObserver{ paidDateId ->
            findNavController().navigate(
                PaidListFragmentDirections.actionPaidListFragmentToMeterDataListFragment(paidDateId)
            )
        })

        return binding.root
    }

}