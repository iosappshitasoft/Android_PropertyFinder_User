package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.ProjectDetailItem
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.HtmlText
import com.app.propertyfinder.ui.theme.Background
import java.util.Locale

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProjectDetailsScreenPreview() {
    val navController = rememberNavController()
    ProjectDetailsScreen(navController = navController, projectId = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    navController: NavController,
    projectId: String?,
    propertyId: String? = null,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val projectDetailsState by propertyViewModel.projectDetailsState.collectAsStateWithLifecycle()
    val propertyDetailsState by propertyViewModel.propertyDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(projectId, propertyId) {
        projectId?.takeIf { it.isNotBlank() }?.let {
            propertyViewModel.getProjectDetails(it)
        }
        propertyId?.takeIf { it.isNotBlank() }?.let {
            propertyViewModel.getPropertyDetails(it) { }
        }
    }

    val project = projectDetailsState.data?.data?.project
    val property = propertyDetailsState.data?.data?.property

    fun getAreaRangeString(projectDetailItem: ProjectDetailItem): String? {
        val layouts = projectDetailItem.unitsFromDeveloper.flatMap { it.layouts }
        val areaSqftList = layouts.mapNotNull { it.areaSqft }
        val areaSqmList = layouts.mapNotNull { it.areaSqm }
        
        fun formatDouble(d: Double): String {
            return if (d % 1.0 == 0.0) d.toInt().toString() else d.toString()
        }
        
        val sqftStr = if (areaSqftList.isNotEmpty()) {
            val minSqft = areaSqftList.minOrNull()
            val maxSqft = areaSqftList.maxOrNull()
            if (minSqft != null && maxSqft != null) {
                if (minSqft == maxSqft) {
                    "${formatDouble(minSqft)} sqft"
                } else {
                    "${formatDouble(minSqft)} - ${formatDouble(maxSqft)} sqft"
                }
            } else null
        } else null

        val sqmStr = if (areaSqmList.isNotEmpty()) {
            val minSqm = areaSqmList.minOrNull()
            val maxSqm = areaSqmList.maxOrNull()
            if (minSqm != null && maxSqm != null) {
                if (minSqm == maxSqm) {
                    "${formatDouble(minSqm)} sqm"
                } else {
                    "${formatDouble(minSqm)} - ${formatDouble(maxSqm)} sqm"
                }
            } else null
        } else null

        return if (!sqftStr.isNullOrBlank() && !sqmStr.isNullOrBlank()) {
            "$sqftStr / $sqmStr"
        } else {
            sqftStr ?: sqmStr
        }
    }

    val description = project?.description?.takeIf { it.isNotBlank() }
        ?: project?.aboutProject?.takeIf { it.isNotBlank() }
        ?: property?.description?.takeIf { it.isNotBlank() }
        ?: ""

    val propertyFeatures = if (project != null) {
        listOfNotNull(
            project.keyInformation?.propertyTypes?.takeIf { it.isNotBlank() }?.let { "It's a $it" },
            getAreaRangeString(project),
            project.projectType?.takeIf { it.isNotBlank() }?.let { type ->
                type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
        )
    } else if (property != null) {
        listOfNotNull(
            property.propertyType?.name?.takeIf { it.isNotBlank() }?.let { "It's a $it" },
            property.area?.sqft?.let { "$it sqft" } ?: property.area?.sqm?.let { "$it sqm" },
            property.completionStatus?.takeIf { it.isNotBlank() }?.let { status ->
                status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            },
            property.furnishedStatus?.takeIf { it.isNotBlank() }?.let { status ->
                status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
        )
    } else emptyList()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = if (property != null) "Property details" else stringResource(R.string.project_details),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (projectDetailsState.isLoading || propertyDetailsState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorResource(R.color.darkBlue))
                    }
                }
            } else {
                // 🔹 First Paragraph (Description)
                if (description.isNotBlank()) {
                    item {
                        HtmlText(
                            html = description,
                            colorResId = R.color.textlight,
                            fontSizeSp = 14f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppSpacerV(10)
                    }
                }

                // 🔹 Feature List
                items(propertyFeatures.size) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 15.dp)
                    ) {
                        AppTextMedium(text = "•", fontSize = 15.sp, modifier = Modifier.padding(end = 6.dp))
                        AppTextMedium(
                            text = propertyFeatures[index],
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 5.dp, end = 6.dp)
                        )
                    }
                }
            }
        }
    }
}