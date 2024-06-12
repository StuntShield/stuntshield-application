package com.geby.stuntshield.data

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geby.stuntshield.data.response.ResultsItem
import com.geby.stuntshield.databinding.ItemRowArticleBinding

class ArticleAdapter : ListAdapter<ResultsItem, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class MyViewHolder(private val binding: ItemRowArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResultsItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(item.thumbnail)
                    .into(imgItemPhoto)
                tvItemName.text = item.title
                tvItemPreview.text = item.description
                root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                    root.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultsItem>() {
            override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}