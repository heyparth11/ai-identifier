package com.example.aiidentifier.presentation.camera

import android.graphics.Rect
import android.graphics.RectF

fun scaleRect(
    boundingBox: Rect,
    imageWidth: Int,
    imageHeight: Int,
    viewWidth: Float,
    viewHeight: Float,
    rotationDegrees: Int
): RectF {
    val effectiveImgWidth = if (rotationDegrees == 90 || rotationDegrees == 270) imageHeight else imageWidth
    val effectiveImgHeight = if (rotationDegrees == 90 || rotationDegrees == 270) imageWidth else imageHeight

    // Calculate scale and offsets for center crop (FILL_CENTER)
    val scale = maxOf(viewWidth / effectiveImgWidth, viewHeight / effectiveImgHeight)
    val offsetX = (viewWidth - effectiveImgWidth * scale) / 2f
    val offsetY = (viewHeight - effectiveImgHeight * scale) / 2f

    val left: Float
    val top: Float
    val right: Float
    val bottom: Float

    // Map coordinates based on rotation
    when (rotationDegrees) {
        90 -> {
            left = (imageHeight - boundingBox.bottom) * scale + offsetX
            top = boundingBox.left * scale + offsetY
            right = (imageHeight - boundingBox.top) * scale + offsetX
            bottom = boundingBox.right * scale + offsetY
        }
        270 -> {
            left = boundingBox.top * scale + offsetX
            top = (imageWidth - boundingBox.right) * scale + offsetY
            right = boundingBox.bottom * scale + offsetX
            bottom = (imageWidth - boundingBox.left) * scale + offsetY
        }
        180 -> {
            left = (imageWidth - boundingBox.right) * scale + offsetX
            top = (imageHeight - boundingBox.bottom) * scale + offsetY
            right = (imageWidth - boundingBox.left) * scale + offsetX
            bottom = (imageHeight - boundingBox.top) * scale + offsetY
        }
        else -> { // 0
            left = boundingBox.left * scale + offsetX
            top = boundingBox.top * scale + offsetY
            right = boundingBox.right * scale + offsetX
            bottom = boundingBox.bottom * scale + offsetY
        }
    }

    return RectF(left, top, right, bottom)
}
