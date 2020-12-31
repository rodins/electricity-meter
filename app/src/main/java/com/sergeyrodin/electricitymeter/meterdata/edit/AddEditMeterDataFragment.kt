package com.sergeyrodin.electricitymeter.meterdata.edit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.AddEditMeterDataFragmentBinding
import com.sergeyrodin.electricitymeter.utils.hideKeyboard

class AddEditMeterDataFragment : Fragment() {

    private lateinit var viewModel: AddEditMeterDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddEditMeterDataFragmentBinding.inflate(inflater, container, false)
        val dataSource = (requireContext().applicationContext as ElectricityMeterApplication).meterDataSource
        val args by navArgs<AddEditMeterDataFragmentArgs>()
        val viewModelFactory = AddEditMeterDataViewModelFactory(dataSource, args.meterDataId)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(AddEditMeterDataViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.saveMeterDataEvent.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard(requireActivity())
            findNavController().navigate(
                AddEditMeterDataFragmentDirections.actionAddEditMeterDataFragmentToMeterDataListFragment()
            )
        })

        val isItemIdSet = args.meterDataId != -1

        setHasOptionsMenu(isItemIdSet)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_meter_data_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete) {
            viewModel.onDeleteMeterData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}