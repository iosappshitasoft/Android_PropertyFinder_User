package com.app.propertyfinder.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.screen.AboutProjectScreen
import com.app.propertyfinder.screen.AboutScreen
import com.app.propertyfinder.screen.AccountScreen
import com.app.propertyfinder.screen.agent.AgentDetailScreen
import com.app.propertyfinder.screen.agent.AgentsScreen
import com.app.propertyfinder.screen.agent.Agents_Properties
import com.app.propertyfinder.screen.AllAmenitiesScreen
import com.app.propertyfinder.screen.AvailablePropertiesScreen
import com.app.propertyfinder.screen.ChangePasswordScreen
import com.app.propertyfinder.screen.company.CompanyAgentsScreen
import com.app.propertyfinder.screen.company.CompanyDetailScreen
import com.app.propertyfinder.screen.company.CompanyPropertiesScreen
import com.app.propertyfinder.screen.ContactDetailsScreen
import com.app.propertyfinder.screen.ContactedPropertiesScreen
import com.app.propertyfinder.screen.CropScreen
import com.app.propertyfinder.screen.NewProjectDetailsScreen
import com.app.propertyfinder.screen.EditProfileScreen
import com.app.propertyfinder.screen.FilterScreen
import com.app.propertyfinder.screen.company.FindCompaniesScreen
import com.app.propertyfinder.screen.developer.FindDevelopersScreen
import com.app.propertyfinder.screen.developer.DeveloperDetailScreen
import com.app.propertyfinder.screen.ExploreCommunityScreen
import com.app.propertyfinder.screen.FAQScreen
import com.app.propertyfinder.screen.HelpScreen
import com.app.propertyfinder.screen.HomeScreen
import com.app.propertyfinder.screen.ImageCropScreen
import com.app.propertyfinder.screen.ImagesFullviewScreen
import com.app.propertyfinder.screen.LoginScreen
import com.app.propertyfinder.screen.MortgageCalculatorScreen
import com.app.propertyfinder.screen.MortgageQuoteScreen
import com.app.propertyfinder.screen.NearestPointsScreen
import com.app.propertyfinder.screen.NetworkUnavailableScreen
import com.app.propertyfinder.screen.NotificationSettingsScreen
import com.app.propertyfinder.screen.NotificationsScreen
import com.app.propertyfinder.screen.PaymentPlansScreen
import com.app.propertyfinder.screen.PriceMapScreen
import com.app.propertyfinder.screen.ProfileImageScreen
import com.app.propertyfinder.screen.ProjectDetailsScreen
import com.app.propertyfinder.screen.PropertyDetailScreen
import com.app.propertyfinder.screen.PropertyLocationScreen
import com.app.propertyfinder.screen.PropertyProjectListScreen
import com.app.propertyfinder.screen.RentBuyCalculatorScreen
import com.app.propertyfinder.screen.ReportScreen
import com.app.propertyfinder.screen.ResidenceDetailsScreen
import com.app.propertyfinder.screen.SavedAlertsScreen
import com.app.propertyfinder.screen.SavedScreen
import com.app.propertyfinder.screen.SplashScreen
import com.app.propertyfinder.screen.TakePhotoScreen
import com.app.propertyfinder.screen.TermsPrivacyScreen
import com.app.propertyfinder.screen.TestingScreen
import com.app.propertyfinder.screen.agent.TrackRecords
import com.app.propertyfinder.screen.TransactionListScreen
import com.app.propertyfinder.screen.TransactionOverviewScreen
import com.app.propertyfinder.screen.TravelTimesScreen
import com.app.propertyfinder.screen.View_full_details
import com.app.propertyfinder.screen.WelcomeScreen
import com.app.propertyfinder.screen.review.OverallReviewsScreen
import com.app.propertyfinder.screen.review.ReviewDetailsScreen
import com.app.propertyfinder.screen.review.WriteReviewScreen
import com.app.propertyfinder.screen.unit.DeveloperUnitsScreen
import com.app.propertyfinder.screen.unit.UnitListScreen
import com.app.propertyfinder.utils.CommonMethods.EnterAnimation
import com.app.propertyfinder.utils.ConnectionState
import com.app.propertyfinder.utils.connectivityState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun SetupNavGraph(navController: NavHostController) {
    val state by connectivityState()

    NavHost(navController = navController, startDestination = Route.Splash.route) {
//    NavHost(navController = navController, startDestination = Route.TestingScreen.route) {

        composable(Route.TestingScreen.route) {
            TestingScreen()
        }
        composable(Route.Splash.route) {
            SplashScreen(navController)
        }
        composable(Route.Welcome.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    WelcomeScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }
        composable(Route.Login.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    LoginScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }
        composable(Route.Change_Password.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    ChangePasswordScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }
        composable(Route.Contact_Details.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    ContactDetailsScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(Route.TransactionOverview.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    TransactionOverviewScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(
            route = Route.TransactionList.route,
            arguments = listOf(
                navArgument("dealType") {
                    type = NavType.StringType
                    defaultValue = "sale"
                },
                navArgument("timeframe") {
                    type = NavType.StringType
                    defaultValue = "ytd"
                }
            )
        ) { backStackEntry ->
            val dealType = backStackEntry.arguments?.getString("dealType") ?: "sale"
            val timeframe = backStackEntry.arguments?.getString("timeframe") ?: "ytd"
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    TransactionListScreen(
                        navController = navController,
                        initialDealType = dealType,
                        initialTimeframe = timeframe
                    )
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(Route.Saved.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    SavedScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(Route.Account.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    AccountScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(Route.EditProfile.route) {
            if (state == ConnectionState.Available) {
                EnterAnimation {
                    EditProfileScreen(navController)
                }
            } else {
                EnterAnimation {
                    NetworkUnavailableScreen()
                }
            }
        }

        composable(Route.ProfileImage.route) {
            EnterAnimation {
                ProfileImageScreen(navController)
            }
        }

        composable(Route.ImageCrop.route) {
            EnterAnimation {
                ImageCropScreen(navController)
            }
        }

        composable(Route.TakePhoto.route) {
            EnterAnimation {
                TakePhotoScreen(navController)
            }
        }

        composable(Route.Crop.route) {
            EnterAnimation {
                CropScreen(navController)
            }
        }

        composable(Route.SavedAlerts.route) {
            EnterAnimation {
                SavedAlertsScreen(navController)
            }
        }

        composable(Route.ContactedProperties.route) {
            EnterAnimation {
                ContactedPropertiesScreen(navController)
            }
        }

        composable(Route.NotificationSettings.route) {
            EnterAnimation {
                NotificationSettingsScreen(navController)
            }
        }

        composable(Route.Agents.route) {
            EnterAnimation {
                AgentsScreen(navController)
            }
        }

        composable(
            route = Route.AgentDetail.route,
            arguments = listOf(
                navArgument("agentId") { type = NavType.StringType },
                navArgument("agentName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString("agentId").orEmpty()
            val agentName = backStackEntry.arguments?.getString("agentName").orEmpty()
            EnterAnimation {
                AgentDetailScreen(
                    navController = navController,
                    agentId = agentId,
                    agentName = agentName
                )
            }
        }

        composable(
            route = Route.Track_records.route,
            arguments = listOf(navArgument("agentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString("agentId").orEmpty()
            EnterAnimation {
                TrackRecords(navController, agentId)
            }
        }

        composable(Route.View_full_details.route) {
            EnterAnimation {
                View_full_details(navController)
            }
        }

        composable(Route.Notifications.route) {
            EnterAnimation {
                NotificationsScreen(navController)
            }
        }

        composable(
            route = Route.Agents_Properties.route,
            arguments = listOf(navArgument("agentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString("agentId").orEmpty()
            EnterAnimation {
                Agents_Properties(navController, agentId)
            }
        }

        composable(Route.FindCompanies.route) {
            EnterAnimation {
                FindCompaniesScreen(navController)
            }
        }
        composable(Route.CompanyDetail.route) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            EnterAnimation {
                CompanyDetailScreen(navController, companyId)
            }
        }

        composable(
            route = Route.CompanyProperties.route,
            arguments = listOf(
                navArgument("companyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            EnterAnimation {
                CompanyPropertiesScreen(navController = navController, companyId = companyId)
            }
        }

        composable(
            route = Route.CompanyAgents.route,
            arguments = listOf(
                navArgument("companyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId").orEmpty()
            EnterAnimation {
                CompanyAgentsScreen(navController = navController, companyId = companyId)
            }
        }

        composable(Route.FindDevelopers.route) {
            EnterAnimation {
                FindDevelopersScreen(navController)
            }
        }

        composable(Route.DeveloperDetail.route) { backStackEntry ->
            val developerId = backStackEntry.arguments?.getString("developerId")
            EnterAnimation {
                DeveloperDetailScreen(navController, developerId)
            }
        }

        composable(
            route = Route.DevPropertiesDetail.route,
            arguments = listOf(
                navArgument("propertyId") { type = NavType.StringType },
                navArgument("fromScreen") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            val fromScreen = backStackEntry.arguments?.getString("fromScreen").orEmpty()
            EnterAnimation {
                NewProjectDetailsScreen(navController, propertyId, fromScreen)
            }
        }
        composable(
            route = Route.ProjectDetails.route + "?projectId={projectId}&propertyId={propertyId}",
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("propertyId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            EnterAnimation {
                ProjectDetailsScreen(navController, projectId, propertyId)
            }
        }
        composable(Route.AllAmenities.route) {
            EnterAnimation {
                AllAmenitiesScreen(navController)
            }
        }
        composable(
            route = Route.AboutProject.route + "?projectId={projectId}",
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            EnterAnimation {
                AboutProjectScreen(navController, projectId)
            }
        }
        composable (Route.TermsPrivacy.route){ backStackEntry->
            val fromScreen = backStackEntry.arguments?.getString("fromScreen")
            EnterAnimation {
                TermsPrivacyScreen(navController, fromScreen.toString())
            }
        }
        composable(Route.PaymentPlan.route) {
            EnterAnimation {
                PaymentPlansScreen(navController)
            }
        }

        composable(Route.AboutUs.route) {
            EnterAnimation {
                AboutScreen(navController)
            }
        }
        composable(Route.MortgageCalculator.route) {
            EnterAnimation {
                MortgageCalculatorScreen(navController)
            }
        }
        composable(Route.RentBuyCalculator.route) {
            EnterAnimation {
                RentBuyCalculatorScreen(navController)
            }
        }
        composable(Route.MortgageQuote.route) {
            EnterAnimation {
                MortgageQuoteScreen(navController)
            }
        }
        composable(Route.Help.route) {
            EnterAnimation {
                HelpScreen(navController)
            }
        }
        composable(Route.ReportProblem.route) {
            EnterAnimation {
                ReportScreen(navController)
            }
        }
        composable(Route.TravelTime.route) {
            EnterAnimation {
                TravelTimesScreen(navController)
            }
        }
        composable(Route.PriceMap.route) {
            EnterAnimation {
                PriceMapScreen(navController)
            }
        }
        composable(Route.ExploreCommunity.route) {
            EnterAnimation {
                ExploreCommunityScreen(navController)
            }
        }
        composable(Route.ResidenceDetails.route) {
            EnterAnimation {
                ResidenceDetailsScreen(navController)
            }
        }
        composable(Route.ImageFullView.route) {
            EnterAnimation {
                ImagesFullviewScreen(navController)
            }
        }
        composable(Route.PropertyLocation.route) {
            EnterAnimation {
                PropertyLocationScreen(navController)
            }
        }
        composable(Route.NearestPoints.route) {
            EnterAnimation {
                NearestPointsScreen(navController)
            }
        }
        composable(Route.Faq.route) {
            EnterAnimation {
                FAQScreen(navController)
            }
        }
        composable(Route.PropertyProjectList.route) { backStackEntry->
            val fromScreen = backStackEntry.arguments?.getString("fromScreen")
            EnterAnimation {
                PropertyProjectListScreen(navController,fromScreen.toString())
            }
        }
        composable(Route.AvailableInProperties.route) {
            EnterAnimation {
                AvailablePropertiesScreen(navController)
            }
        }
        composable(Route.OverallReviews.route) {
            EnterAnimation {
                OverallReviewsScreen(navController)
            }
        }
        composable(Route.ReviewDetails.route) {
            EnterAnimation {
                ReviewDetailsScreen(navController)
            }
        }
        composable(Route.WriteReview.route) {
            EnterAnimation {
                WriteReviewScreen(navController)
            }
        }
        composable(Route.DeveloperUnit.route) {
            EnterAnimation {
                DeveloperUnitsScreen(navController)
            }
        }
        composable(Route.UnitList.route) {
            EnterAnimation {
                UnitListScreen(navController)
            }
        }


        // Shared ViewModel graph only for inside screens
        navigation(startDestination = Route.Home.route, route = Route.MainGraph.route) {
            composable(Route.Home.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.MainGraph.route)
                }
                val viewModel: PropertyViewModel = hiltViewModel(parentEntry)

                if (state == ConnectionState.Available) {
                    EnterAnimation {
                        HomeScreen(navController = navController, viewModel = viewModel)
                    }
                } else {
                    EnterAnimation { NetworkUnavailableScreen() }
                }
            }
            composable(Route.Filter.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.MainGraph.route)
                }
                val viewModel: PropertyViewModel = hiltViewModel(parentEntry)

                if (state == ConnectionState.Available) {
                    EnterAnimation {
                        FilterScreen(navController = navController, viewModel = viewModel)
                    }
                } else {
                    EnterAnimation { NetworkUnavailableScreen() }
                }
            }
            composable(Route.PropertyDetail.route) { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId")?: ""
                if (state == ConnectionState.Available) {
                    EnterAnimation {
                        PropertyDetailScreen(
                            navController = navController,
                            propertyId = propertyId
                        )
                    }
                } else {
                    EnterAnimation {
                        NetworkUnavailableScreen()
                    }
                }
            }
        }
    }
}
