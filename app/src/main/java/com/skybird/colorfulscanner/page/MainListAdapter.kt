package com.skybird.colorfulscanner.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skybird.colorfulscanner.R

/**
 * Date：2022/6/29
 * Describe:
 */
class MainListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data = arrayListOf<FileUiBean>()
    val isEditList = false

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(bean: FileUiBean) {

        }
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (data.size == 0) {
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.main_null_item, parent)
            )
        } else {
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(data[position])
        }
    }

    override fun getItemCount(): Int {
        return if (data.size == 0) 1 else data.size
    }

}

data class FileUiBean(
    val fileName: String,
    val pictureNum: Int,
    val fileType: FileType,
    val isChecked: Boolean = false
)

enum class FileType {
    IMAGE, FILE
}