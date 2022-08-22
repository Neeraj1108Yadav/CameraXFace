package com.example.cameraxface.vision

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.cameraxface.camerax.BaseImageAnalyzer
import com.example.cameraxface.camerax.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class FaceContourDetectionProcessor(private val view: GraphicOverlay,
                                   private val listener:ImageProxyListener) : BaseImageAnalyzer<List<Face>>()  {

    interface ImageProxyListener{
        fun getProxyImage(imageProxy: ImageProxy)
    }

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        results.forEach {
            val faceGraphic = FaceContourGraphic(graphicOverlay, it, rect)
            graphicOverlay.add(faceGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun getProxyImage(imageProxy: ImageProxy) {
        listener.getProxyImage(imageProxy)
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }
}

/*
 for (face in faces) {
            val faceGraphic = FaceGraphic(graphicOverlay, face)
            // Draws face, leftEyeBrowTop, rightEyeBrowTop contours.
            var bottomNosePoints = 0
            var rightEye = 0
            var leftEye = 0
            var topLeftX= 0F
            var topLeftY = 0F
            var topRightX = 0F
            var topRightY = 0F
            var bottomLeftX = 0F
            var bottomLeftY = 0F

            for (contour in face.allContours) {
                for (point in contour.points) {
                    when(contour.faceContourType){
                        13 -> {bottomNosePoints++}
                        6 -> {leftEye++}
                        7 -> {rightEye++}
                        else ->{}
                    }

                    if(contour.faceContourType == 13 && bottomNosePoints == 1){
                        bottomLeftY = faceGraphic.translateY(point.y)
                        bottomLeftX = faceGraphic.translateX(point.x)
                    }else if(contour.faceContourType == 6 && leftEye == (contour.points.size/2) ){
                        topLeftX = faceGraphic.translateX(point.x)
                        topLeftY = faceGraphic.translateY(point.y)
                    } else if(contour.faceContourType == 7 && rightEye == 1){
                        topRightX = faceGraphic.translateX(point.x)
                        topRightY = faceGraphic.translateY(point.y)
                    }
                }
            }

            val distanceBetweenTopTwoPoints = sqrt(((topLeftX - topRightX) * (topLeftX - topRightX)) + (((topLeftY - topRightY) * (topLeftY - topRightY))))
            val distanceBetweenTopBottomPoints = sqrt(((topLeftX - bottomLeftX) * (topLeftX - bottomLeftX)) + (((topLeftY - bottomLeftY) * (topLeftY - bottomLeftY))))
            val left = topRightX
            val top =  topRightY
            val right = topRightX + distanceBetweenTopTwoPoints
            val bottom = topRightY + distanceBetweenTopBottomPoints
            val foreheadRect = RectF(right, top, left, bottom)
            listener.getFaceBoundingBox(foreheadRect)
            graphicOverlay?.add(faceGraphic)
        }
 */