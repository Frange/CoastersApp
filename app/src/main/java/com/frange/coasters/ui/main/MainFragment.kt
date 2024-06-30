package com.frange.coasters.ui.main

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.frange.coasters.R
import com.frange.coasters.databinding.FragmentMainListBinding
import com.frange.coasters.domain.base.Status
import com.frange.coasters.domain.model.ParkInfo
import com.frange.coasters.domain.model.Ride
import com.frange.coasters.ui.base.BaseFragment
import com.frange.coasters.ui.main.adapter.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainListBinding>(),
    AdapterView.OnItemSelectedListener {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var parkInfoSpinnerAdapter: ArrayAdapter<ParkInfo>
    private lateinit var parkListAdapter: ParkListAdapter

    private var currentCoasterPosition = 0

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMainListBinding = FragmentMainListBinding.inflate(inflater, container, false)

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        initObservers()
        initAdapters()

        requestAllParkInfoList()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.swipeRefreshLayout?.isRefreshing = false

            mainViewModel.requestPark(
                currentCoasterPosition
            )
        }

        binding?.refreshButton?.setOnClickListener {
            mainViewModel.requestPark(currentCoasterPosition)
        }

        binding?.bRetry?.setOnClickListener {
            requestAllParkInfoList()
        }
    }

    private fun initAdapters() {
        parkInfoSpinnerAdapter = ParkSpinnerAdapter(
            this.requireContext(),
            R.layout.simple_spinner_item,
            values = arrayListOf()
        )
        binding?.parkInfoSpinner?.adapter = parkInfoSpinnerAdapter

        binding?.parkInfoSpinner?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                currentCoasterPosition = position
                mainViewModel.requestPark(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun requestAllParkInfoList() {
        mainViewModel.requestAllParkInfoList()
    }

    private fun initObservers() {
        mainViewModel.getParkList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding?.progressBar?.visibility = VISIBLE
                    binding?.tvMessage?.visibility = GONE
                    binding?.bRetry?.visibility = GONE
                }
                Status.SUCCESS -> {
                    binding?.progressBar?.visibility = GONE
                    binding?.rvList?.visibility = VISIBLE
                    binding?.tvMessage?.visibility = GONE
                    binding?.bRetry?.visibility = GONE

                    parkInfoSpinnerAdapter.clear()
                    parkInfoSpinnerAdapter.addAll(it.data!!)
                    currentCoasterPosition = 0
                    binding?.parkInfoSpinner?.adapter = parkInfoSpinnerAdapter
                    parkInfoSpinnerAdapter.setNotifyOnChange(true)
                }
                Status.EXCEPTION -> {
                    binding?.progressBar?.visibility = GONE
                    binding?.rvList?.visibility = GONE
                    binding?.tvMessage?.visibility = VISIBLE
                    binding?.bRetry?.visibility = VISIBLE
                    binding?.tvMessage?.text =
                        getString(com.frange.coasters.R.string.exception_poi_call_message)
                    showToast(
                        requireContext(),
                        getString(com.frange.coasters.R.string.error_poi_call_toast)
                    )
                }
                Status.ERROR -> {
                    binding?.progressBar?.visibility = GONE
                    binding?.rvList?.visibility = GONE
                    binding?.tvMessage?.visibility = VISIBLE
                    binding?.bRetry?.visibility = VISIBLE
                    binding?.tvMessage?.text =
                        getString(com.frange.coasters.R.string.error_poi_call_message)
                    showToast(
                        requireContext(),
                        getString(com.frange.coasters.R.string.error_poi_call_toast)
                    )
                }
            }
        }

        mainViewModel.getPark().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    if (it.data != null && !it.data.rideList.isNullOrEmpty()) {
                        initCoasterAdapter(it.data.rideList)
                        parkListAdapter =
                            ParkListAdapter(it.data)
                        binding?.rvList?.adapter = parkListAdapter
                        binding?.rvList?.visibility = VISIBLE
                        binding?.tvMessage?.visibility = GONE
                        binding?.bRetry?.visibility = GONE
                    } else {
                        binding?.rvList?.visibility = GONE
                        binding?.tvMessage?.visibility = VISIBLE
                        binding?.bRetry?.visibility = GONE
                        binding?.tvMessage?.text =
                            getString(com.frange.coasters.R.string.no_rides_message)
                    }
                }
                Status.EXCEPTION -> {
                    binding?.progressBar?.visibility = GONE
//                    binding?.rvList?.visibility = GONE
//                    binding?.tvMessage?.visibility = VISIBLE
//                    binding?.bRetry?.visibility = VISIBLE
//                    binding?.tvMessage?.text =
//                        getString(com.jmr.poi.R.string.exception_poi_call_message)
                    showToast(
                        requireContext(),
                        getString(com.frange.coasters.R.string.error_poi_call_toast)
                    )
                }
                Status.ERROR -> {
                    binding?.progressBar?.visibility = GONE
//                    binding?.rvList?.visibility = GONE
//                    binding?.tvMessage?.visibility = VISIBLE
//                    binding?.bRetry?.visibility = VISIBLE
//                    binding?.tvMessage?.text =
//                        getString(com.jmr.poi.R.string.error_poi_call_message)
                    showToast(
                        requireContext(),
                        getString(com.frange.coasters.R.string.error_poi_call_toast)
                    )
                }
            }
        }
    }

    private fun initCoasterAdapter(coasterList: List<Ride>?) {
        binding?.rvList?.layoutManager = LinearLayoutManager(context)
        coasterList?.sortedBy { coaster -> coaster.name }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        mainViewModel.requestParkList(position)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}