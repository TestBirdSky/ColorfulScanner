package com.skybird.colorfulscanner.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.EditFileNameDialogBinding
import com.skybird.colorfulscanner.utils.CSUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2022/6/30
 * Describe:
 */
class CreateFileDialog(
    val title: String = "New Folder",
    val editContent: String = "",
    val onNegative: (editName: String, dialog: Dialog?) -> Unit,
) : DialogFragment() {
    lateinit var binding: EditFileNameDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.edit_file_name_dialog,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.run {
            isCancelable = false
            setCanceledOnTouchOutside(false)
        }
        binding.run {
            tvTitle.text = title
            edit.setText(editContent)
            edit.setSelection(editContent.length)
            tvSave.setOnClickListener {
                val s = edit.text.toString()
                if (TextUtils.isEmpty(s)) {
                    ToastUtils.showShort(R.string.input_name_tips)
                } else {
                    onNegative.invoke(s, dialog)
                }
            }
            tvCancel.setOnClickListener {
                dismiss()
            }

        }
        lifecycleScope.launch {
            delay(200)
            binding.edit.requestFocus()
            context?.let { CSUtils.showSoftInput(it, binding.edit) }
        }
    }
}