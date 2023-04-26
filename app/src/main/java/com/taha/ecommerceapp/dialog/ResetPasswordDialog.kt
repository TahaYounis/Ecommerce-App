package com.taha.ecommerceapp.dialog

import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.taha.ecommerceapp.R

// create this fun as extension fun because the same fun is going to be used in another fragment so we don't need to rewrite this builder again and again
fun Fragment.setupBottomSheetDialog(
    onSendCLick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val btnSend = view.findViewById<Button>(R.id.btnSendResetPassword)
    val btnCancel = view.findViewById<Button>(R.id.btnCancelResetPassword)

    btnSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendCLick(email)
        dialog.dismiss()
    }
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}