package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.FragmentMeterDataListBinding

class MeterDataListFragment : Fragment() {
    private val viewModel by viewModels<MeterDataListViewModel> {
        MeterDataListViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource)
    }

    private var paidMenuItem: MenuItem? = null
    private var isPaidMenuItemVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMeterDataListBinding.inflate(inflater, container, false)
        val adapter = MeterDataAdapter(MeterDataClickListener { id ->
            viewModel.onAddEditMeterData(id)
        })

        setupBinding(binding, adapter)
        setDataToAdapter(adapter)

        viewModel.addMeterDataEvent.observe(viewLifecycleOwner, EventObserver{ meterDataId ->
            val title = if(meterDataId == -1)
                getString(R.string.add_data)
            else
                getString(R.string.edit_data)

            findNavController().navigate(
                MeterDataListFragmentDirections
                    .actionMeterDataListFragmentToAddEditMeterDataFragment(meterDataId, title)
            )
        })

        viewModel.isPaidButtonVisible.observe(viewLifecycleOwner, { isVisible ->
            isPaidMenuItemVisible = isVisible
            paidMenuItem?.let {
                it.isVisible = isVisible
            }
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
            dataList.adapter = adapter
        }
    }

    private fun setDataToAdapter(adapter: MeterDataAdapter) {
        viewModel.dataToDisplay.observe(viewLifecycleOwner, { meterData ->
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
        }else {
            return NavigationUI.onNavDestinationSelected(item, findNavController())
                    || super.onOptionsItemSelected(item)
        }
    }
}