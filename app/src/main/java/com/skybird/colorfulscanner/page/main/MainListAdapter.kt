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
class MainListAdapter(
    val itemClickListener: (bean: FileUiBean) -> Unit,
    val itemNameClickListener: (bean: FileUiBean) -> Unit,
    val itemCheckListener: (isCanDel: Boolean, isCanMove: Boolean) -> Unit
) : ListAdapter<FileUiBean, MainListAdapter.MyViewHolder>(DiffUI()) {
    private val data = arrayListOf<FileUiBean>()
    private var checkedList = arrayListOf<FileUiBean>()

    private val noCheckedFolderList = arrayListOf<FileUiBean>()

    var isShowEditList = false
        set(value) {
            field = value
            if (isShowEditList) {
                checkedList.clear()
                noCheckedFolderList.clear()
                for (da in data) {
                    da.isChecked = false
                    if (da.fileType == FileType.FOLDER) {
                        noCheckedFolderList.add(da)
                    }
                }
            } else {
                itemCheckListener.invoke(false, false)
            }
            notifyDataSetChanged()
        }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iv = view.findViewById<ImageView>(R.id.iv_item_icon)
        private val item = view.findViewById<ViewGroup>(R.id.item)
        private val ivNull = view.findViewById<ImageView>(R.id.iv_null)
        private val tvName = view.findViewById<TextView>(R.id.tv_name)
        private val tvNum = view.findViewById<TextView>(R.id.tv_num)
        private val itemChecked = view.findViewById<CheckBox>(R.id.item_check)

        fun bind(bean: FileUiBean, position: Int) {
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
                    setData(bean)
                    refreshCheckListener()
                    notifyItemChanged(adapterPosition)
                } else {
                    itemClickListener.invoke(bean)
                }
            }
            tvName.setOnClickListener {
                if (bean.fileType != FileType.IMAGE) {
                    itemNameClickListener.invoke(bean)
                }
            }
        }
    }

    private fun setData(bean: FileUiBean) {
        LogCSI("setData $bean")
        if (bean.isChecked) {
            checkedList.add(bean)
        } else {
            checkedList.remove(bean)
        }
        if (bean.fileType == FileType.FOLDER) {
            if (!bean.isChecked) {
                noCheckedFolderList.add(bean)
            } else {
                noCheckedFolderList.remove(bean)
            }
        }
    }

    private fun refreshCheckListener() {
        itemCheckListener.invoke(
            checkedList.size > 0,
            checkedList.size > 0 && noCheckedFolderList.size > 0
        )
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
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshAllData(datas: ArrayList<FileUiBean>) {
        if (isShowEditList) {
            checkedList.clear()
            noCheckedFolderList.clear()
            for (da in datas) {
                if (da.fileType == FileType.FOLDER) {
                    noCheckedFolderList.add(da)
                }
            }
        }
        data.clear()
        data.addAll(datas)
        submitList(datas) {
            LogCSI("callback---${datas.size}")
            notifyDataSetChanged()
        }
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

    fun getNoCheckedFolderList(): ArrayList<FileUiBean> {
        return noCheckedFolderList
    }

    fun getAllCheckedList(): ArrayList<FileUiBean> {
        return checkedList
    }
}

class DiffUI : DiffUtil.ItemCallback<FileUiBean>() {
    override fun areItemsTheSame(oldItem: FileUiBean, newItem: FileUiBean): Boolean {
        LogCSI("areItemsTheSame---> ${oldItem.filePath == newItem.filePath} ---${oldItem}--${newItem}")
        return oldItem.filePath == newItem.filePath
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

enum class FileType {
    IMAGE, FOLDER
}