package com.taha.ecommerceapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.util.Constants.INTRODUCTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object {
        const val SOPPING_ACTIVITY = 23
        const val ACCOUNT_OPTION_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment22
    }

    init {
        // check if button is clicked or if the user is already started
        val isButtonChecked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null){
            viewModelScope.launch {
                _navigate.emit(SOPPING_ACTIVITY)
            }
        }else if (isButtonChecked){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        }else{ Unit }
    }
    fun startButtonClick(){
        // change value inside shared preference
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }
}