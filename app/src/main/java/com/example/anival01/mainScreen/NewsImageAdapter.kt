package com.example.anival01.mainScreen

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anival01.databinding.ItemNewsImageBinding


//class NewsImageAdapter(private val images: Array<Int>) :
//    RecyclerView.Adapter<NewsImageAdapter.ViewHolder>() {
//
//    private lateinit var b: ItemNewsImageBinding
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        b = ItemNewsImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(b.root)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val imageResId = images[position]
//        holder.imageView.setImageResource(imageResId)
//    }
//
//    override fun getItemCount(): Int {
//        return images.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView = b.imageView
//    }
//}

class NewsImageAdapter(private val images: Array<Int>, private val links: Array<String>) :
    RecyclerView.Adapter<NewsImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResId = images[position]
        val link = links[position]

        holder.bind(imageResId, link)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(private val binding: ItemNewsImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int, link: String) {
            binding.imageView.setImageResource(imageResId)
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                binding.root.context.startActivity(intent)
            }
        }
    }
}