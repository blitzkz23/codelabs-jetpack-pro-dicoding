package com.app.academy.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.academy.R
import com.app.academy.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
		setContentView(activityHomeBinding.root)

		val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
		activityHomeBinding.viewPager.adapter = sectionsPagerAdapter
		activityHomeBinding.tabs.setupWithViewPager(activityHomeBinding.viewPager)

		supportActionBar?.elevation = 0f

	}
}