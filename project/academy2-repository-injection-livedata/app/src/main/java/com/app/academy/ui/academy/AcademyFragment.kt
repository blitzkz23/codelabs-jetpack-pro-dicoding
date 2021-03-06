package com.app.academy.ui.academy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.academy.databinding.FragmentAcademyBinding
import com.app.academy.viewmodel.ViewModelFactory


class AcademyFragment : Fragment() {

	private lateinit var fragmentAcademyBinding: FragmentAcademyBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		fragmentAcademyBinding = FragmentAcademyBinding.inflate(layoutInflater, container, false)
		return fragmentAcademyBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (activity != null) {
			// Set view model and data from it
			val factory = ViewModelFactory.getInstance(requireActivity())
			val viewModel = ViewModelProvider(
				this, factory
			)[AcademyViewModel::class.java]


			// Set adapter
			val academyAdapter = AcademyAdapter()
			fragmentAcademyBinding.progressBar.visibility = View.VISIBLE
			viewModel.getCourses().observe(viewLifecycleOwner, { courses ->
				fragmentAcademyBinding.progressBar.visibility = View.GONE
				academyAdapter.setCourses(courses)
				academyAdapter.notifyDataSetChanged()
			})

			with(fragmentAcademyBinding.rvAcademy) {
				layoutManager = LinearLayoutManager(context)
				setHasFixedSize(true)
				adapter = academyAdapter
			}
		}
	}

}