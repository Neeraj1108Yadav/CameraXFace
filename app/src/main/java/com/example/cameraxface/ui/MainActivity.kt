package com.example.cameraxface.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxface.camerax.CameraManager
import com.example.cameraxface.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(),CameraManager.CameraPreviewListener{

    /**
     * <uses-feature android:name="android.hardware.camera.any" />
     * It ensures that device has camera.
     * And .any means it can be front or back camera
     */
    private lateinit var binding:ActivityMainBinding
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createCameraManager()
        if(allPermissionsGranted()){
            cameraManager.startCamera()
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            binding.viewFinder,
            this,
            binding.graphicOverlay,
            this
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                cameraManager.startCamera()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getImageProxy(imageProxy: ImageProxy) {
       CoroutineScope(Dispatchers.IO).launch {
           try {
               val mediaImage = imageProxy.image
               if (mediaImage != null && mediaImage.format == InputImage.IMAGE_FORMAT_YUV_420_888) {
                   val rotation = imageProxy.imageInfo.rotationDegrees
                   val yBuffer = mediaImage.planes[0].buffer // Y
                   val vuBuffer = mediaImage.planes[2].buffer // VU
                   val ySize = yBuffer.remaining()
                   val vuSize = vuBuffer.remaining()

                   val nv21 = ByteArray(ySize + vuSize)

                   yBuffer.get(nv21, 0, ySize)
                   vuBuffer.get(nv21, ySize, vuSize)

                   val yuvImage = YuvImage(nv21, ImageFormat.NV21, mediaImage.width, mediaImage.height, null)
                   val outputStream = ByteArrayOutputStream()
                   //yuvImage.compressToJpeg(cropRect, 100, outputStream)
                   yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, outputStream)
                   imageProxy.close()
                   val imageBytes = outputStream.toByteArray()
                   val original = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                   //** Rotation of Bitmap **//
                   val matrix = Matrix()
                   matrix.preRotate(rotation.toFloat())
                   val rotatedBitmap = Bitmap.createBitmap(
                       original,
                       0,
                       0,
                       original.width,
                       original.height,
                       matrix,
                       true
                   )
                   original.recycle()
                   //** Rotation of Bitmap **//
                    withContext(Dispatchers.Main){
                        binding.actionImage.setImageBitmap(rotatedBitmap)
                    }
               } else {
                   imageProxy.close()
               }
           } catch (e: Exception) {
               imageProxy.close()
               e.printStackTrace()
           }
       }
    }

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf (Manifest.permission.CAMERA).toTypedArray()
    }
}