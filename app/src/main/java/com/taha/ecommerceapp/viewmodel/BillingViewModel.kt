package com.taha.ecommerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taha.ecommerceapp.data.Address
import com.taha.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
):ViewModel(){

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }
    fun getUserAddress(){
        viewModelScope.launch { _address.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                // have listener on that because we might navigate from billing fragment to address fragment
                // and when we do that the user might add new address or delete an exited address, now we want
                // reflect that to the billing fragment this why we used this callback addSnapshotListener
                if (error != null){
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(addresses!!)) }
            }
    }
}