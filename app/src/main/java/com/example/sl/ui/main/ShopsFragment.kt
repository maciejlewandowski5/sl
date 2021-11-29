package com.example.sl.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.sl.databinding.FragmentMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.example.sl.Item
import com.example.sl.Keys.IS_ARCHIVED
import com.example.sl.Keys.SHOP_ID
import com.example.sl.model.Shop
import com.example.sl.model.State
import com.example.sl.resolveCollection
import com.example.sl.ui.main.adapters.ShopsAdapter
import kotlin.math.roundToInt

class ShopsFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var shopsViewModel: ShopsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val page = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
        val collection = resolveCollection(page)
        initializeViewModel(collection, page)
    }

    private fun initializeViewModel(collection: String, page: Int) {
        shopsViewModel = ViewModelProvider(
            this,
            ShopsViewModelFactory(collection)
        ).get(ShopsViewModel::class.java)
        shopsViewModel.tab = page
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val adapter = ShopsAdapter { shop -> adapterOnClick(shop) }
        initializeRecycler(adapter)

        observeShops(adapter)
        shopsViewModel.fetchFirstPage()
        fetchOnScroll(adapter)
        return root
    }

    private fun fetchOnScroll(adapter: ShopsAdapter) {
        binding.recycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    loadDataIfScrollEnd(recyclerView, dy, adapter)
                }
            })
    }

    private fun loadDataIfScrollEnd(
        recyclerView: RecyclerView,
        dy: Int,
        adapter: ShopsAdapter
    ) {
        if (!recyclerView.canScrollVertically(1) && dy != 0) {
            shopsViewModel.fetchNextPage(adapter.itemCount.toLong()) {
                indicateDataWasLoaded(recyclerView)
            }
        }
    }

    private fun indicateDataWasLoaded(recyclerView: RecyclerView) {
        recyclerView.post {
            recyclerView.smoothScrollBy(
                0,
                dpToPx(SCROLL_BY, requireContext()).roundToInt()
            )
        }
    }

    private fun observeShops(adapter: ShopsAdapter) {
        shopsViewModel.shops.observe(requireActivity(),
            {
                it?.let { state ->
                    when {
                        state.isSuccess() -> {
                            resolveEmptyOrNot(state)
                            adapter.submitList(state.value)
                        }
                        state.isError() -> {
                            binding.sectionLabel.visibility = VISIBLE
                            binding.sectionLabel.text = state.error
                        }
                        state.isLoading() -> {
                            binding.sectionLabel.visibility = INVISIBLE
                            binding.progressBar.visibility = VISIBLE
                        }
                    }
                }
            })
    }

    private fun resolveEmptyOrNot(state: State<List<Shop>>) {
        binding.progressBar.visibility = INVISIBLE
        if (state.value?.isEmpty() == true) {
            binding.sectionLabel.visibility = VISIBLE
        } else {
            binding.sectionLabel.visibility = INVISIBLE
        }
    }

    private fun initializeRecycler(adapter: ShopsAdapter) {
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = mLayoutManager
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
    }

    private fun dpToPx(dp: Float, context: Context): Float {
        return dp * (context.resources
            .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun adapterOnClick(shop: Shop) {
        val intent = Intent(requireContext(), Item::class.java)
        intent.putExtra(SHOP_ID, shop.id!!)
        intent.putExtra(IS_ARCHIVED, shopsViewModel.tab)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"
        private const val SCROLL_BY = 150f

        @JvmStatic
        fun newInstance(sectionNumber: Int): ShopsFragment {
            return ShopsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}