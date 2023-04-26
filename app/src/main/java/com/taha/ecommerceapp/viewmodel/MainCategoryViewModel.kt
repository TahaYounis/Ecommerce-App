package com.taha.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.taha.ecommerceapp.data.Product
import com.taha.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    //fun fetch specialProducts from fireStore
    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener {
                //convert QuerySnapshot to our product object
                val specialProductsList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun fetchBestDealsProducts(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category","Best Deals").get()
            .addOnSuccessListener {
                val bestDealsProduct = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProduct))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun fetchBestProducts(){
        if (!pagingInfo.isPagingEnd) { // if it true then will not fetch more data
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10)
                .get() // limit just load 10 product and we will use paging to ge next 10 product
                .addOnSuccessListener {
                    val bestProduct = it.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProduct == pagingInfo.oldBestProducts // if equal then stop paging if not continue paging
                    pagingInfo.oldBestProducts = bestProduct
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProduct))
                    }
                    pagingInfo.bestProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
    internal data class PagingInfo(
        var bestProductsPage: Long = 1,
        var oldBestProducts: List<Product> = emptyList(), // used to compare the current list with the old list so if they equal then we know that we don't have any more products from fireStore so end paging
        var isPagingEnd: Boolean = false
    )
}