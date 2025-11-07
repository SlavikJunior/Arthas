package com.example.arthas.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

class IconResource private constructor(
    @DrawableRes
    private val _resID: Int?,
    private val _imageVector: ImageVector?
) {

    fun getResID(): Int? = _resID
    fun getImageVector(): ImageVector? = _imageVector

    @Composable
    fun asPainterResource(): Painter {
        _resID?.let {
            return painterResource(id = _resID)
        }
        _imageVector?.let {
            return rememberVectorPainter(image = _imageVector)
        }
        throw IllegalStateException("Both resID and imageVector are null")
    }

    companion object {
        fun fromDrawableResource(@DrawableRes resID: Int): IconResource {
            return IconResource(resID, null)
        }

        fun fromImageVector(imageVector: ImageVector): IconResource {
            return IconResource(null, imageVector)
        }
    }
}