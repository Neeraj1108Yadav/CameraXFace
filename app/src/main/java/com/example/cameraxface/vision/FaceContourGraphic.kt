package com.example.cameraxface.vision

import android.graphics.*
import com.example.cameraxface.camerax.GraphicOverlay
import com.google.mlkit.vision.face.Face
import kotlin.math.sqrt

class FaceContourGraphic(overlay: GraphicOverlay?,
                         private val face: Face,
                         private val imageRect: Rect) : GraphicOverlay.Graphic(overlay!!) {

    /*private val facePositionPaint: Paint
    private val landmarkPositionPaint: Paint
    private val numColors = COLORS.size
    private val idPaints = Array(numColors) { Paint() }
    private val boxPaints = Array(numColors) { Paint() }
    private val labelPaints = Array(numColors) { Paint() }*/

    private val facePositionPaint: Paint
    private val contourPointPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.WHITE
        val contourColor = Color.RED

        facePositionPaint = Paint()
        contourPointPaint = Paint()
        facePositionPaint.color = selectedColor
        contourPointPaint.color = contourColor

        idPaint = Paint()
        idPaint.color = selectedColor

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    override fun draw(canvas: Canvas?) {
        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )
        canvas?.drawRect(rect, boxPaint)

        val contours = face.allContours
        for(faceContour in contours){
            for(points in faceContour.points){
                val px = translateX(points.x)
                val py = translateY(points.y)
                canvas?.drawCircle(px,py,FACE_POSITION_RADIUS,contourPointPaint)
            }
        }
    }

    companion object {
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val FACE_POSITION_RADIUS = 5.0f
    }

}

/*
init {
        //Log.d("frame","GraphicOverlay Width : ${overlay?.imageWidth} x Height : ${overlay?.imageHeight}")
        val selectedColor = Color.RED
        val landmarkColor = Color.RED

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        landmarkPositionPaint = Paint()
        landmarkPositionPaint.color = landmarkColor
        for (i in 0 until numColors) {
            idPaints[i] = Paint()
            idPaints[i].color = COLORS[i][0]
            idPaints[i].textSize = ID_TEXT_SIZE
            boxPaints[i] = Paint()
            boxPaints[i].color = COLORS[i][1]
            boxPaints[i].style = Paint.Style.STROKE
            boxPaints[i].strokeWidth = BOX_STROKE_WIDTH
            labelPaints[i] = Paint()
            labelPaints[i].color = COLORS[i][1]
            labelPaints[i].style = Paint.Style.FILL
        }
    }

    override fun draw(canvas: Canvas?) {
        val colorID = 0

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
                    bottomLeftY = translateY(point.y)
                    bottomLeftX = translateX(point.x)
                    /*canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )*/
                }else if(contour.faceContourType == 6 && leftEye == (contour.points.size/2) ){
                    topLeftX = translateX(point.x)
                    topLeftY = translateY(point.y)
                    /*canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )*/
                } else if(contour.faceContourType == 7 && rightEye == 1){
                    topRightX = translateX(point.x)
                    topRightY = translateY(point.y)
                    /*canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )*/
                }

                /*if(contour.faceContourType == 1){
                    topLeftX = translateX(point.x)
                    topLeftY = translateY(point.y)
                    canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )

                }else if(contour.faceContourType == 1 && facePoints == contour.points.size){
                    topRightX = translateX(point.x)
                    topRightY = translateY(point.y)
                    canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )
                }else if(contour.faceContourType == 2 && leftEyeBrowTopPoints == contour.points.size){ // LEFT EYEBROW TOP
                    bottomLeftY = translateY(contour.points[contour.points.size-1].y)
                    bottomLeftX = translateX(contour.points[contour.points.size-1].x)
                    /*canvas?.drawCircle(
                        translateX(point.x),
                        translateY(point.y),
                        FACE_POSITION_RADIUS,
                        facePositionPaint
                    )*/
                }*/

                /*else if(contour.faceContourType == 4 && rightEyeBrowTopPoints == contour.points.size){// RIGHT EYEBROW TOP
                  bottomRightY = translateY(contour.points[contour.points.size-1].y)
                  canvas.drawCircle(
                    translateX(point.x),
                    translateY(point.y),
                    FACE_POSITION_RADIUS,
                    facePositionPaint
                  )
                }*/
                /*canvas.drawText(
                    "P: " + String.format(Locale.US, "%d",points),
                    translateX(point.x),
                    translateY(point.y),
                    idPaints[colorID]
                  )
                canvas.drawCircle(
                  translateX(point.x),
                  translateY(point.y),
                  FACE_POSITION_RADIUS,
                  facePositionPaint
                )*/
            }
        }

        //val distanceBetweenTopTwoPoints = sqrt(((topRightX - topLeftX) * (topRightX - topLeftX)) + (((topRightY - topLeftY) * (topRightY - topLeftY))))
        val distanceBetweenTopTwoPoints = sqrt(((topLeftX - topRightX) * (topLeftX - topRightX)) + (((topLeftY - topRightY) * (topLeftY - topRightY))))
        val distanceBetweenTopBottomPoints = sqrt(((topLeftX - bottomLeftX) * (topLeftX - bottomLeftX)) + (((topLeftY - bottomLeftY) * (topLeftY - bottomLeftY))))
        val left = topRightX
        val top =  topRightY
        val right = topRightX + distanceBetweenTopTwoPoints
        val bottom = topRightY + distanceBetweenTopBottomPoints
        //Log.d("frame","topLeftX : $topLeftX topLeftY : $topLeftY topLeftX2 : $right topLeftY2 : $bottom")
        val foreheadRect = RectF(right, top, left, bottom.toFloat())
        canvas?.drawRect(foreheadRect, boxPaints[colorID])
        //drawFaceLandmark(canvas,FaceLandmark.LEFT_EYE)
        //drawFaceLandmark(canvas,FaceLandmark.RIGHT_EYE)
    }

    companion object {
        private const val FACE_POSITION_RADIUS = 5.0f
        private const val LANDMARK_POSITION_RADIUS = 10.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 40.0f
        private const val BOX_STROKE_WIDTH = 5.0f
        private const val NUM_COLORS = 10
        private val COLORS =
            arrayOf(
                intArrayOf(Color.BLACK, Color.WHITE),
                intArrayOf(Color.WHITE, Color.MAGENTA),
                intArrayOf(Color.BLACK, Color.LTGRAY),
                intArrayOf(Color.WHITE, Color.RED),
                intArrayOf(Color.WHITE, Color.BLUE),
                intArrayOf(Color.WHITE, Color.DKGRAY),
                intArrayOf(Color.BLACK, Color.CYAN),
                intArrayOf(Color.BLACK, Color.YELLOW),
                intArrayOf(Color.WHITE, Color.BLACK),
                intArrayOf(Color.BLACK, Color.GREEN)
            )
    }
 */