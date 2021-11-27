package com.example.sl.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.sl.databinding.FragmentMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var loading = true
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private lateinit var flowersListViewModel: ShopsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        flowersListViewModel = ViewModelProvider(
            this,
            FlowersListViewModelFactory(requireContext())
        ).get(ShopsViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = mLayoutManager


        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        val flowersAdapter = FlowersAdapter { flower -> adapterOnClick(flower) }
        binding.recycler.adapter = flowersAdapter

        flowersListViewModel.flowersLiveData.observe(requireActivity(),
            {
                it?.let {
                    if(it.isEmpty()){
                        binding.sectionLabel.visibility = VISIBLE
                    }else{
                        binding.progressBar.visibility = INVISIBLE
                        binding.sectionLabel.visibility = INVISIBLE
                    }
                    flowersAdapter.submitList(it)
                }
            })
        flowersListViewModel.fetchNextPage(flowersAdapter.itemCount.toLong())
        {
        }
        binding.recycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1) && dy != 0) {
                        flowersListViewModel.fetchNextPage(flowersAdapter.itemCount.toLong()) {
                            recyclerView.post {
                                recyclerView.smoothScrollBy(
                                    0,
                                    dpToPx(150f, requireContext()).roundToInt()
                                )
                            }
                        }
                    }
                }
            })
        return root
    }

    fun dpToPx(dp: Float, context: Context): Float {
        return dp * (context.resources
            .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun pxToDp(px: Float, context: Context): Float {
        return px / (context.resources
            .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun adapterOnClick(flower: Shop) {
        val intent = Intent(requireContext(), Item::class.java)
        intent.putExtra(SHOP_ID, flower.id!!)
        intent.putExtra(IS_ARCHIVED,pageViewModel.text.value)
        startActivity(intent)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val SHOP_ID = "section_number"
        private const val IS_ARCHIVED = "is_archived"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}