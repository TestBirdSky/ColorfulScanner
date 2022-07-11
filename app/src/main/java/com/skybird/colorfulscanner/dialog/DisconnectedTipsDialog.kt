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
import com.skybird.colorfulscanner.databinding.DisconnectedDialogBinding

/**
 * Date：2022/7/11
 * Describe:
 */
class DisconnectedTipsDialog(val onNegative: () -> Unit, val onPositive: () -> Unit) :
    DialogFragment() {


    lateinit var binding: DisconnectedDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.disconnected_dialog,
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
            tvSure.setOnClickListener {
                onNegative.invoke()
                dismiss()
            }
            tvCancel.setOnClickListener {
                onPositive.invoke()
                dismiss()
            }

        }
    }
}