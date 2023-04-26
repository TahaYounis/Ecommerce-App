package com.taha.ecommerceapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.activities.ShoppingActivity
import com.taha.ecommerceapp.databinding.FragmentIntroductionBinding
import com.taha.ecommerceapp.viewmodel.IntroductionViewModel
import com.taha.ecommerceapp.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.taha.ecommerceapp.viewmodel.IntroductionViewModel.Companion.SOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.navigate.collect {
                when (it) {
                    SOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            /* this flag will make sure to pop activity from the stack so when the user login into their account
                             they will navigate to the shopping activity and when navigate back we don't need to go back to the login and register activity*/
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    ACCOUNT_OPTION_FRAGMENT -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment22)
                    }
                    else -> Unit
                }
            }
        }

        binding.btnStart.setOnClickListener {
            // change value inside shared preference
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment22)
        }
    }
}