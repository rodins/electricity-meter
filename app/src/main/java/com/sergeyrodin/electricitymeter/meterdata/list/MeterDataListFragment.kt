package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataListBinding
import com.sergeyrodin.electricitymeter.utils.hideKeyboard

const val PAID_DATE_ID = "paidDateId"
const val NO_PAID_DATE_ID = -1

class MeterDataListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMeterDataListBinding.inflate(inflater, container, false)
        val dataSource = (requireActivity().application as ElectricityMeterApplication).meterDataSource
        val args = requireArguments()
        val paidDateId = args.getInt(PAID_DATE_ID, NO_PAID_DATE_ID)
        val viewModelFactory = MeterDataListViewModelFactory(dataSource, paidDateId)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(MeterDataListViewModel::class.java)

        binding.meterDataListViewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = MeterDataAdapter()
        binding.dataList.adapter = adapter

        viewModel.dataToDisplay.observe(viewLifecycleOwner, Observer { meterData ->
            adapter.data = meterData
        })

        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner, EventObserver{
            hideKeyboard(requireActivity())
            binding.dataEdit.text.clear()
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.meter_data_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }
}