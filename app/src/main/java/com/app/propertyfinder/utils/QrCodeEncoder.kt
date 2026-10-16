package com.app.propertyfinder.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeEncoder {

    fun encodeAsImageBitmap(content: String, sizePx: Int = 512, drawLogo: Boolean = true): ImageBitmap? {
        if (content.isBlank() || sizePx <= 0) return null
        return try {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                hints
            )
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] =
                        if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)

            if (drawLogo) {
                val canvas = Canvas(bitmap)
                val cx = w / 2f
                val cy = h / 2f

                // 1. Draw outer white circle (the background card/border)
                val bgRadius = w * 0.11f // about 56 pixels for 512 size
                val bgPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawCircle(cx, cy, bgRadius, bgPaint)

                // 2. Draw thin red/grey border on the outer edge of the white circle
                val borderPaint = Paint().apply {
                    color = Color.parseColor("#EA3934") // Red border
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    isAntiAlias = true
                }
                canvas.drawCircle(cx, cy, bgRadius, borderPaint)

                // 3. Draw inner green circle
                val greenRadius = bgRadius * 0.75f // about 42 pixels
                val greenPaint = Paint().apply {
                    color = Color.parseColor("#00A663") // Green color matching greencricle
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawCircle(cx, cy, greenRadius, greenPaint)

                // 4. Draw simplified white palm tree inside the green circle
                val treePaint = Paint().apply {
                    color = Color.WHITE
                    strokeWidth = 3f
                    strokeCap = Paint.Cap.ROUND
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                }

                // Trunk
                val trunkBottom = cy + greenRadius * 0.6f
                val trunkTop = cy - greenRadius * 0.2f
                canvas.drawLine(cx, trunkBottom, cx, trunkTop, treePaint)

                // Leaves (6 leaves curving outwards)
                val leafPaint = Paint(treePaint).apply {
                    strokeWidth = 2.5f
                }

                val path = Path()

                // Left top leaf
                path.reset()
                path.moveTo(cx, trunkTop)
                path.quadTo(cx - greenRadius * 0.4f, trunkTop - greenRadius * 0.3f, cx - greenRadius * 0.6f, trunkTop)
                canvas.drawPath(path, leafPaint)

                // Right top leaf
                path.reset()
                path.moveTo(cx, trunkTop)
                path.quadTo(cx + greenRadius * 0.4f, trunkTop - greenRadius * 0.3f, cx + greenRadius * 0.6f, trunkTop)
                canvas.drawPath(path, leafPaint)

                // Left middle leaf
                path.reset()
                path.moveTo(cx, trunkTop + greenRadius * 0.1f)
                path.quadTo(cx - greenRadius * 0.5f, trunkTop - greenRadius * 0.1f, cx - greenRadius * 0.7f, trunkTop + greenRadius * 0.2f)
                canvas.drawPath(path, leafPaint)

                // Right middle leaf
                path.reset()
                path.moveTo(cx, trunkTop + greenRadius * 0.1f)
                path.quadTo(cx + greenRadius * 0.5f, trunkTop - greenRadius * 0.1f, cx + greenRadius * 0.7f, trunkTop + greenRadius * 0.2f)
                canvas.drawPath(path, leafPaint)

                // Left bottom leaf
                path.reset()
                path.moveTo(cx, trunkTop + greenRadius * 0.3f)
                path.quadTo(cx - greenRadius * 0.4f, trunkTop + greenRadius * 0.2f, cx - greenRadius * 0.5f, trunkTop + greenRadius * 0.5f)
                canvas.drawPath(path, leafPaint)

                // Right bottom leaf
                path.reset()
                path.moveTo(cx, trunkTop + greenRadius * 0.3f)
                path.quadTo(cx + greenRadius * 0.4f, trunkTop + greenRadius * 0.2f, cx + greenRadius * 0.5f, trunkTop + greenRadius * 0.5f)
                canvas.drawPath(path, leafPaint)
            }

            bitmap.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }
}
