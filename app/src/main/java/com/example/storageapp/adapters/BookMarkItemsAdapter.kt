package com.example.storageapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.storageapp.databinding.CellBookmarkItemsBinding
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.round
import com.example.storageapp.utils.epochToIso8601

class BookMarkItemsAdapter(val onBookMarkItemClick:(item:PdfModel)->Unit) : Adapter<BookMarkItemsAdapter.BookMarkItemViewHolder>() {

    private val diffUtils = object : DiffUtil.ItemCallback<PdfModel>() {

        override fun areItemsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return  newItem == oldItem
        }
    }

    val asyncListDiffer = AsyncListDiffer(this,diffUtils)


    class BookMarkItemViewHolder(
        val binding: CellBookmarkItemsBinding
    ): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(item: PdfModel) = binding.apply{
            textPdfName.text = item.name
            val fileSizeInKb = (item.fileSize/1024f)
            val fileSizeInMb = (fileSizeInKb/1024f)
            val fileSize = if(fileSizeInMb>=1) "${fileSizeInMb.round()} MB" else "${fileSizeInKb.round()} kB"
            textFileSizeAndDate.text = "${epochToIso8601(item.createdAt.toLong())} - $fileSize"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarkItemViewHolder {
        val binding = CellBookmarkItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookMarkItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: BookMarkItemViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.onBind(item)
        holder.binding.root.setOnClickListener {
            onBookMarkItemClick(item)
        }
    }
}