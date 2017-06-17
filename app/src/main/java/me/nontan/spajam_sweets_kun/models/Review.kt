package me.nontan.spajam_sweets_kun.models

data class Review(
        val review_id: Int,
        val shop_id: Int,
        val rating: Int,
        val review_text: String,
        val sweet_type: Int,
        val latitude: Double,
        val longitude: Double
)