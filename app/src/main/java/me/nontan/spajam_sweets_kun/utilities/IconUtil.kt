package me.nontan.spajam_sweets_kun.utilities

import android.graphics.Bitmap
import me.nontan.spajam_sweets_kun.R

fun iconNumberToResource(iconNo: Int): Int {
    when(iconNo){
        0 -> {
            return  R.drawable.cake
        }
        1 -> {
            return R.drawable.crepe
        }
        2 -> {
            return R.drawable.icecream
        }
        3 -> {
            return R.drawable.kakigoori
        }
        4 -> {
            return R.drawable.pafe
        }
    }
    return R.drawable.cake
}

fun shrinkIcon(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    var targetWidth = 48
    var targetHeight = 48
    if (width > height) {
        targetHeight = (targetHeight.toDouble() * height.toDouble() / width.toDouble()).toInt()
    } else {
        targetWidth = (targetWidth.toDouble() * width.toDouble() / height.toDouble()).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
}
