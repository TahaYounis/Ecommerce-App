package com.taha.ecommerceapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.activities.ShoppingActivity
import com.taha.ecommerceapp.adapters.ColorsAdapter
import com.taha.ecommerceapp.adapters.SizesAdapter
import com.taha.ecommerceapp.adapters.ViewPagerForImages
import com.taha.ecommerceapp.data.CartProduct
import com.taha.ecommerceapp.databinding.FragmentProductDetailsBinding
import com.taha.ecommerceapp.util.Resource
import com.taha.ecommerceapp.util.hideBottomNavigationView
import com.taha.ecommerceapp.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment(R.layout.fragment_product_details) {

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPagerForImages() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSizes.visibility = View.INVISIBLE
        }
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        viewPagerAdapter.differ.submitList(product.images)
        // we used (?.let) because may there is no colors so if there is color apply it in adapter
        product.colors?.let { colorsAdapter.differ.submitList(it)}
        product.sizes?.let { sizesAdapter.differ.submitList(it)}

        sizesAdapter.onItemClick = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductToCart(CartProduct(product, 1, selectedColor, selectedSize))
        }
        lifecycleScope.launchWhenCreated {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.btnAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), "Product was added", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.btnAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}