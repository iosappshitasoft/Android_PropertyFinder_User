package com.app.propertyfinder.navigation

sealed class Route(val route: String) {
    object MainGraph : Route("main_graph")

    object Splash : Route("splash")
    object Welcome : Route("welcome")
    object Login : Route("login")

    object Change_Password : Route("Change password")
    object Contact_Details : Route("contact_details")
    object Home : Route("home")
    object Filter : Route("filter")
    object PropertyDetail : Route("property_detail/{propertyId}")
    object TransactionOverview : Route("transaction_overview")
    object TransactionList : Route("transaction_list?dealType={dealType}&timeframe={timeframe}") {
        fun createRoute(dealType: String, timeframe: String) =
            "transaction_list?dealType=$dealType&timeframe=$timeframe"
    }
    object Saved : Route("saved")
    object Account : Route("account")
    object EditProfile : Route("edit_profile")
    object ProfileImage : Route("profile_image")
    object TakePhoto : Route("take_photo")
    object Crop : Route("crop")
    object ImageCrop : Route("image_crop")
    object SavedAlerts : Route("saved_alerts")
    object ContactedProperties : Route("contacted_properties")
    object NotificationSettings : Route("notification_settings")
    object Agents : Route("agents")
    object AgentDetail : Route("agent_detail/{agentId}/{agentName}") {
        fun createRoute(agentId: String, agentName: String): String =
            "agent_detail/$agentId/${android.net.Uri.encode(agentName)}"
    }
    object Track_records : Route("track_records/{agentId}") {
        fun createRoute(agentId: String): String = "track_records/$agentId"
    }
    object View_full_details : Route("view_full_details")
    object Notifications : Route("notifications")
    object Agents_Properties : Route("agents_Properties/{agentId}") {
        fun createRoute(agentId: String) = "agents_Properties/$agentId"
    }
    object FindCompanies : Route("find_companies")
    object CompanyDetail : Route("company_detail/{companyId}")
    object FindDevelopers : Route("find_developers")
    object DeveloperDetail : Route("developer_detail/{developerId}") {
        fun createRoute(developerId: String) = "developer_detail/$developerId"
    }
    object DevPropertiesDetail : Route("dev_properties_details/{propertyId}/{fromScreen}"){
        fun createRoute(propertyId: String, fromScreen: String) =
            "dev_properties_details/$propertyId/$fromScreen"
    }
    object ProjectDetails : Route("project_details") {
        fun createRoute(projectId: String) = "project_details?projectId=$projectId"
        fun createPropertyRoute(propertyId: String) = "project_details?propertyId=$propertyId"
    }
    object AllAmenities : Route("all_amenities")
    object AboutProject : Route("about_project") {
        fun createRoute(projectId: String) = "about_project?projectId=$projectId"
    }
    object PaymentPlan : Route("payment_plan")
    object CompanyProperties : Route("company_properties/{companyId}") {
        fun createRoute(companyId: String) = "company_properties/$companyId"
    }
    object CompanyAgents : Route("company_agents/{companyId}") {
        fun createRoute(companyId: String) = "company_agents/$companyId"
    }
    object AboutUs : Route("about_us")
    object TermsPrivacy : Route("terms_privacy/{fromScreen}") {
        fun createRoute (fromScreen: String) = "terms_privacy/${fromScreen}"
    }
    object MortgageCalculator : Route("mortgage_calculator")
    object RentBuyCalculator : Route("rent_buy_calculator")
    object MortgageQuote : Route("mortgage_quote")

    object Help : Route("help_screen")
    object ReportProblem : Route("report_screen")
    object TravelTime : Route("travel_time")
    object PriceMap : Route("price_map")
    object ExploreCommunity : Route("explore_community")
    object ResidenceDetails : Route("residence_details")

    object ImageFullView : Route("images_fullview")
    object PropertyLocation : Route("property_location")
    object NearestPoints : Route("nearest_points")
    object Faq : Route("faq")
    object PropertyProjectList : Route("property_project/{fromScreen}"){
        fun createRoute (fromScreen: String) = "property_project/${fromScreen}"
    }
    object AvailableInProperties : Route("available_properties")
    object OverallReviews : Route("over_all_reviews")
    object ReviewDetails : Route("review_details")
    object WriteReview : Route("write_review")

    object DeveloperUnit : Route("developer_unit")
    object UnitList : Route("unit_list")
    object TestingScreen : Route("testing_screem")

    /** Nested graph for add-property flow; steps share one ViewModel scoped to this graph. */
    object AddPropertyGraph : Route("add_property_graph")
    object AddPropertyStep1 : Route("add_property_step_1")
    object AddPropertyStep2 : Route("add_property_step_2")
    object AddPropertyStep3 : Route("add_property_step_3")
    object AddPropertyStep4 : Route("add_property_step_4")
    object AddPropertyStep5 : Route("add_property_step_5")
}