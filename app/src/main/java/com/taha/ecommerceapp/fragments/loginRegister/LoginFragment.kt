package com.taha.ecommerceapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.activities.ShoppingActivity
import com.taha.ecommerceapp.databinding.FragmentLoginBinding
import com.taha.ecommerceapp.dialog.setupBottomSheetDialog
import com.taha.ecommerceapp.util.Resource
import com.taha.ecommerceapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLoginLogin.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email,password)
            }
            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            tvForgetPasswordLogin.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
            lifecycleScope.launchWhenCreated {
                viewModel.resetPassword.collect{
                    when(it){
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            Snackbar.make(requireView(),"Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
        // launchWhenCreated make aware of app lifecycle so when app be in background will unsubscribe with shared flow
        lifecycleScope.launchWhenCreated {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLoginLogin.revertAnimation()
                        Intent(requireActivity(),ShoppingActivity::class.java).also { intent ->
                            /* this flag will make sure to pop activity from the stack so when the user login into their account
                             they will navigate to the shopping activity and when navigate back we don't need to go back to the login and register activity*/
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireActivity(), it.message.toString(), Toast.LENGTH_LONG).show()
                        binding.btnLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }
}