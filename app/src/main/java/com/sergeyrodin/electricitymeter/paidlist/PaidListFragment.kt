package com.sergeyrodin.electricitymeter.paidlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.ElectricityMeterApplication
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.PaidListFragmentBinding

class PaidListFragment : Fragment() {
    private val viewModel by viewModels<PaidListViewModel> {
        PaidListViewModelFactory(
            (requireActivity().application as ElectricityMeterApplication).meterDataSource
        )
    }

    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PaidListFragmentBinding.inflate(inflater, container, false)
        val adapter = createAdapter()

        setupBinding(binding, adapter)
        setDataToAdapter(adapter)
        observeItemClickEvent()

        viewModel.highlightedPosition.observe(viewLifecycleOwner, { highlightedPosition ->
            adapter.highlightedPosition = highlightedPosition
            if(highlightedPosition != -1) {
                startActionMode()
            }else {
                finishActionMode()
            }


        })

        return binding.root
    }

    private fun createAdapter() = PaidListAdapter(
        PaidDateClickListener { id ->
            viewModel.onItemClick(id)
        }, PaidDateLongClickListener { position ->
            viewModel.onItemLongClick(position)
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

    private fun startActionMode() {
        if(actionMode == null) {
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    val inflater = mode?.menuInflater
                    inflater?.inflate(R.menu.paid_date_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    if(item?.itemId == R.id.action_delete_paid_date) {
                        viewModel.deleteSelectedPaidDate()
                        return true
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    actionMode = null
                }
            })
        }
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }

    private fun navigateToMeterDataListFragment(paidDateId: Int) {
        findNavController().navigate(
            PaidListFragmentDirections.actionPaidListFragmentToMeterDataListFragment(paidDateId)
        )
    }
}