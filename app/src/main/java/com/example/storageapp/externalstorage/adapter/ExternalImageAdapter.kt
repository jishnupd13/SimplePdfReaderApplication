package com.example.storageapp.externalstorage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.storageapp.R
import com.example.storageapp.databinding.CellExternalStorageImagesBinding
import com.example.storageapp.externalstorage.model.PhotosExternalModel

class ExternalImageAdapter(val onItemLongClick:(item:PhotosExternalModel)->Unit): Adapter<ExternalImageAdapter.ExternalImageViewHolder>() {

   private val diffUtil = object : DiffUtil.ItemCallback<PhotosExternalModel>() {
        override fun areItemsTheSame(
            oldItem: PhotosExternalModel,
            newItem: PhotosExternalModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PhotosExternalModel,
            newItem: PhotosExternalModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val asyncListDiffer = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExternalImageViewHolder {
        val binding = CellExternalStorageImagesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ExternalImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ExternalImageViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.onBind(item)
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    override fun getItemId(position: Int): Long {
        return asyncListDiffer.currentList[position].id
    }

    class ExternalImageViewHolder(
        private val binding: CellExternalStorageImagesBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun onBind(item:PhotosExternalModel){
           Glide
               .with(binding.imageCamera.context)
               .load(item.contentUri)
               .placeholder(ContextCompat.getDrawable(binding.root.context,R.drawable.ic_camera))
               .into(binding.imageCamera)
        }
    }


}