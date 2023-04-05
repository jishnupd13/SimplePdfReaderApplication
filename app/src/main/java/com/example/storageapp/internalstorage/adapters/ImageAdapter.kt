package com.example.storageapp.internalstorage.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.storageapp.databinding.CellInternalStorageImagesBinding
import com.example.storageapp.hide
import com.example.storageapp.internalstorage.models.InternalStorageImageModel
import com.example.storageapp.show

class ImageAdapter(
    val onLongClickPressed:(item:InternalStorageImageModel,position:Int)->Unit,
    val onItemSelected:(item:InternalStorageImageModel,position:Int)-> Unit
): Adapter<ImageAdapter.InternalImageViewHolder>() {

    private val diffUtils = object : DiffUtil.ItemCallback<InternalStorageImageModel>() {
        override fun areItemsTheSame(
            oldItem: InternalStorageImageModel,
            newItem: InternalStorageImageModel
        ): Boolean {
            return  (oldItem.name == newItem.name) && (oldItem.isImageSelected == newItem.isImageSelected)
        }

        override fun areContentsTheSame(
            oldItem: InternalStorageImageModel,
            newItem: InternalStorageImageModel
        ): Boolean {
            return oldItem == newItem
        }
    }

     val asyncListDiffer = AsyncListDiffer(this,diffUtils)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternalImageViewHolder {
        val binding = CellInternalStorageImagesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InternalImageViewHolder(binding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: InternalImageViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.onBind(item)
        holder.binding.apply {

            root.setOnLongClickListener {
                onLongClickPressed(item,position)
                true
            }

            root.setOnClickListener {
                onItemSelected(item,position)
            }
        }
    }

    class InternalImageViewHolder(
        val binding: CellInternalStorageImagesBinding
    ):RecyclerView.ViewHolder(binding.root) {
        fun onBind(item:InternalStorageImageModel) = binding.apply {
            imageCamera.setImageBitmap(item.bitmap)
            if(item.isImageSelected)
                imgSelect.show()
            else
                imgSelect.hide()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}


