package com.sergeyrodin.electricitymeter.paidlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.databinding.PaidListFragmentBinding

class PaidListFragment : Fragment() {
    private val viewModel by viewModels<PaidListViewModel>{
        PaidListViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PaidListFragmentBinding.inflate(inflater, container, false)
        val adapter = createAdapter()

        setupBinding(binding, adapter)
        setDataToAdapter(adapter)
        observeItemClickEvent()

        return binding.root
    }

    private fun createAdapter() = PaidListAdapter(PaidDateClickListener { id ->
        viewModel.onItemClick(id)
    })

    private fun setupBinding(
        binding: PaidListFragmentBinding,
        adapter: PaidListAdapter
    ) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.dateItems.adapter = adapter
    }

    private fun setDataToAdapter(adapter: PaidListAdapter) {
        viewModel.paidDates.observe(viewLifecycleOwner, {
            adapter.data = it
        })
    }

    private fun observeItemClickEvent() {
        viewModel.itemClickEvent.observe(viewLifecycleOwner, EventObserver { paidDateId ->
            navigateToMeterDataListFragment(paidDateId)
        })
    }

    private fun navigateToMeterDataListFragment(paidDateId: Int) {
        findNavController().navigate(
            PaidListFragmentDirections.actionPaidListFragmentToMeterDataListFragment(paidDateId)
        )
    }
}