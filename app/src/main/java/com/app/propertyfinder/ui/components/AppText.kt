package com.app.propertyfinder.ui.components

import android.R.attr.lineHeight
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary

@Composable
fun AppTextRegular(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        fontStyle = FontStyle.Normal,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,

    )
}

@Composable
fun AppTextLight(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextSecondary,
    fontSize: TextUnit = 12.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W400,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun AppTextMedium(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontSize: TextUnit = 14.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W500,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun AppTextSemiBold(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W600,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun AppTextBold(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontSize: TextUnit = 20.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.W700,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    colorResId: Int = R.color.textprimary,
    fontSizeSp: Float = 14f,
    maxLines: Int = Int.MAX_VALUE,
    lineSpacingExtra: Float = 0f
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context ->
            android.widget.TextView(context).apply {
                textSize = fontSizeSp
                setTextColor(context.getColor(colorResId))
                val tf = androidx.core.content.res.ResourcesCompat.getFont(context, R.font.font_regular)
                typeface = tf
                if (maxLines != Int.MAX_VALUE) {
                    this.maxLines = maxLines
                    ellipsize = android.text.TextUtils.TruncateAt.END
                }
                setLineSpacing(lineSpacingExtra, 1.0f)
            }
        },
        update = { textView ->
            val parsed = androidx.core.text.HtmlCompat.fromHtml(
                html,
                androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            var end = parsed.length
            while (end > 0 && parsed[end - 1].isWhitespace()) {
                end--
            }
            textView.text = parsed.subSequence(0, end)
        },
        modifier = modifier
    )
}

