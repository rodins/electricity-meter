package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataListBinding
import com.sergeyrodin.electricitymeter.utils.hideKeyboard

class MeterDataListFragment : Fragment() {
    private val args: MeterDataListFragmentArgs by navArgs()
    private val viewModel by viewModels<MeterDataListViewModel> {
        MeterDataListViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource,
            args.paidDateId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMeterDataListBinding.inflate(inflater, container, false)
        val adapter = MeterDataAdapter()

        setupBinding(binding, adapter)
        setDataToAdapter(adapter)
        observeHideKeyboardEvent(binding)

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupBinding(
        binding: FragmentMeterDataListBinding,
        adapter: MeterDataAdapter
    ) {
        binding.apply {
            meterDataListViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            dataList.adapter = adapter
        }
    }

    private fun setDataToAdapter(adapter: MeterDataAdapter) {
        viewModel.dataToDisplay.observe(viewLifecycleOwner, { meterData ->
            adapter.data = meterData
        })
    }

    private fun observeHideKeyboardEvent(binding: FragmentMeterDataListBinding) {
        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard(requireActivity())
            binding.dataEdit.text.clear()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.meter_data_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_paid) {
            viewModel.onPaid()
            return true
        }else {
            return NavigationUI.onNavDestinationSelected(item, findNavController())
                    || super.onOptionsItemSelected(item)
        }
    }
}