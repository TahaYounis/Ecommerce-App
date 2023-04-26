package com.taha.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taha.ecommerceapp.data.order.Order
import com.taha.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }

        firestore.runBatch {
            // runButch to many operation in same time if any operation fail all fail, only write
            // TODO: Add the order into user-orders collection
            // TODO: Add the order into orders collection
            // TODO: Delete the products from user-cart collection

            firestore.collection("user").document(auth.uid!!).collection("orders").document()
                .set(order)

            firestore.collection("orders").document().set(order)

            firestore.collection("user").document(auth.uid!!).collection("cart")
                .get().addOnSuccessListener {
                    it.documents.forEach{
                        it.reference.delete()
                    }
                }
            //for runBatch
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }.addOnFailureListener {
            viewModelScope.launch { _order.emit(Resource.Error(it.message.toString())) }
        }
    }
}