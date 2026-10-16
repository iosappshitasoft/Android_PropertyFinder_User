package com.app.propertyfinder.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import com.app.propertyfinder.ui.components.AppSwitch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.DividerColor
import com.app.propertyfinder.ui.theme.OTPVerifiedBtn
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.MolumulkAccentCoral
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import androidx.compose.ui.text.font.FontWeight
import com.app.propertyfinder.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.api.dto.response.ListingType
import com.app.propertyfinder.api.dto.response.PropertyType
import com.app.propertyfinder.api.dto.response.alertFrequencies
import com.app.propertyfinder.api.dto.response.amenities
import com.app.propertyfinder.ui.components.AppEditText
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.BarActive
import com.app.propertyfinder.ui.theme.BarInActive
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.TextSecondary
import java.util.Locale
import kotlin.text.category
import kotlin.text.clear

/** Order matches typical API `category` values (basic, safety, outdoor, …). */
private val AMENITY_CATEGORY_ORDER = listOf(
    "basic", "safety", "outdoor", "indoor", "luxury", "other"
)

private fun formatAmenityCategoryHeader(categoryKey: String): String {
    if (categoryKey.isBlank()) return "Other"
    return categoryKey.split("-").joinToString(" ") { word ->
        word.replaceFirstChar { c ->
            if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
        }
    }
}

/**
 * Groups amenities by [amenities.category], sorts categories, sorts items by [amenities.displayOrder].
 */
private fun buildAmenitySections(items: List<amenities>): List<Pair<String, List<amenities>>> {
    val grouped = items.groupBy { it.category?.lowercase(Locale.getDefault()) ?: "" }
    return grouped.entries
        .sortedBy { (key, _) ->
            val idx = AMENITY_CATEGORY_ORDER.indexOf(key)
            if (idx >= 0) idx else AMENITY_CATEGORY_ORDER.size + key.hashCode() % 10_000
        }
        .map { (key, list) ->
            key to list.sortedBy { it.displayOrder ?: Int.MAX_VALUE }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterBottomSheet(
    listingTypes: List<ListingType>,
    initialSelectedId: String?,
    initialShowCommercialOnly: Boolean,
    onDismiss: () -> Unit,
    onShowProperties: (ListingType, Boolean) -> Unit
) {
    if (listingTypes.isEmpty()) return

    // Switch is only linked to "Commercial Buy" — not "Commercial Rent"
    fun ListingType.isCommercialBuy() = name?.equals("commercial buy", ignoreCase = true) == true
    fun ListingType.isCommercial() = name?.contains("commercial", ignoreCase = true) == true

    val defaultId = initialSelectedId ?: listingTypes.firstOrNull()?._id
    var selectedId by remember(initialSelectedId, listingTypes) { mutableStateOf(defaultId) }
    var showCommercialOnly by remember(initialShowCommercialOnly) {
        mutableStateOf(initialShowCommercialOnly)
    }

    // Track the last non-Commercial-Buy item so we can revert to it when switch is turned OFF.
    // Commercial Rent is treated like a normal (non-switch) item for revert purposes.
    var lastNonCommercialBuyId by remember(initialSelectedId, listingTypes) {
        val initItem = listingTypes.firstOrNull { it._id == initialSelectedId }
        val fallback = if (initItem?.isCommercialBuy() == false) {
            initialSelectedId
        } else {
            listingTypes.firstOrNull { !it.isCommercialBuy() }?._id
        }
        mutableStateOf(fallback)
    }

    val selectedItem = listingTypes.firstOrNull { it._id == selectedId }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MolumulkBorderDivider)
                    )

                    AppSpacerV(20)

                    AppTextBold(
                        text = stringResource(R.string.category),
                        fontSize = 18.sp,
                        color = MolumulkCharcoal
                    )
                }
            }

            AppSpacerV(24)

            listingTypes.forEachIndexed { index, item ->
                val isSelected = selectedId == item._id
                val backgroundColor = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite
                val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
                val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .background(backgroundColor)
                        .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .clickable {
                            selectedId = item._id
                            if (item.isCommercialBuy()) {
                                // Only "Commercial Buy" turns the switch ON
                                showCommercialOnly = true
                            } else {
                                // All other items (including Commercial Rent) turn switch OFF
                                // and update the revert target
                                showCommercialOnly = false
                                lastNonCommercialBuyId = item._id
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextSemiBold(
                        text = item.name.orEmpty(),
                        color = textColor,
                        fontSize = 14.sp
                    )
                }

                if (index != listingTypes.lastIndex) {
                    AppSpacerV(10)
                }
            }

            AppSpacerV(20)

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MolumulkBorderDivider
            )

            AppSpacerV(16)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextSemiBold(
                    text = "View commercial properties only",
                    color = MolumulkCharcoal,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                AppSpacerH(20)
                AppSwitch(
                    checked = showCommercialOnly,
                    onCheckedChange = { isChecked ->
                        showCommercialOnly = isChecked
                        if (isChecked) {
                            // Switch ON → select "Commercial Buy" specifically
                            val commercialBuy = listingTypes.firstOrNull { it.isCommercialBuy() }
                            if (commercialBuy != null) selectedId = commercialBuy._id
                        } else {
                            // Switch OFF → revert to last non-Commercial-Buy selection
                            val revertId = lastNonCommercialBuyId
                                ?: listingTypes.firstOrNull { !it.isCommercialBuy() }?._id
                            if (revertId != null) selectedId = revertId
                        }
                    }
                )
            }

            AppSpacerV(28)

            PrimaryRoundButton(
                text = stringResource(R.string.show_properties),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight),
                containerColor = MolumulkPrimaryCoralRed,
                cornerRadius = MolumulkDimens.PrimaryCtaRadius
            ) {
                selectedItem?.let {
                    onShowProperties(it, showCommercialOnly)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTypeBottomSheet(
    propertyTypes: List<PropertyType>,
    initialSelectedId: String?, // changed
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (PropertyType?) -> Unit // changed
) {
    var selectedId by remember(initialSelectedId) { mutableStateOf(initialSelectedId) } // changed

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MolumulkBorderDivider)
                    )

                    AppSpacerV(20)

                    AppTextBold(
                        text = stringResource(R.string.property_type),
                        fontSize = 18.sp,
                        color = MolumulkCharcoal
                    )
                }
            }

            AppSpacerV(24)

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                propertyTypes.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            val itemId = item._id.orEmpty()
                            val isSelected = selectedId == itemId // changed
                            PropertyTypeChip(
                                text = item.name.orEmpty(),
                                isSelected = isSelected,
                                onClick = {
                                    selectedId = if (isSelected) null else itemId // changed
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val hasSelection = !selectedId.isNullOrEmpty()
                AppTextBold(
                    text = stringResource(R.string.clear),
                    color = if (hasSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedId = null // changed
                        onClear()
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = stringResource(R.string.show_properties),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight),
                containerColor = MolumulkPrimaryCoralRed,
                cornerRadius = MolumulkDimens.PrimaryCtaRadius
            ) {
                val selectedItem = propertyTypes.find { it._id == selectedId } // changed
                onShowProperties(selectedItem) // changed
            }
        }
    }
}
@Composable
fun PropertyTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextSemiBold(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceBottomSheet(
    minPrice: Float,
    maxPrice: Float,
    initialRange: ClosedFloatingPointRange<Float>,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
    ) {
        PriceBottomSheetContent(
            minPrice = minPrice,
            maxPrice = maxPrice,
            initialRange = initialRange,
            onClear = onClear,
            onShowProperties = onShowProperties
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceBottomSheetContent(
    minPrice: Float,
    maxPrice: Float,
    initialRange: ClosedFloatingPointRange<Float>,
    onClear: () -> Unit,
    onShowProperties: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val fullRange = minPrice..maxPrice
    var priceRange by remember(initialRange) { mutableStateOf(initialRange) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MolumulkBorderDivider)
                )

                AppSpacerV(20)

                AppTextBold(
                    text = stringResource(R.string.price),
                    fontSize = 18.sp,
                    color = MolumulkCharcoal
                )
            }
        }

        AppSpacerV(24)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            PriceBarChart(
                minPrice = minPrice,
                maxPrice = maxPrice,
                selectedRange = priceRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(bottom = 20.dp) // Offset bars to meet slider track center
            )

            CustomRangeSlider(
                min = minPrice,
                max = maxPrice,
                start = priceRange.start,
                end = priceRange.endInclusive,
                onValueChange = { startValue, endValue ->
                    priceRange = startValue..endValue
                }
            )
        }

        AppSpacerV(20)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = MolumulkBorderDivider,
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppTextMedium(
                        text = stringResource(R.string.minimum),
                        color = MolumulkMutedSlate,
                        fontSize = 12.sp
                    )
                    AppSpacerV(4)
                    AppTextSemiBold(
                        text = "${priceRange.start.toInt()} AED",
                        color = MolumulkCharcoal,
                        fontSize = 14.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = MolumulkBorderDivider,
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppTextMedium(
                        text = stringResource(R.string.maximum),
                        color = MolumulkMutedSlate,
                        fontSize = 12.sp
                    )
                    AppSpacerV(4)
                    AppTextSemiBold(
                        text = "${priceRange.endInclusive.toInt()} AED",
                        color = MolumulkCharcoal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        AppSpacerV(20)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val hasPriceSelection = priceRange != fullRange
            AppTextBold(
                text = stringResource(R.string.clear),
                color = if (hasPriceSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasPriceSelection) {
                    priceRange = fullRange
                    onClear()
                }
            )
        }

        AppSpacerV(25)

        PrimaryRoundButton(
            text = stringResource(R.string.show_properties),
            modifier = Modifier
                .fillMaxWidth()
                .height(MolumulkDimens.PrimaryButtonHeight),
            containerColor = MolumulkPrimaryCoralRed,
            cornerRadius = MolumulkDimens.PrimaryCtaRadius
        ) {
            onShowProperties(priceRange)
        }
    }
}

@Composable
fun CustomRangeSlider(
    min: Float,
    max: Float,
    start: Float,
    end: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float, Float) -> Unit
) {
    val currentStart by rememberUpdatedState(start)
    val currentEnd by rememberUpdatedState(end)
    var isDraggingStart by remember { mutableStateOf(false) }
    var isDraggingEnd by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp) // Increased height to accommodate bubbles
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val thumbWidthDp = 37.dp
        val thumbHeightDp = 27.dp
        val thumbWidthPx = with(LocalDensity.current) { thumbWidthDp.toPx() }
        val thumbHeightPx = with(LocalDensity.current) { thumbHeightDp.toPx() }

        val circleOffsetPx = with(LocalDensity.current) { 13.5.dp.toPx() }
        val rangeWidthPx = widthPx - (2 * circleOffsetPx)

        // Use the parameters directly for the UI
        val startX = ((start - min) / (max - min) * rangeWidthPx)
        val endX = ((end - min) / (max - min) * rangeWidthPx)

        val trackYFromBottom = with(LocalDensity.current) { 20.dp.toPx() }

        Canvas(modifier = Modifier.matchParentSize()) {
            val trackY = size.height - trackYFromBottom

            drawLine(
                color = Color.Black.copy(alpha = 0.1f),
                start = Offset(circleOffsetPx, trackY),
                end = Offset(widthPx - circleOffsetPx, trackY),
                strokeWidth = 2.dp.toPx()
            )

            if (endX > startX + thumbWidthPx) {
                drawLine(
                    color = Color.Black,
                    start = Offset(startX + thumbWidthPx, trackY),
                    end = Offset(endX, trackY),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }

        // Helper to format price
        val formatPrice: (Float) -> String = { "%,d AED".format(it.toInt()) }

        // START THUMB VALUE BUBBLE
        if (isDraggingStart) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset((startX + thumbWidthPx / 2).toInt(), 0)
                    }
                    .offset(x = (-60).dp) // Offset to center a potentially wider bubble
                    .width(120.dp)
                    .height(with(LocalDensity.current) {
                        (constraints.maxHeight - trackYFromBottom - thumbHeightPx / 2 - 30.dp.toPx()).toDp()
                    }),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF222222), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextMedium(
                        text = formatPrice(start),
                        color = Color.White,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // START THUMB
        Image(
            painter = painterResource(id = R.drawable.ic_startthumb),
            contentDescription = null,
            modifier = Modifier
                .offset {
                    val scale = if (isDraggingStart) 1.2f else 1f
                    IntOffset(
                        (startX - (thumbWidthPx * (scale - 1f) / 2)).toInt(),
                        (constraints.maxHeight - trackYFromBottom - (thumbHeightPx * scale) / 2).toInt()
                    )
                }
                .size(
                    width = if (isDraggingStart) thumbWidthDp * 1.2f else thumbWidthDp,
                    height = if (isDraggingStart) thumbHeightDp * 1.2f else thumbHeightDp
                )
                .pointerInput(rangeWidthPx, min, max) {
                    detectDragGestures(
                        onDragStart = { isDraggingStart = true },
                        onDragEnd = { isDraggingStart = false },
                        onDragCancel = { isDraggingStart = false }
                    ) { change, dragAmount ->
                        change.consume()
                        val delta = dragAmount.x / rangeWidthPx * (max - min)
                        onValueChange((currentStart + delta).coerceIn(min, currentEnd), currentEnd)
                    }
                }
        )

        // END THUMB VALUE BUBBLE
        if (isDraggingEnd) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset((endX + thumbWidthPx / 2).toInt(), 0)
                    }
                    .offset(x = (-60).dp)
                    .width(120.dp)
                    .height(with(LocalDensity.current) {
                        (constraints.maxHeight - trackYFromBottom - thumbHeightPx / 2 - 30.dp.toPx()).toDp()
                    }),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF222222), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextMedium(
                        text = formatPrice(end),
                        color = Color.White,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // END THUMB
        Image(
            painter = painterResource(id = R.drawable.ic_endthumb),
            contentDescription = null,
            modifier = Modifier
                .offset {
                    val scale = if (isDraggingEnd) 1.2f else 1f
                    IntOffset(
                        (endX - (thumbWidthPx * (scale - 1f) / 2)).toInt(),
                        (constraints.maxHeight - trackYFromBottom - (thumbHeightPx * scale) / 2).toInt()
                    )
                }
                .size(
                    width = if (isDraggingEnd) thumbWidthDp * 1.2f else thumbWidthDp,
                    height = if (isDraggingEnd) thumbHeightDp * 1.2f else thumbHeightDp
                )
                .pointerInput(rangeWidthPx, min, max) {
                    detectDragGestures(
                        onDragStart = { isDraggingEnd = true },
                        onDragEnd = { isDraggingEnd = false },
                        onDragCancel = { isDraggingEnd = false }
                    ) { change, dragAmount ->
                        change.consume()
                        val delta = dragAmount.x / rangeWidthPx * (max - min)
                        onValueChange(currentStart, (currentEnd + delta).coerceIn(currentStart, max))
                    }
                }
        )
    }
}

@Composable
fun PriceBarChart(
    minPrice: Float,
    maxPrice: Float,
    selectedRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFFFCBBBD),
    inactiveColor: Color = Color(0xFFE7ECF4)
) {
    val barCount = 55
    val barWidth = (maxPrice - minPrice) / barCount

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val margin = 13.5.dp.toPx()
        val barSpacing = 2.dp.toPx()
        val availableWidth = canvasWidth - (2 * margin) - (barSpacing * (barCount - 1))
        val individualBarWidth = availableWidth / barCount

        val random = kotlin.random.Random(42)
        val barHeights = List(barCount) { random.nextFloat() * 0.8f + 0.2f }

        for (i in 0 until barCount) {
            val barStart = minPrice + (i * barWidth)
            val barEnd = barStart + barWidth
            val isInRange = barStart < selectedRange.endInclusive && barEnd > selectedRange.start
            val barHeight = barHeights[i] * canvasHeight

            val x = margin + i * (individualBarWidth + barSpacing)
            val y = canvasHeight - barHeight
            val radius = 10.dp.toPx()

            drawRoundRect(
                color = if (isInRange) activeColor else inactiveColor,
                topLeft = Offset(x, y),
                size = Size(individualBarWidth, barHeight),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedsAndBathsBottomSheet(
    initialBeds: String?,
    initialBaths: String?,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (String?, String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
    ) {
        BedsAndBathsBottomSheetContent(
            initialBeds = initialBeds,
            initialBaths = initialBaths,
            onClear = onClear,
            onShowProperties = onShowProperties
        )
    }
}

@Composable
private fun BedsAndBathsBottomSheetContent(
    initialBeds: String?,
    initialBaths: String?,
    onClear: () -> Unit,
    onShowProperties: (String?, String?) -> Unit
) {
    var selectedBeds by remember(initialBeds) { mutableStateOf(initialBeds) }
    var selectedBaths by remember(initialBaths) { mutableStateOf(initialBaths) }

    val bedOptions = listOf("Studio", "1", "2", "3", "4", "5", "6", "7", "7+")
    val bathOptions = listOf("1", "2", "3", "4", "5", "6", "7", "7+")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MolumulkBorderDivider)
                )

                AppSpacerV(20)

                AppTextBold(
                    text = stringResource(R.string.beds_baths),
                    fontSize = 18.sp,
                    color = MolumulkCharcoal
                )
            }
        }

        AppSpacerV(24)

        AppTextBold(
            text = stringResource(R.string.beds),
            fontSize = 15.sp,
            color = MolumulkCharcoal
        )

        AppSpacerV(15)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bedOptions.chunked(4).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowOptions.forEach { label ->
                        val value = if (label == "Studio") "0" else label
                        val isSelected = selectedBeds == value
                        BedsBathsChip(
                            text = label,
                            isSelected = isSelected,
                            onClick = {
                                selectedBeds = if (isSelected) null else value
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowOptions.size < 4) {
                        repeat(4 - rowOptions.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        AppSpacerV(25)

        AppTextBold(
            text = stringResource(R.string.baths),
            fontSize = 15.sp,
            color = MolumulkCharcoal
        )

        AppSpacerV(15)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bathOptions.chunked(4).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowOptions.forEach { label ->
                        BedsBathsChip(
                            text = label,
                            isSelected = selectedBaths == label,
                            onClick = {
                                selectedBaths = if (selectedBaths == label) null else label
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        AppSpacerV(20)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val hasBedOrBathSelection = selectedBeds != null || selectedBaths != null
            AppTextBold(
                text = stringResource(R.string.clear),
                color = if (hasBedOrBathSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasBedOrBathSelection) {
                    selectedBeds = null
                    selectedBaths = null
                    onClear()
                }
            )
        }

        AppSpacerV(25)

        PrimaryRoundButton(
            text = stringResource(R.string.show_properties),
            modifier = Modifier
                .fillMaxWidth()
                .height(MolumulkDimens.PrimaryButtonHeight),
            containerColor = MolumulkPrimaryCoralRed,
            cornerRadius = MolumulkDimens.PrimaryCtaRadius
        ) {
            onShowProperties(selectedBeds, selectedBaths)
        }
    }
}

@Composable
fun BedsBathsChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextSemiBold(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            maxLines = 1
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmenitiesBottomSheet(
    amenities: List<amenities>,
    initialSelectedIds: Set<String>,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (List<amenities>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
    ) {
        AmenitiesBottomSheetContent(
            amenities = amenities,
            initialSelectedIds = initialSelectedIds,
            onClear = onClear,
            onShowProperties = onShowProperties
        )
    }
}

@Composable
private fun AmenitiesBottomSheetContent(
    amenities: List<amenities>,
    initialSelectedIds: Set<String>,
    onClear: () -> Unit,
    onShowProperties: (List<amenities>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedIds by remember(initialSelectedIds) { mutableStateOf(initialSelectedIds) }

    val filteredAmenities = remember(searchText, amenities) {
        if (searchText.isBlank()) {
            amenities
        } else {
            amenities.filter {
                it.name.orEmpty().contains(searchText, ignoreCase = true)
            }
        }
    }

    val amenitySections = remember(filteredAmenities) {
        buildAmenitySections(filteredAmenities)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MolumulkBorderDivider)
                )

                AppSpacerV(20)

                AppTextBold(
                    text = stringResource(R.string.amenities),
                    fontSize = 18.sp,
                    color = MolumulkCharcoal
                )
            }
        }

        AppSpacerV(20)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
            color = Color.White,
            border = BorderStroke(1.dp, MolumulkBorderDivider)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppImage(
                    R.drawable.search,
                    modifier = Modifier.size(20.dp),
                    tint = MolumulkPrimaryCoralRed
                )

                AppSpacerH(width = 8)

                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = MolumulkCharcoal,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchText.isEmpty()) {
                                AppTextRegular(
                                    text = stringResource(R.string.search_amenities),
                                    color = MolumulkMutedSlate,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        AppSpacerV(16)

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            amenitySections.forEachIndexed { index, (categoryKey, sectionItems) ->
                item(key = "amenity_header_$categoryKey") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AppTextBold(
                            text = formatAmenityCategoryHeader(categoryKey),
                            fontSize = 15.sp,
                            color = MolumulkCharcoal,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                }
                items(
                    items = sectionItems,
                    key = { it._id.orEmpty() }
                ) { item ->
                    val itemId = item._id.orEmpty()
                    val isSelected = selectedIds.contains(itemId)

                    AmenityItem(
                        name = item.name.orEmpty(),
                        iconUrl = "https://d1dp1oh0ra5b0z.cloudfront.net/img/amenities/${item.image}",
                        isSelected = isSelected,
                        onClick = {
                            selectedIds = if (isSelected) {
                                selectedIds - itemId
                            } else {
                                selectedIds + itemId
                            }
                        }
                    )
                    AppSpacerV(10)
                }
            }
        }

        AppSpacerV(16)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val hasAmenitiesSelection = selectedIds.isNotEmpty()
            AppTextBold(
                text = stringResource(R.string.clear),
                color = if (hasAmenitiesSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasAmenitiesSelection) {
                    selectedIds = emptySet()
                    searchText = ""
                    onClear()
                }
            )
        }

        AppSpacerV(20)

        PrimaryRoundButton(
            text = stringResource(R.string.show_properties),
            modifier = Modifier
                .fillMaxWidth()
                .height(MolumulkDimens.PrimaryButtonHeight),
            containerColor = MolumulkPrimaryCoralRed,
            cornerRadius = MolumulkDimens.PrimaryCtaRadius
        ) {
            val selectedItems = amenities.filter { selectedIds.contains(it._id) }
            onShowProperties(selectedItems)
        }
    }
}

@Composable
fun AmenityItem(name: String, iconUrl: String?, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal
    val iconTint = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal
    val iconBg = if (isSelected) Color.White else Color(0xFFF5F5F5)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = iconUrl,
                contentDescription = name,
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(iconTint),
                loading = {
                    AppImage(
                        resId = R.drawable.ic_placeholder,
                        contentDescription = name,
                        modifier = Modifier.size(18.dp),
                        tint = iconTint
                    )
                },
                error = {
                    AppImage(
                        resId = R.drawable.search,
                        contentDescription = name,
                        modifier = Modifier.size(18.dp),
                        tint = iconTint
                    )
                }
            )
        }

        AppSpacerH(12)

        AppTextSemiBold(
            text = name,
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            AppImage(
                resId = R.drawable.tick,
                contentDescription = "Selected",
                tint = MolumulkPrimaryCoralRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Composable
private fun CreateAlertDialogContent(onDismiss: () -> Unit, onCreateAlert: (alertName: String, email: String) -> Unit) {
    var alertName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppTextSemiBold(
                text = stringResource(R.string.create_alert),
                fontSize = 20.sp,
                color = TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Alert Name Input
                Column {
                    AppTextMedium(
                        text = stringResource(R.string.alert_name),
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    AppSpacerV(8)
                    AppEditText(
                        hint = stringResource(R.string.enter_alert_name),
                        value = alertName,
                        onValueChange = { alertName = it }
                    )
                }

                // Email Input
                Column {
                    AppTextMedium(
                        text = stringResource(R.string.email_address),
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    AppSpacerV(8)
                    AppEditText(
                        hint = stringResource(R.string.enter_email_address),
                        value = email,
                        onValueChange = { email = it }
                    )
                }
            }
        },
        confirmButton = {
            PrimaryRoundButton(
                text = stringResource(R.string.create_alert),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (alertName.isNotBlank() && email.isNotBlank()) {
                    onCreateAlert(alertName, email)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                border = BorderStroke(1.dp, DividerColor)
            ) {
                AppTextMedium(
                    text = stringResource(R.string.cancel),
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertBottomSheet(
    showBottomSheet: Boolean,
    alertfrequencies: List<alertFrequencies>,
    selectedFrequencyValue: String,
    onDismiss: () -> Unit,
    onCreateAlert: (String, String) -> Unit,
    snackbarManager: com.app.propertyfinder.utils.SnackbarManager? = null
) {
    var alertName by remember { mutableStateOf("") }
    var alertNameError by remember { mutableStateOf("") }

    var selectedFrequencyValueState by remember(selectedFrequencyValue, alertfrequencies) {
        mutableStateOf(
            selectedFrequencyValue.ifBlank {
                alertfrequencies.firstOrNull()?.value.orEmpty()
            }
        )
    }

    var expanded by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius)
        ) {
            CreateAlertContent(
                alertName = alertName,
                onAlertNameChange = {
                    alertName = it
                    if (it.isNotBlank()) alertNameError = ""
                },
                alertNameError = alertNameError,
                alertfrequencies = alertfrequencies,
                selectedFrequencyValue = selectedFrequencyValueState,
                onFrequencyChange = { selectedFrequencyValueState = it },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onCreateClick = {
                    if (alertName.isNotBlank()) {
                        onCreateAlert(alertName, selectedFrequencyValueState)
                        onDismiss()
                    } else {
                        alertNameError = "Kindly enter the alert name"
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertContent(
    alertName: String,
    onAlertNameChange: (String) -> Unit,
    alertNameError: String,
    alertfrequencies: List<alertFrequencies>,
    selectedFrequencyValue: String,
    onFrequencyChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateClick: () -> Unit
) {
    val selectedFrequencyName = alertfrequencies
        .firstOrNull { it.value == selectedFrequencyValue }
        ?.name
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MolumulkBorderDivider)
                )

                AppSpacerV(20)

                AppTextBold(
                    text = stringResource(R.string.create_alert),
                    fontSize = 18.sp,
                    color = MolumulkCharcoal
                )
            }
        }

        AppSpacerV(24)

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AppTextSemiBold(
                text = "Alert name",
                fontSize = 14.sp,
                color = MolumulkCharcoal
            )

            AppSpacerV(height = 8)

            OutlinedTextField(
                value = alertName,
                onValueChange = onAlertNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MolumulkCharcoal
                ),
                placeholder = {
                    AppTextRegular(
                        text = "Properties for rent in UAE",
                        color = MolumulkMutedSlate,
                        fontSize = 14.sp
                    )
                },
                isError = alertNameError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MolumulkPrimaryCoralRed,
                    unfocusedBorderColor = MolumulkBorderDivider,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MolumulkPrimaryCoralRed,
                    errorBorderColor = MolumulkAccentCoral
                ),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )

            if (alertNameError.isNotEmpty()) {
                AppSpacerV(height = 6)
                AppTextRegular(
                    text = alertNameError,
                    color = MolumulkAccentCoral,
                    fontSize = 12.sp
                )
            }

            AppSpacerV(height = 20)

            AppTextSemiBold(
                text = "Receive updates",
                fontSize = 14.sp,
                color = MolumulkCharcoal
            )

            AppSpacerV(height = 8)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedFrequencyName,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MolumulkCharcoal
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = null,
                            tint = MolumulkCharcoal
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MolumulkPrimaryCoralRed,
                        unfocusedBorderColor = MolumulkBorderDivider,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                )

                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(
                        surface = Color.White,
                        surfaceTint = Color.Transparent
                    )
                ) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) },
                        modifier = Modifier.background(Color.White)
                    ) {
                        alertfrequencies.forEach { frequency ->
                            DropdownMenuItem(
                                text = {
                                    AppTextMedium(
                                        text = frequency.name.orEmpty(),
                                        color = MolumulkCharcoal,
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier.background(Color.White),
                                onClick = {
                                    onFrequencyChange(frequency.value.orEmpty())
                                    onExpandedChange(false)
                                }
                            )
                        }
                    }
                }
            }

            AppSpacerV(height = 28)

            PrimaryRoundButton(
                text = stringResource(R.string.create),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight),
                containerColor = MolumulkPrimaryCoralRed,
                cornerRadius = MolumulkDimens.PrimaryCtaRadius
            ) {
                onCreateClick()
            }
        }
    }
}


