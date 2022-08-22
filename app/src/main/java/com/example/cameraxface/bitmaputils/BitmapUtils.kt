package com.example.cameraxface.bitmaputils

import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

class BitmapUtils {

    fun convertImageToBitmap(imageProxy: ImageProxy) :Bitmap?{
        var bitmap:Bitmap? = null
        val mediaImage = imageProxy.image
        try {
            if (mediaImage != null && mediaImage.format == InputImage.IMAGE_FORMAT_YUV_420_888) {
                bitmap = getImageBitmap(mediaImage)
                imageProxy.close()
            } else {
                imageProxy.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun getImageBitmap(mediaImage: Image): Bitmap {
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
        val imageBytes = outputStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        //return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val rotated = rotateBitmap(bitmap, 270F)
        val bos = ByteArrayOutputStream()
        rotated.compress(Bitmap.CompressFormat.JPEG, 100, bos) //100 is the best quality possibe
        val square = bos.toByteArray()
        return BitmapFactory.decodeByteArray(square, 0, square.size)
    }

    private fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(degrees)
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
        return rotatedBitmap
    }
}