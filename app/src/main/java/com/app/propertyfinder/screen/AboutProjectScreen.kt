package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.HtmlText
import com.app.propertyfinder.ui.theme.Background

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutProjectScreenPreview() {
    val navController = rememberNavController()
    AboutProjectScreen(navController, null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutProjectScreen(
    navController: NavController,
    projectId: String?,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val projectDetailsState by propertyViewModel.projectDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) {
        projectId?.takeIf { it.isNotBlank() }?.let {
            propertyViewModel.getProjectDetails(it)
        }
    }

    val project = projectDetailsState.data?.data?.project
    val description = project?.description?.takeIf { it.isNotBlank() }
        ?: project?.aboutProject?.takeIf { it.isNotBlank() }
        ?: ""

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.about_the_project), fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (projectDetailsState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorResource(R.color.darkBlue))
                }
            } else {
                if (description.isNotBlank()) {
                    HtmlText(
                        html = description,
                        colorResId = R.color.textprimary,
                        fontSizeSp = 14f,
                        lineSpacingExtra = 4f,
                        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()
                    )
                }
            }
        }
    }
}