package com.skybird.colorfulscanner.page.main

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skybird.colorfulscanner.utils.LogCSE
import java.lang.IndexOutOfBoundsException

/**
 * Date：2022/7/1
 * Describe:
 */
class MyGridLayoutManager constructor(
    context: Context,
    spanCount: Int,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : GridLayoutManager(
    context, spanCount,
    orientation, reverseLayout
) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            LogCSE("e $e")
        }
    }
}