package com.frange.coasters.ui.main

import android.os.Bundle
import com.frange.coasters.databinding.ActivityMainBinding
import com.frange.coasters.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun createBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        updateToolbar(true)

        binding.toolbar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(com.frange.coasters.R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }

    private fun updateToolbar(isMainScreen: Boolean) {
        binding.toolbar.title =
            if (isMainScreen) getString(com.frange.coasters.R.string.poi_list_toolbar_title)
            else getString(com.frange.coasters.R.string.poi_detail_toolbar_title)

        binding.toolbar.subtitle =
            if (isMainScreen) getString(com.frange.coasters.R.string.poi_list_toolbar_subtitle)
            else ""

        supportActionBar?.setDisplayHomeAsUpEnabled(!isMainScreen)
        supportActionBar?.setDisplayShowHomeEnabled(!isMainScreen)
    }

}