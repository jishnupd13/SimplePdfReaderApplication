package com.example.storageapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.storageapp.R
import com.example.storageapp.databinding.CellPdfItemBinding
import com.example.storageapp.hide
import com.example.storageapp.pdfreader.models.PdfModel
import com.example.storageapp.round
import com.example.storageapp.show
import com.example.storageapp.utils.BookMarkUtils
import com.example.storageapp.utils.epochToIso8601

class PdfItemAdapter(
    val onItemLongPressed:(item:PdfModel,position:Int)->Unit,
    val onItemClick:(item:PdfModel,position:Int) -> Unit
): Adapter<PdfItemAdapter.PdfItemViewHolder>() {

    val bookMarkUtils = BookMarkUtils()

    private val diffUtils = object : ItemCallback<PdfModel>() {

        override fun areItemsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PdfModel, newItem: PdfModel): Boolean {
            return  newItem == oldItem
        }
    }

    val asyncListDiffer = AsyncListDiffer(this,diffUtils)

  inner class PdfItemViewHolder(
        val binding:CellPdfItemBinding
    ):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(item:PdfModel) = binding.apply{
           textPdfName.text = item.name
            val fileSizeInKb = (item.fileSize/1024f)
            val fileSizeInMb = (fileSizeInKb/1024f)
            val fileSize = if(fileSizeInMb>=1) "${fileSizeInMb.round()} MB" else "${fileSizeInKb.round()} kB"
            textFileSizeAndDate.text = "${epochToIso8601(item.createdAt.toLong())} - $fileSize"

            if(item.isEnableSelection){
                imgSelected.show()
                imageBookMark.hide()
            } else{
                imgSelected.hide()
                imageBookMark.show()
            }

            if(item.isItemSelected)
                imgSelected.setImageDrawable(ContextCompat.getDrawable(imgSelected.context, R.drawable.ic_selected))
            else
                imgSelected.setImageDrawable(ContextCompat.getDrawable(imgSelected.context, R.drawable.ic_unselected))

            if(bookMarkUtils.fetchBookMarkIdList().contains(item.id)){
                imageBookMark.setImageDrawable(ContextCompat.getDrawable(imageBookMark.context,R.drawable.ic_book_mark_selected))
            }else{
                imageBookMark.setImageDrawable(ContextCompat.getDrawable(imageBookMark.context,R.drawable.ic_book_mark_un_selected))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfItemViewHolder {
        val binding = CellPdfItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PdfItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PdfItemViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.onBind(item)
        holder.binding.root.setOnLongClickListener {
            onItemLongPressed.invoke(item,position)
            asyncListDiffer.currentList.forEach {
                it.isEnableSelection = true
            }
            notifyDataSetChanged()
            true
        }
      holder.binding.root.setOnClickListener {
          onItemClick(item,position)
      }

      holder.binding.imageBookMark.setOnClickListener {
          if(bookMarkUtils.fetchBookMarkIdList().contains(item.id)){
              bookMarkUtils.removeBookMarkId(id = item.id)
              notifyItemChanged(position)
          }else{
              bookMarkUtils.saveBookMarkId(id = item.id)
              notifyItemChanged(position)
          }
      }
    }
}