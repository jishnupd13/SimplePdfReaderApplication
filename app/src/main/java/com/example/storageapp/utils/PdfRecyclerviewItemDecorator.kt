package com.example.storageapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PdfRecyclerviewItemDecorator (
        private val paddingTop:Int,
        private val paddingHorizontal:Int,
        private val paddingBottom:Int
        ) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    )
    {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = paddingHorizontal
        outRect.left = paddingHorizontal
        outRect.bottom = paddingBottom
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = paddingTop
        }
    }
}