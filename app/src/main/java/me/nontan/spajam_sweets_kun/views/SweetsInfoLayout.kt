package me.nontan.spajam_sweets_kun.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import me.nontan.spajam_sweets_kun.R

class SweetsInfoLayout: LinearLayout {
    var shopNameTextView: TextView
    var reviewTextView: TextView

    constructor(context: Context): super(context) {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.sweets_info_layout, this)

        shopNameTextView = findViewById(R.id.shopNameTextView) as TextView
        reviewTextView = findViewById(R.id.reviewTextView) as TextView
    }
}
