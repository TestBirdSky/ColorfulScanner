package com.skybird.colorfulscanner.page.v

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.ItemSerS1Binding
import com.skybird.colorfulscanner.utils.CSUtils

/**
 * Date：2022/7/8
 * Describe:
 */
var curSelectedName = ""

class SerAdapter(val click: (data: SerUiBean) -> Unit) :
    ListAdapter<SerUiBean, SerViewHolder>(Diff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerViewHolder {
        return SerViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_ser_s1,
                parent,
                false,

                )
        ) {
            itemClick(it)
        }
    }

    override fun onBindViewHolder(holder: SerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private fun itemClick(data: SerUiBean) {
        curSelectedName = data.name
        for (i in currentList) {
            i.isChecked = i.name == curSelectedName
        }
        submitList(currentList)
        click.invoke(data)
    }
}

class SerViewHolder(val binding: ItemSerS1Binding, val itemClick: (data: SerUiBean) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: SerUiBean) {
        binding.run {
            item.text = data.name
            item.isChecked = curSelectedName == data.name
            iv.setImageResource(CSUtils.getCircleNIcon(data.native))
            item.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked && curSelectedName == data.name) {
                    item.isChecked = true
                }
                if (isChecked && curSelectedName != data.name) {
                    itemClick.invoke(data)
                }
            }
            executePendingBindings()
        }
    }
}

data class SerUiBean(val name: String, val native: String, var isChecked: Boolean = false)

class Diff : DiffUtil.ItemCallback<SerUiBean>() {
    override fun areItemsTheSame(oldItem: SerUiBean, newItem: SerUiBean): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SerUiBean, newItem: SerUiBean): Boolean {
        return oldItem.isChecked == newItem.isChecked
    }

}