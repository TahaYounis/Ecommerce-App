package com.taha.ecommerceapp.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.data.User
import com.taha.ecommerceapp.databinding.FragmentUserAccountBinding
import com.taha.ecommerceapp.dialog.setupBottomSheetDialog
import com.taha.ecommerceapp.util.Resource
import com.taha.ecommerceapp.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment: Fragment(R.layout.fragment_user_account) {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{ showUserLoading() }
                    is Resource.Success ->{
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {
                viewModel.resetPassword(it)
            }
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}