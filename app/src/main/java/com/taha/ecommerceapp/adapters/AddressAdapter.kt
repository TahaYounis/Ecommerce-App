package com.taha.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.taha.ecommerceapp.R
import com.taha.ecommerceapp.data.Address
import com.taha.ecommerceapp.databinding.AddressRvItemBinding

class AddressAdapter: Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressRvItemBinding): ViewHolder(binding.root){
        fun bind(address: Address, isSelected:Boolean) {
            binding.apply {
                buttonAddress.text = address.addressTitle
                if (isSelected) {
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }else{
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0) // in this case we want unSelect the address
                notifyItemChanged(selectedAddress) // this will make u sure to unselect the current
            selectedAddress = holder.adapterPosition // change the position
            notifyItemChanged(selectedAddress) // make sure to apply or select button and to change its color

            onClick?.invoke(address)
        }
    }
    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Address)->Unit)? = null
}