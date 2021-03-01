package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeterDataListFragment : Fragment() {
    private val viewModel by viewModels<MeterDataListViewModel>()

    private var paidMenuItem: MenuItem? = null
    private var isPaidMenuItemVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMeterDataListBinding.inflate(inflater, container, false)
        val adapter = MeterDataAdapter(MeterDataClickListener { id ->
            viewModel.onEditMeterData(id)
        })

        setupBinding(binding, adapter)
        setDataToAdapter(adapter)

        viewModel.addMeterDataEvent.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(
                MeterDataListFragmentDirections
                    .actionMeterDataListFragmentToAddMeterDataFragment()
            )
        })

        viewModel.editMeterDataEvent.observe(viewLifecycleOwner, EventObserver{ meterDataId ->
            findNavController().navigate(
                MeterDataListFragmentDirections
                    .actionMeterDataListFragmentToAddEditMeterDataFragment(meterDataId)
            )
        })

        viewModel.isPaidButtonVisible.observe(viewLifecycleOwner, { isVisible ->
            isPaidMenuItemVisible = isVisible
            paidMenuItem?.let {
                it.isVisible = isVisible
            }
        })

        viewModel.setPriceEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.priceFragment)
        })

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
            meterDataList.dataList.adapter = adapter
        }
    }

    private fun setDataToAdapter(adapter: MeterDataAdapter) {
        viewModel.calculator.dataToDisplay.observe(viewLifecycleOwner, { meterData ->
            adapter.data = meterData
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.meter_data_menu, menu)
        paidMenuItem = menu.findItem(R.id.action_paid)
        paidMenuItem?.isVisible = isPaidMenuItemVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_paid) {
            viewModel.onPaid()
            return true
        } else {
            return NavigationUI.onNavDestinationSelected(item, findNavController())
                    || super.onOptionsItemSelected(item)
        }
    }
}