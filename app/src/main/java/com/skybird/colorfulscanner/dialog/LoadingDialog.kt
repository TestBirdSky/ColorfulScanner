package com.skybird.colorfulscanner.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.DialogLoadingFragmentBinding

/**
 * Date：2022/7/6
 * Describe:
 */
class LoadingDialog(private val isWindowTransparent: Boolean = true) : DialogFragment() {
    lateinit var binding: DialogLoadingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_loading_fragment,
                container,
                false
            )
        //布局底色
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //设置dialog窗体颜色透明
        if (isWindowTransparent)
            dialog?.window?.setDimAmount(0f)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.run {
            setCanceledOnTouchOutside(false)
        }
//        val rotate: Animation = AnimationUtils.loadAnimation(context, R.anim.start_animtor)
//        binding.progressBar.startAnimation(rotate)
    }
}