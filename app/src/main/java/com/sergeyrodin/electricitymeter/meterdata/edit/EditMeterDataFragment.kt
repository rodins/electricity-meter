package com.sergeyrodin.electricitymeter.meterdata.edit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.EditMeterDataFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMeterDataFragment : Fragment() {

    private val viewModel: EditMeterDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditMeterDataFragmentBinding.inflate(inflater, container, false)
        val args by navArgs<EditMeterDataFragmentArgs>()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.start(args.meterDataId)

        viewModel.saveMeterDataEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                EditMeterDataFragmentDirections
                    .actionAddEditMeterDataFragmentToMeterDataListFragment()
            )
        })

        setHasOptionsMenu(true)

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