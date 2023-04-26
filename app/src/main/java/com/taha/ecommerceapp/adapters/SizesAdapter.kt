package com.taha.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.taha.ecommerceapp.databinding.SizesRvItemBinding

class SizesAdapter: RecyclerView.Adapter<SizesAdapter.SizesHolder>() {
    private var selectedPosition = -1

    inner class SizesHolder(private val binding: SizesRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(size: String, position:Int){
            //determine which size is selected so if we select the size we will show the shadow and icon
            binding.tvSize.text = size
            if (position == selectedPosition){ // size is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            }else{ // size is not selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesHolder {
        return SizesHolder(
            SizesRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: SizesHolder, position: Int) {
        var size = differ.currentList[position]
        holder.bind(size,position)

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
            onItemClick?.invoke(size)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((String) -> Unit)? = null
}