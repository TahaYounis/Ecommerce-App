package com.taha.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.taha.ecommerceapp.databinding.ColorRvItemBinding
import org.checkerframework.checker.units.qual.h

class ColorsAdapter: RecyclerView.Adapter<ColorsAdapter.ColorsHolder>() {

    private var selectedPosition = -1

    inner class ColorsHolder(private val binding: ColorRvItemBinding):ViewHolder(binding.root){
        fun bind(color: Int, position:Int){
            //determine which color is selected so if we select the color we will show the shadow and icon
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)
            if (position == selectedPosition){ // color is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            }else{ // color is not selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsHolder {
        return ColorsHolder(
            ColorRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ColorsHolder, position: Int) {
        var color = differ.currentList[position]
        holder.bind(color,position)

        // determine when we should change the selected position value
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                // for unselected items so when we click on the item we check if selected position is >= 0 which
                // will rebuild the view of recyclerView so it will go and execute the bind fun for that position
                // which execute the else in unselected item
                notifyItemChanged(selectedPosition)
            // the after unselected that item we actually select the new item
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(color)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Int) -> Unit)? = null
}