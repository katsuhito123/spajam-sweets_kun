package me.nontan.spajam_sweets_kun.models

data class ReviewCreateRequest(
        val shop_id: Int,
        val rating: Int,
        val review_text: String,
        val sweet_type: Int
)