package com.skybird.colorfulscanner.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.ItemMoveFileBinding

/**
 * Date：2022/7/1
 * Describe:
 */
class MoveFileAdapter : RecyclerView.Adapter<MoveFileAdapter.MoveFileViewHolder>() {
    val data = arrayListOf<FileUiBean>()
    var itemClickListener: ItemClickListener? = null
    var curSelectFilePath = ""

    inner class MoveFileViewHolder(val fileBinding: ItemMoveFileBinding) :
        RecyclerView.ViewHolder(fileBinding.root) {
        fun bind(bean: FileUiBean) {
            bean.isChecked = bean.filePath == curSelectFilePath
            fileBinding.bean = bean
            fileBinding.item.setOnClickListener {
//                itemClickListener?.onClick(bean)
                curSelectFilePath = bean.filePath
                notifyDataSetChanged()
            }
            fileBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveFileViewHolder {
        return MoveFileViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_move_file,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MoveFileViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
