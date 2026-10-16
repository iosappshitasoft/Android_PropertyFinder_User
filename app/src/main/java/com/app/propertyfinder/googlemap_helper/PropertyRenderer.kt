package com.app.propertyfinder.googlemap_helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.propertyfinder.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import androidx.core.graphics.createBitmap
import com.google.maps.android.clustering.Cluster

class PropertyRenderer(private val context: Context, map: GoogleMap,
    clusterManager: ClusterManager<PropertyClusterItem>
) : DefaultClusterRenderer<PropertyClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: PropertyClusterItem, markerOptions: MarkerOptions) {

        val icon = bitmapDescriptorFromVector(context, R.drawable.ic_map_marker,100,130)
        markerOptions.icon(icon).title(item.title)
    }


    override fun onBeforeClusterRendered(cluster: Cluster<PropertyClusterItem>, markerOptions: MarkerOptions) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_cluster_layout, null)
        val textView = view.findViewById<TextView>(R.id.cluster_text)

        textView.text = formatClusterCount(cluster.size)

        textView.setBackgroundResource(R.drawable.cluster_circle_bg)

        val icon = createBitmapFromView(view)

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<PropertyClusterItem>): Boolean {
        return cluster.size > 1
    }
    override fun getColor(clusterSize: Int): Int {
        return ContextCompat.getColor(context, R.color.splash_primary)
    }
    override fun getClusterTextAppearance(clusterSize: Int): Int {
        return 0
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, width: Int, height: Int): BitmapDescriptor {

    val drawable = ContextCompat.getDrawable(context, vectorResId)!!
    drawable.setBounds(0, 0, width, height)

    val bitmap = createBitmap(width, height)

    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun createBitmapFromView(view: View): Bitmap {

    view.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )

    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = createBitmap(view.measuredWidth, view.measuredHeight)
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    return bitmap
}

fun formatClusterCount1(count: Int): String {

    return when {
        count < 10 -> count.toString()

        count < 100 -> "${count}+"

        count < 1_000 -> "${count}+"

        count < 10_000 -> {
            val value = count / 1000.0
            if (value % 1 == 0.0)
                "${value.toInt()}k+"
            else
                String.format("%.1fk+", value)
        }

        count < 100_000 -> {
            val value = count / 1000.0
            String.format("%.1fk+", value)
        }

        else -> {
            val value = count / 1000.0
            String.format("%.1fk+", value)
        }
    }
}

fun formatClusterCount(count: Int): String {
    return when {
        count < 1_000 -> "${count}+"

        count < 1_000_000 -> {
            val value = count / 1000.0
            if (value % 1 == 0.0)
                "${value.toInt()}k+"
            else
                String.format("%.1fk+", value)
        }

        else -> {
            val value = count / 1_000_000.0
            if (value % 1 == 0.0)
                "${value.toInt()}M+"
            else
                String.format("%.1fM+", value)
        }
    }
}