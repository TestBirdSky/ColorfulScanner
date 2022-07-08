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
import com.skybird.colorfulscanner.databinding.DelDialogBinding

/**
 * Date：2022/7/7
 * Describe:
 */
class DeleteDialog(val content: String, val onNegative: () -> Unit) : DialogFragment() {

    lateinit var binding: DelDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.del_dialog,
                container,
                false
            )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.run {
            isCancelable = false
            setCanceledOnTouchOutside(false)
        }
        binding.run {
            tvContent.text = content
            tvDel.setOnClickListener {
                onNegative.invoke()
                dismiss()
            }
            tvCancel.setOnClickListener {
                dismiss()
            }

        }
    }
}