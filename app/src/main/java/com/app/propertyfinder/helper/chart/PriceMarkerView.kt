package com.app.propertyfinder.helper.chart

import android.content.Context
import android.view.View
import android.widget.TextView
import com.app.propertyfinder.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class PriceMarkerView(
    context: Context,
    private val months: List<String>,
    private val redValues: List<Float>,
    private val blueValues: List<Float>,
    private val from: String
) : MarkerView(context, R.layout.chart_marker_view) {

    private val monthText: TextView = findViewById(R.id.monthText)
    private val redValue: TextView = findViewById(R.id.redValue)
    private val blueValue: TextView = findViewById(R.id.blueValue)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        val index = e?.x?.toInt() ?: 0

        if (index < months.size) {

            monthText.text = months[index]
            redValue.text = "Palm Jumeirah @${redValues[index]} AED"
            if (from == "explore"){
                blueValue.text = "Abu Dhabi @${blueValues[index]} AED/Sqft"
            } else if (from == "residence"){
                blueValue.visibility = GONE
            }
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}