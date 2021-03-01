package com.sergeyrodin.electricitymeter.price

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeyrodin.electricitymeter.EventObserver
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.PriceFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PriceFragment : Fragment() {

    private val viewModel: PriceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PriceFragmentBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.saveEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.meterDataListFragment)
        })

        return binding.root
    }

}