package com.taha.ecommerceapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// this class to add extra spaces between our recyclerView items
class VerticalItemDecoration(private val amount: Int = 30): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // here specify from which we want to add this amount (like margin)
        outRect.bottom = amount
    }
}