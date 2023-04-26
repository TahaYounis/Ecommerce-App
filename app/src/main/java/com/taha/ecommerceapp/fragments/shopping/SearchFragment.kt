package com.taha.ecommerceapp.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.adapters.BestProductAdapter
import com.taha.ecommerceapp.adapters.SearchAdapter
import com.taha.ecommerceapp.data.Product
import com.taha.ecommerceapp.databinding.FragmentSearchBinding
import com.taha.ecommerceapp.util.Resource
import com.taha.ecommerceapp.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllProductRv()

        showKeyboardAutomatically()

        onHomeClick()

        searchProduct()

        lifecycleScope.launchWhenStarted {
            viewModel.search.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        searchAdapter.differ.submitList(it.data)
                        hideLoading()
                        showCancelTv()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireActivity(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        hideLoading()
                        showCancelTv()
                    }
                    else -> Unit
                }
            }
        }

        onSearchTextClick()

        onCancelTvClick()

    }

    private fun hideLoading() {
        binding.progressbarCategories.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressbarCategories.visibility = View.VISIBLE
    }

    private fun setupAllProductRv() {
        searchAdapter = SearchAdapter()
        binding.rvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun onCancelTvClick() {
        binding.tvCancel.setOnClickListener {
            searchAdapter.differ.submitList(emptyList())
            binding.edSearch.setText("")
            hideCancelTv()
        }
    }

    private fun onSearchTextClick() {
        searchAdapter.onItemClick = { product ->
            val bundle = Bundle()
            bundle.putParcelable("product", product)

            /**
             * Hide the keyboard
             */

            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )

        }
    }

    private fun showCancelTv() {
        binding.tvCancel.visibility = View.VISIBLE
        binding.imgMic.visibility = View.GONE
        binding.imgScan.visibility = View.GONE
        binding.fragmeMicrohpone.visibility = View.GONE
        binding.frameScan.visibility = View.GONE

    }

    var job: Job? = null
    private fun searchProduct(){
        binding.edSearch.addTextChangedListener { query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()){
                val searchQuery = query.toString().substring(0,1).uppercase()
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(100L)
                    viewModel.searchProduct(searchQuery)
                }
            }else{
                searchAdapter.differ.submitList(emptyList())
                hideCancelTv()
            }
        }
    }

    private fun hideCancelTv() {
        binding.tvCancel.visibility = View.GONE
        binding.imgMic.visibility = View.VISIBLE
        binding.imgScan.visibility = View.VISIBLE
        binding.fragmeMicrohpone.visibility = View.VISIBLE
        binding.frameScan.visibility = View.VISIBLE
    }

    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    private fun showKeyboardAutomatically() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.edSearch.requestFocus()
        imm.showSoftInput(binding.edSearch,0)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.edSearch.clearFocus()
    }

    override fun onResume() {
        super.onResume()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.visibility = View.VISIBLE
    }
}