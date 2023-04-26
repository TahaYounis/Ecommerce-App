package com.taha.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taha.ecommerceapp.data.Product
import com.taha.ecommerceapp.databinding.ProductRvItemBinding
import com.taha.ecommerceapp.helper.getProductPrice

class BestProductAdapter: RecyclerView.Adapter<BestProductAdapter.BestProductsHolder>() {
    inner class BestProductsHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                    val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                    tvNewPrice.text = String.format("%.2f",priceAfterOffer)
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                if(product.offerPercentage == null)
                    tvNewPrice.visibility = View.INVISIBLE

                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvPrice.text = "${product.price}"
                tvName.text = product.name
            }

        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsHolder {
        return BestProductsHolder(
            ProductRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: BestProductsHolder, position: Int) {
        val  product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick:((Product) -> Unit)? = null

//    fun setFiltered(mList: List<Product>){
//        this.mList = mList
//        notifyDataSetChanged()
//    }
}