package com.sergeyrodin.electricitymeter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.databinding.ActivityMainBinding
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataAdapter
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataListViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil
                .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MeterDataListViewModel::class.java)

        binding.meterDataListViewModel = viewModel

        val adapter = MeterDataAdapter()
        binding.dataList.adapter = adapter

        viewModel.dataToDisplay.observe(this, Observer { meterData ->
            adapter.data = meterData
        })
    }
}