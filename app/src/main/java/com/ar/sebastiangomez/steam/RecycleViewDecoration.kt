package com.ar.sebastiangomez.steam

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val spaceHeight: Int, private val paddingHorizontal: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = spaceHeight
        outRect.bottom = spaceHeight
        outRect.left = paddingHorizontal // Padding izquierdo
        outRect.right = paddingHorizontal // Padding derecho
    }
}