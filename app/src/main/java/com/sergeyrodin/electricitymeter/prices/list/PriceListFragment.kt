package com.sergeyrodin.electricitymeter.prices.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.PriceListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PriceListFragment : Fragment() {

    private val viewModel by viewModels<PriceListViewModel>()
    private var actionMode: ActionMode? = null
    private lateinit var adapter: PriceListAdapter
    private lateinit var binding: PriceListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PriceListFragmentBinding.inflate(inflater, container, false)

        createAdapter()
        setupBindingAfterCreateAdapter()

        updatePricesToAdapter()
        navigateToAddPriceFragment()
        switchActionMode()
        updateSelectedPriceToAdapter()
        displaySnackbarOnDeleteError()

        return binding.root
    }

    private fun createAdapter() {
        adapter = PriceListAdapter(PriceLongClickListener { price ->
            viewModel.onPriceLongClick(price)
        }, PriceClickListener {
            viewModel.onPriceClick()
        })
    }

    private fun setupBindingAfterCreateAdapter() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.priceItems.adapter = adapter
    }

    private fun updatePricesToAdapter() {
        viewModel.prices.observe(viewLifecycleOwner) { prices ->
            adapter.prices = prices
        }
    }

    private fun navigateToAddPriceFragment() {
        viewModel.addPriceEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                PriceListFragmentDirections.actionPriceListFragmentToPriceFragment()
            )
        })
    }

    private fun switchActionMode() {
        viewModel.actionDeleteEvent.observe(viewLifecycleOwner) { isActionMode ->
            if (isActionMode) {
                startActionMode()
            } else {
                finishActionMode()
            }
        }
    }

    private fun updateSelectedPriceToAdapter() {
        viewModel.selectedPriceEvent.observe(viewLifecycleOwner) { price ->
            adapter.selectedPrice = price
        }
    }

    private fun displaySnackbarOnDeleteError() {
        viewModel.deleteErrorEvent.observe(viewLifecycleOwner, EventObserver { count ->
            val errorText = resources.getQuantityString(R.plurals.delete_price_error, count, count)
            Snackbar.make(requireView(), errorText, Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun startActionMode() {
        if(actionMode == null) {
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    val inflater = mode?.menuInflater
                    inflater?.inflate(R.menu.price_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    if(item?.itemId == R.id.action_delete_price) {
                        viewModel.onDeletePrice()
                        return true
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    actionMode = null
                    viewModel.onDestroyActionMode()
                }
            })
        }
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }
}