package com.example.anival01.mainScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anival01.databinding.ItemNewsImageBinding


class NewsImageAdapter(private val images: Array<Int>) :
    RecyclerView.Adapter<NewsImageAdapter.ViewHolder>() {

    private lateinit var b: ItemNewsImageBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        b = ItemNewsImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResId = images[position]
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = b.imageView
    }
}
