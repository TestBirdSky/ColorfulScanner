package com.skybird.colorfulscanner.page.picturedeal

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.ItemFliterBinding
import com.skybird.colorfulscanner.utils.LogCSI
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Date：2022/7/5
 * Describe:
 */
class FilterAdapter(val itemClick: (bean: FilterItemBean) -> Unit) :
    RecyclerView.Adapter<FilterAdapter.Holder>() {
    val data = arrayListOf<FilterItemBean>()
    private var lastClickPosition = 0

    inner class Holder(val binding: ItemFliterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bean: FilterItemBean) {
            binding.run {
                tv.text = bean.name
                loadImage(iv, bean.bitmap)
                iv.setOnClickListener {
                    if (lastClickPosition != adapterPosition) {
                        lastClickPosition = adapterPosition
                        itemClick.invoke(bean)
                    }
                }
                executePendingBindings()
            }
        }
    }

    private fun loadImage(view: ImageView, bitmap: Bitmap, corner: Int = 8) {
        val c = RoundedCorners(corner)
        //处理图片裁剪布满整个view  CenterCrop()
        val roundOptions = RequestOptions().transform(c).transform(CenterCrop(), c)
        Glide.with(view).load(bitmap).skipMemoryCache(true).apply(roundOptions).into(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_fliter,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun getCurSelectedBitmap(): Bitmap {
        return data[lastClickPosition].bitmap
    }

    fun refreshData(bitmap: Bitmap) {
        if (data.isEmpty()) {
            val origin = FilterItemBean(Filter(), bitmap, "Original")
            val starLit =
                FilterItemBean(SampleFilters.getStarLitFilter(), bitmap, "StarLit")
            val blueMess =
                FilterItemBean(SampleFilters.getBlueMessFilter(), bitmap, "BlueMess")
            val aweStruckVibe =
                FilterItemBean(SampleFilters.getAweStruckVibeFilter(), bitmap, "AweStruckVibe")
            val limeStutter =
                FilterItemBean(SampleFilters.getLimeStutterFilter(), bitmap, "LimeStutter")
            val nightWhisper =
                FilterItemBean(SampleFilters.getNightWhisperFilter(), bitmap, "NightWhisper")
            val blackAWhite =
                FilterItemBean(Filter().apply {
                    addSubFilter(SaturationSubFilter(0.1f))
                }, bitmap, "B&W")
            data.add(origin)
            data.add(starLit)
            data.add(blueMess)
            data.add(aweStruckVibe)
            data.add(limeStutter)
            data.add(nightWhisper)
            data.add(blackAWhite)
            notifyDataSetChanged()
        }
        CoroutineScope(Dispatchers.IO).launch {
            for (i: Int in 0 until data.size) {
                val s = System.currentTimeMillis()
                data[i].bitmap = Bitmap.createBitmap(bitmap)
                data[i].bitmap = data[i].filter.processFilter(data[i].bitmap)
                LogCSI("start--$i---》 ${s}--- $bitmap ---${data[i].bitmap}")
                withContext(Dispatchers.Main) {
                    notifyItemChanged(i)
                }
                LogCSI("end--》 ${System.currentTimeMillis() - s}---")
            }
        }

    }

}