package com.skybird.colorfulscanner.page.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.loadImage
import com.skybird.colorfulscanner.page.FileUiBean
import com.skybird.colorfulscanner.utils.LogCSI

/**
 * Date：2022/6/29
 * Describe:
 */
class MainListAdapter : ListAdapter<FileUiBean, MainListAdapter.MyViewHolder>(DiffUI()) {
    private val data = arrayListOf<FileUiBean>()
    var itemClickListener: ItemClickListener? = null
    var itemNameClickListener: ItemNameClickListener? = null
    var isShowEditList = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = view.findViewById<ImageView>(R.id.iv_item_icon)
        private val item = view.findViewById<ViewGroup>(R.id.item)
        private val ivNull = view.findViewById<ImageView>(R.id.iv_null)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)
        private val tvNum = view.findViewById<TextView>(R.id.tv_num)
        private val itemChecked = view.findViewById<CheckBox>(R.id.item_check)

        fun bind(bean: FileUiBean) {
            tvNum.text = "${bean.pictureNum}"
            tvNum.visibility = if (bean.pictureNum > 0) View.VISIBLE else View.GONE
            if (bean.fileType == FileType.FOLDER) {
                ivNull.visibility = View.VISIBLE
                tvName.text = bean.fileName
                iv.visibility = View.INVISIBLE
            } else {
                tvName.text = ""
                ivNull.visibility = View.GONE
                iv.visibility = View.VISIBLE
                iv.loadImage(bean.filePath)
            }
            if (isShowEditList) {
                itemChecked.isChecked = bean.isChecked
                itemChecked.visibility = View.VISIBLE
            } else {
                itemChecked.visibility = View.GONE
            }
            item.setOnClickListener {
                if (isShowEditList) {
                    bean.isChecked = !bean.isChecked
                    notifyItemChanged(adapterPosition)
                } else {
                    itemClickListener?.onClick(bean)
                }
            }
            tvName.setOnClickListener {
                itemNameClickListener?.onClick(bean)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return data[position].id
    }

    fun refreshAllData(datas: ArrayList<FileUiBean>) {
        data.clear()
        data.addAll(datas)
        submitList(datas)
        LogCSI("refreshAllData---${datas.size}")
        datas.forEach {
            LogCSI("$it")
        }
        notifyDataSetChanged()
    }

    fun getAllImageUrl(): List<String> {
        val arrayList = arrayListOf<String>()
        data.forEach {
            if (it.fileType == FileType.IMAGE) {
                arrayList.add(it.filePath)
            }
        }
        return arrayList
    }

    val noCheckEdFileList = arrayListOf<FileUiBean>()

    fun getAllCheckEdList(): ArrayList<FileUiBean> {
        noCheckEdFileList.clear()
        noCheckEdFileList.addAll(data)
        val re = arrayListOf<FileUiBean>()
        data.forEach {
            if (it.isChecked) {
                re.add(it)
                noCheckEdFileList.remove(it)
            }
        }
        return re
    }
}

class DiffUI : DiffUtil.ItemCallback<FileUiBean>() {
    override fun areItemsTheSame(oldItem: FileUiBean, newItem: FileUiBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FileUiBean, newItem: FileUiBean): Boolean {
        val isSame = (oldItem.fileName == newItem.fileName
                && oldItem.pictureNum == newItem.pictureNum
                && oldItem.filePath == newItem.filePath
                && oldItem.isChecked == newItem.isChecked
                && oldItem.fileType == newItem.fileType)
        LogCSI("areContentsTheSame---> $isSame ---${oldItem}--${newItem}")
        return isSame
    }
}

fun interface ItemClickListener {
    fun onClick(bean: FileUiBean)
}

fun interface ItemNameClickListener {
    fun onClick(bean: FileUiBean)
}

enum class FileType {
    IMAGE, FOLDER
}