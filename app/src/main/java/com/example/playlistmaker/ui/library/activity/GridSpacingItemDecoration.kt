package com.example.playlistmaker.ui.library.activity

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(private val spanCount: Int, private val horizontalSpacing: Int, private val verticalSpacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing

        if (column == 0) {
            outRect.left = verticalSpacing
            outRect.right = horizontalSpacing
        } else {
            outRect.left = horizontalSpacing
            outRect.right = verticalSpacing
        }

    }
}
