package me.nontan.spajam_sweets_kun.utilities

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
