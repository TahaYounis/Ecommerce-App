package com.taha.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.google.firebase.firestore.FirebaseFirestore
import com.taha.ecommerceapp.data.Product
import com.taha.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search: StateFlow<Resource<List<Product>>> = _search

    fun searchProduct(searchQuery: String){
        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }
        sp(searchQuery).addOnCompleteListener {
            if (it.isSuccessful){
                val productsList = it.result!!.toObjects(Product::class.java)
                viewModelScope.launch {
                    _search.emit(Resource.Success(productsList))
                }
            }else{
                viewModelScope.launch {
                    _search.emit(Resource.Error(it.exception.toString()))
                }
            }
        }
    }

    fun sp(searchQuery: String) = firestore.collection("Products")
        .orderBy("name")
        .startAt(searchQuery)
        .endAt("\u03A9+$searchQuery")
        .limit(5)
        .get()
}