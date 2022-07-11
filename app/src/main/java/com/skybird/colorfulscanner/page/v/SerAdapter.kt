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
import com.skybird.colorfulscanner.utils.LogCSI
import com.skybird.colorfulscanner.utils.getCurSelectedName

/**
 * Date：2022/7/8
 * Describe:
 */


class SerAdapter(val click: (data: SerUiBean) -> Unit) :
    RecyclerView.Adapter<SerViewHolder>() {
    var curSelectedName = getCurSelectedName()
    var currentList = arrayListOf<SerUiBean>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        holder.bind(currentList[position], curSelectedName)
    }

    private fun itemClick(data: SerUiBean) {
        curSelectedName = data.name
        notifyDataSetChanged()
        click.invoke(data)
    }

    fun reset() {
        curSelectedName = getCurSelectedName()
        notifyDataSetChanged()
    }

    override fun getItemCount() = currentList.size
}

class SerViewHolder(val binding: ItemSerS1Binding, val itemClick: (data: SerUiBean) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SerUiBean, selectedName: String) {
        binding.run {
            item.text = data.name
            item.isChecked = selectedName == data.name
            iv.setImageResource(CSUtils.getCircleNIcon(data.native))
            item.setOnClickListener {
                itemClick.invoke(data)
            }
            executePendingBindings()
        }
    }
}

data class SerUiBean(val name: String, val native: String, var isChecked: Boolean = false)