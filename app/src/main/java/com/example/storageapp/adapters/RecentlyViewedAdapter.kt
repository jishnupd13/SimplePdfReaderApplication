package com.example.storageapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.storageapp.R
import com.example.storageapp.databinding.CellRecentlyViewedItemBinding
import com.example.storageapp.hide
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.round
import com.example.storageapp.show
import com.example.storageapp.utils.RecentlyViewedUtils
import com.example.storageapp.utils.epochToIso8601

class RecentlyViewedAdapter(val onItemClick:(item:PdfModel)->Unit): Adapter<RecentlyViewedAdapter.RecentlyViewedItemViewHolder>() {

    private var recentlyViewedUtils = RecentlyViewedUtils()

    private val diffUtils = object : DiffUtil.ItemCallback<PdfModel>() {

        override fun areItemsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return  newItem == oldItem
        }
    }

    val asyncListDiffer = AsyncListDiffer(this,diffUtils)

    class RecentlyViewedItemViewHolder(
        val binding: CellRecentlyViewedItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun onBind(item: PdfModel) = binding.apply{
            textPdfName.text = item.name
            val fileSizeInKb = (item.fileSize/1024f)
            val fileSizeInMb = (fileSizeInKb/1024f)
            val fileSize = if(fileSizeInMb>=1) "${fileSizeInMb.round()} MB" else "${fileSizeInKb.round()} kB"
            textFileSizeAndDate.text = "${epochToIso8601(item.createdAt.toLong())} - $fileSize"

            if(checkFileExists(uri = item.contentUri,root.context)){
                layoutFileDoesNotExists.hide()
                root.isEnabled = true
            }else{
                layoutFileDoesNotExists.show()
                root.isEnabled = false
            }

            Glide.with(root.context)
                .load(item.pdfPreview)
                .error(R.drawable.ic_pdf)
                .placeholder(R.drawable.ic_pdf)
                .into(imgPdf)
        }

        private fun checkFileExists(uri:Uri,context:Context):Boolean{
            try {
                context.contentResolver.openInputStream(uri).use { return true }
            }catch (e:Exception){
                return false
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentlyViewedItemViewHolder {
        val binding = CellRecentlyViewedItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentlyViewedItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: RecentlyViewedItemViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.onBind(item)
        holder.binding.root.setOnClickListener {
            onItemClick(item)
            recentlyViewedUtils.saveRecentlyViewedItem(item)
        }
    }


}