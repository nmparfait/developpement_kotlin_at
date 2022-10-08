package com.example.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project.databinding.KachraItemBinding
import com.example.project.retrofitClass.BooksItem

class KachraAdapter(private val bookList: List<BooksItem>) :
    RecyclerView.Adapter<KachraAdapter.KachraHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KachraHolder {
        return KachraHolder(
            KachraItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: KachraHolder, position: Int) {
        val model = bookList[position]
        with(holder.binding) {
            tvName.text = model.volumeInfo.title
            tvAuthor.text = model.volumeInfo.authors.toString()

            if (model.volumeInfo.imageLinks != null && model.volumeInfo.imageLinks!!.smallThumbnail != null){
                val url = model.volumeInfo.imageLinks?.smallThumbnail.toString()
                Glide.with(tvName.context)
                    .asBitmap()
                    .load(url)
                    .dontAnimate()
                    .into(mainImage)
            }

        }
    }

    override fun getItemCount() = bookList.size

    class KachraHolder(val binding: KachraItemBinding) : RecyclerView.ViewHolder(binding.root)
}