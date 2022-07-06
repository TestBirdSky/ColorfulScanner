package com.skybird.colorfulscanner.dialog

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.databinding.ShareImageDialogBinding
import com.skybird.colorfulscanner.loadImage
import com.skybird.colorfulscanner.utils.CSFileUtils
import com.skybird.colorfulscanner.utils.LogCSI
import kotlinx.coroutines.launch
import java.io.*

/**
 * Date：2022/7/6
 * Describe:
 */
class BottomShareDialog(val bitmap: Bitmap) : DialogFragment() {

    lateinit var binding: ShareImageDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.share_image_dialog,
                container,
                false
            )
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.setGravity(Gravity.BOTTOM)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            iv.loadImage(bitmap)
            ivClose.setOnClickListener {
                dismiss()
            }
            ivShare.setOnClickListener {
                if (btnJpg.isChecked) {
                    shareBitmapFile()
                } else {
                    sharePdfFile()
                }
            }
            ivSaveToLocal.setOnClickListener {
                XXPermissions.with(this@BottomShareDialog)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request(
                        object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean
                            ) {
                                if (all) {
                                    lifecycleScope.launch {
                                        if (btnJpg.isChecked) {
                                            CSFileUtils.saveBitmapToLocalPhotoAlbum(
                                                context!!,
                                                bitmap
                                            )
                                            dismiss()
                                        } else {
                                            val file = CSFileUtils.saveBitmapToAppCacheFile(bitmap)
                                            ToastUtils.showLong(
                                                getString(
                                                    R.string.save_picture_to_local_success,
                                                    file.absolutePath
                                                )
                                            )
                                            dismiss()
                                            LogCSI("file --> ${file.absolutePath}")
                                        }
                                    }
                                }
                            }

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean
                            ) {
                                super.onDenied(permissions, never)
                                if (never) {
                                    ToastUtils.showShort(getString(R.string.failed_permission))
                                } else {
                                    ToastUtils.showShort(getString(R.string.failed_permission))
                                }
                            }
                        }
                    )

            }
            btnJpg.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    btnPdf.isChecked = false
                } else {
                    if (!btnPdf.isChecked) {
                        btnPdf.isChecked = true
                    }
                }
            }
            btnPdf.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    btnJpg.isChecked = false
                } else {
                    if (!btnJpg.isChecked) {
                        btnJpg.isChecked = true
                    }
                }
            }

        }
    }

    override fun getTheme(): Int {
        return R.style.BottomShareDialogStyle
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.run {
            window?.setLayout(
                dm.widthPixels,
                window?.attributes?.height ?: ConvertUtils.dp2px(470f)
            )
        }
    }

    private fun shareBitmapFile() {
        lifecycleScope.launch {
            val path = CSFileUtils.saveBitmapToTempFile(bitmap)
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setAction(Intent.ACTION_VIEW)
            context?.let {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(it, "fileprovider", File(path))
                } else {
                    Uri.fromFile(File(path))
                }
                intent.setDataAndType(uri, "image/*")
                startActivity(Intent.createChooser(intent, "share"))
            }
            dismiss()
        }
    }

    private fun sharePdfFile() {
        lifecycleScope.launch {
            val file = CSFileUtils.saveBitmapToTempPdfFile(bitmap)
            val intent = Intent(Intent.ACTION_SEND)
            context?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val uri = FileProvider.getUriForFile(it, "fileprovider", file)
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                } else {
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                }
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.type = "application/pdf"
                startActivity(Intent.createChooser(intent, "share"))
            }
            dismiss()
        }
    }
}