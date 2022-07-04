package com.skybird.colorfulscanner.page

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.skybird.colorfulscanner.R
import com.skybird.colorfulscanner.base.BaseDataBindingAc
import com.skybird.colorfulscanner.databinding.AcTakePhotoBinding
import com.skybird.colorfulscanner.toNexAct
import com.skybird.colorfulscanner.utils.LogCSE
import com.skybird.colorfulscanner.utils.LogCSI
import java.text.SimpleDateFormat
import java.util.*

/**
 * Date：2022/7/4
 * Describe:
 */
class TakePhotoActivity : BaseDataBindingAc<AcTakePhotoBinding>() {
    private var imageCapture: ImageCapture? = null

    override fun layoutId() = R.layout.ac_take_photo

    @SuppressLint("RestrictedApi")
    override fun initUI() {
        binding.run {
            ivClose.setOnClickListener {
                onBackPressed()
            }
            ivNineGrid.setOnCheckedChangeListener { buttonView, isChecked ->

            }
            btnFlashlight.setOnCheckedChangeListener { buttonView, isChecked ->
                imageCapture?.camera?.cameraControl?.enableTorch(isChecked)
            }
            ivTakePhoto.setOnClickListener {
                ivTakePhoto.isClickable = false
                takePhoto()
            }
        }
        startCamera()
    }

    override fun initData() {

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                LogCSE("Use case binding failed ${exc.printStackTrace()}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    LogCSI("Photo capture failed: ${exc.message}")
                    binding.ivTakePhoto.isClickable = true
                    ToastUtils.showLong(R.string.take_photo_failed)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    binding.ivTakePhoto.isClickable = true
                    toNexAct(PictureDealActivity::class.java, Bundle().apply {
                        putString("picture_uri", output.savedUri.toString())
                    })
                    LogCSI(msg)
                }
            }
        )
    }

}