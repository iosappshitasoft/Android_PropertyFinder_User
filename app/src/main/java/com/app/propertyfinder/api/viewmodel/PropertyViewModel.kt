package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.PaginationState
import com.app.propertyfinder.api.dto.response.MasterDataResponse
import com.app.propertyfinder.api.dto.response.ProjectSearchResponse
import com.app.propertyfinder.api.dto.response.ProjectDetailResponse
import com.app.propertyfinder.api.dto.response.PropertyDetailResponse
import com.app.propertyfinder.api.dto.response.PriceInsightsResponse
import com.app.propertyfinder.api.dto.response.HistoricalTransactionsResponse
import com.app.propertyfinder.api.dto.response.PropertyListItem
import com.app.propertyfinder.api.dto.response.PropertyResponse
import com.app.propertyfinder.api.dto.response.HelpFaqsResponse
import com.app.propertyfinder.api.dto.response.toPropertyListItem
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.helper.PropertyFilterState
import com.app.propertyfinder.helper.DEFAULT_MAX_PRICE
import com.app.propertyfinder.helper.DEFAULT_MIN_PRICE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(private val repository: ApiRepository) : ViewModel() {
    companion object {
        // Keep filters across screen/viewmodel recreation during app session.
        // Clears automatically when app process restarts.
        private var sessionFilterState: PropertyFilterState? = null
        private var sessionDefaultMinPriceBound: Float = DEFAULT_MIN_PRICE
        private var sessionDefaultMaxPriceBound: Float = DEFAULT_MAX_PRICE
    }
    private enum class SearchMode { PROPERTIES, PROJECTS }

    private val _masterDataState = MutableStateFlow(UiState<ApiEnvelope<MasterDataResponse>>())
    val masterDataState: StateFlow<UiState<ApiEnvelope<MasterDataResponse>>> =
        _masterDataState.asStateFlow()

    private val _helpFaqsState = MutableStateFlow(UiState<ApiEnvelope<HelpFaqsResponse>>())
    val helpFaqsState: StateFlow<UiState<ApiEnvelope<HelpFaqsResponse>>> =
        _helpFaqsState.asStateFlow()

    fun getMasterData(
        types: String,
        onResult: (ApiResult<ApiEnvelope<MasterDataResponse>>) -> Unit
    ) {
        viewModelScope.launch {
            _masterDataState.value = UiState(isLoading = true)

            when (val result = repository.getMasterData(types)) {
                is ApiResult.Success -> {
                    _masterDataState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _masterDataState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }
    fun clearMasterDataState() {
        _masterDataState.value = UiState()
    }

    fun getHelpFaqs(
        category: String,
        onResult: (ApiResult<ApiEnvelope<HelpFaqsResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _helpFaqsState.value = UiState(isLoading = true)
            when (val result = repository.getHelpFaqs(category)) {
                is ApiResult.Success -> {
                    _helpFaqsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _helpFaqsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }

    private val _propertyDataState = MutableStateFlow(UiState<ApiEnvelope<PropertyResponse>>())
    private val _directoryPropertiesState =
        MutableStateFlow(UiState<ApiEnvelope<PropertyResponse>>())
    val directoryPropertiesState: StateFlow<UiState<ApiEnvelope<PropertyResponse>>> =
        _directoryPropertiesState.asStateFlow()

    private val _propertyPaginationState = MutableStateFlow(PaginationState<PropertyListItem>())
    val propertyPaginationState: StateFlow<PaginationState<PropertyListItem>> =
        _propertyPaginationState.asStateFlow()

    private val _propertyDetailsState = MutableStateFlow(UiState<ApiEnvelope<PropertyDetailResponse>>())
    val propertyDetailsState: StateFlow<UiState<ApiEnvelope<PropertyDetailResponse>>> =
        _propertyDetailsState.asStateFlow()
    private val _projectDetailsState = MutableStateFlow(UiState<ApiEnvelope<ProjectDetailResponse>>())
    val projectDetailsState: StateFlow<UiState<ApiEnvelope<ProjectDetailResponse>>> =
        _projectDetailsState.asStateFlow()

    private val _priceInsightsState = MutableStateFlow(UiState<ApiEnvelope<PriceInsightsResponse>>())
    val priceInsightsState: StateFlow<UiState<ApiEnvelope<PriceInsightsResponse>>> =
        _priceInsightsState.asStateFlow()

    private val _historicalTransactionsState = MutableStateFlow(UiState<ApiEnvelope<HistoricalTransactionsResponse>>())
    val historicalTransactionsState: StateFlow<UiState<ApiEnvelope<HistoricalTransactionsResponse>>> =
        _historicalTransactionsState.asStateFlow()

    private val _filterState = MutableStateFlow(sessionFilterState ?: PropertyFilterState())
    val filterState: StateFlow<PropertyFilterState> = _filterState.asStateFlow()

    private var currentPage = 1
    private var currentLimit = 15
    private var currentListingTypeId: String? = null
    private var currentSearchMode: SearchMode = SearchMode.PROPERTIES
    private var defaultMinPriceBound: Float = sessionDefaultMinPriceBound
    private var defaultMaxPriceBound: Float = sessionDefaultMaxPriceBound

    private val _developerProjectsPaginationState =
        MutableStateFlow(PaginationState<PropertyListItem>())
    val developerProjectsPaginationState: StateFlow<PaginationState<PropertyListItem>> =
        _developerProjectsPaginationState.asStateFlow()

    private var developerProjectsDeveloperId: String? = null
    private var developerProjectsLimit = 18
    private var developerProjectsBedrooms: String? = null
    private var developerProjectsPriceMin: Int? = null
    private var developerProjectsPriceMax: Int? = null
    private var developerProjectsSortBy: String? = "newest"

    fun setPriceBounds(min: Float, max: Float) {
        defaultMinPriceBound = min
        defaultMaxPriceBound = max
        sessionDefaultMinPriceBound = min
        sessionDefaultMaxPriceBound = max
    }

    fun ensureDefaultListingType(defaultListingTypeId: String?) {
        if (!defaultListingTypeId.isNullOrBlank() &&
            _filterState.value.selectedListingTypeId == null
        ) {
            _filterState.value = _filterState.value.copy(
                selectedListingTypeId = defaultListingTypeId
            )
        }
    }

    fun updateFilterState(
        selectedListingTypeId: String? = _filterState.value.selectedListingTypeId,
        showCommercialOnly: Boolean = _filterState.value.showCommercialOnly,
        isDldRegistered: Boolean = _filterState.value.isDldRegistered,
        isVerifiedOnly: Boolean = _filterState.value.isVerifiedOnly,
        locationQuery: String = _filterState.value.locationQuery,
        selectedPropertyTypeId: String? = _filterState.value.selectedPropertyTypeId,
        selectedPriceRange: ClosedFloatingPointRange<Float> = _filterState.value.selectedPriceRange,
        selectedBeds: String? = _filterState.value.selectedBeds,
        selectedBaths: String? = _filterState.value.selectedBaths,
        selectedFurnishingId: String? = _filterState.value.selectedFurnishingId,
        minSize: String = _filterState.value.minSize,
        maxSize: String = _filterState.value.maxSize,
        selectedAmenityIds: Set<String> = _filterState.value.selectedAmenityIds,
        postedDate: String = _filterState.value.postedDate,
        sortBy: String? = _filterState.value.sortBy,
        selectedVirtualViewingId: String? = _filterState.value.selectedVirtualViewingId,
        selectedDeliveryDateId: String? = _filterState.value.selectedDeliveryDateId
    ) {
        _filterState.value = _filterState.value.copy(
            selectedListingTypeId = selectedListingTypeId,
            showCommercialOnly = showCommercialOnly,
            isDldRegistered = isDldRegistered,
            isVerifiedOnly = isVerifiedOnly,
            locationQuery = locationQuery,
            selectedPropertyTypeId = selectedPropertyTypeId,
            selectedPriceRange = selectedPriceRange,
            selectedBeds = selectedBeds,
            selectedBaths = selectedBaths,
            selectedFurnishingId = selectedFurnishingId,
            minSize = minSize,
            maxSize = maxSize,
            selectedAmenityIds = selectedAmenityIds,
            postedDate = postedDate,
            sortBy = sortBy,
            selectedVirtualViewingId = selectedVirtualViewingId,
            selectedDeliveryDateId = selectedDeliveryDateId
        )
        sessionFilterState = _filterState.value
    }

    fun applyFullFilterState(state: PropertyFilterState) {
        _filterState.value = state
        sessionFilterState = state
    }

    fun clearFilterState() {
        _filterState.value = PropertyFilterState()
        sessionFilterState = _filterState.value
    }

    fun resetFilterState(defaultListingTypeId: String? = _filterState.value.selectedListingTypeId) {
        _filterState.value = PropertyFilterState(
            selectedListingTypeId = defaultListingTypeId
        )
        sessionFilterState = _filterState.value
    }

    fun getPropertyDetails(propertyId: String, onResult: (ApiResult<ApiEnvelope<PropertyDetailResponse>>) -> Unit) {
        viewModelScope.launch {
            _propertyDetailsState.value = UiState(isLoading = true)

            when (val result = repository.propertyDetails(TokenManager.accessToken,propertyId)) {
                is ApiResult.Success -> {
                    _propertyDetailsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _propertyDetailsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }

    fun clearPropertyDetailsState() {
        _propertyDetailsState.value = UiState()
    }

    fun getPriceInsights(propertyId: String) {
        viewModelScope.launch {
            _priceInsightsState.value = UiState(isLoading = true)
            when (val result = repository.getPriceInsights(propertyId)) {
                is ApiResult.Success -> {
                    _priceInsightsState.value = UiState(
                        isLoading = false,
                        data = result.body
                    )
                }
                is ApiResult.Error -> {
                    _priceInsightsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun getHistoricalTransactions(request: Map<String, Any>) {
        viewModelScope.launch {
            _historicalTransactionsState.value = UiState(isLoading = true)
            when (val result = repository.getHistoricalTransactions(request)) {
                is ApiResult.Success -> {
                    _historicalTransactionsState.value = UiState(
                        isLoading = false,
                        data = result.body
                    )
                }
                is ApiResult.Error -> {
                    _historicalTransactionsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun getProjectDetails(
        projectId: String,
        onResult: (ApiResult<ApiEnvelope<ProjectDetailResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _projectDetailsState.value = UiState(isLoading = true)
            when (val result = repository.projectDetails(projectId)) {
                is ApiResult.Success -> {
                    _projectDetailsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _projectDetailsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }

    /**
     * Agent / agency property directory: minimal [propertyData] body (listingType, page, limit,
     * plus agentId or agencyId). Omits [sortBy] when null or blank.
     */
    fun loadDirectoryProperties(
        listingTypeId: String,
        agentId: String? = null,
        agencyId: String? = null,
        sortBy: String? = null,
        keyword: String? = null,
        page: Int = 1,
        limit: Int = 20
    ) {
        if (listingTypeId.isBlank()) return
        if (agentId.isNullOrBlank() && agencyId.isNullOrBlank()) return

        viewModelScope.launch {
            _directoryPropertiesState.value = UiState(isLoading = true)
            val request = hashMapOf<String, Any>(
                "listingType" to listingTypeId,
                "page" to page,
                "limit" to limit
            )
            if (!agentId.isNullOrBlank()) request["agentId"] = agentId
            if (!agencyId.isNullOrBlank()) request["agencyId"] = agencyId
            if (!sortBy.isNullOrBlank()) request["sortBy"] = sortBy
            if (!keyword.isNullOrBlank()) request["keyword"] = keyword

            when (val result = repository.propertyData(request, token = TokenManager.accessToken)) {
                is ApiResult.Success -> {
                    _directoryPropertiesState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _directoryPropertiesState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun loadFirstPropertyPage(
        listingTypeId: String,
        limit: Int = 15,
        isNewProjects: Boolean = false,
        amenityIds: List<String>? = null,
        onResult: (ApiResult<ApiEnvelope<PropertyResponse>>) -> Unit
    ) {
        currentPage = 1
        currentLimit = limit
        currentListingTypeId = listingTypeId
        currentSearchMode = if (isNewProjects) SearchMode.PROJECTS else SearchMode.PROPERTIES

        _propertyPaginationState.value = _propertyPaginationState.value.copy(
            isFirstPageLoading = true,
            isNextPageLoading = false,
            errorMessage = null,
            currentPage = 1,
            items = emptyList(),
            totalCount = 0
        )

        val request = if (isNewProjects) {
            buildProjectRequest(page = 1, limit = limit, amenityIds = amenityIds)
        } else {
            buildPropertyRequest(listingTypeId = listingTypeId, page = 1, limit = limit, amenityIds = amenityIds)
        }

        viewModelScope.launch {
            _propertyDataState.value = UiState(isLoading = true)

            when (val result = if (isNewProjects) {
                repository.projectData(request)
            } else {
                repository.propertyData(request, token = TokenManager.accessToken)
            }) {
                is ApiResult.Success -> {
                    val properties = if (isNewProjects) {
                        (result.body.data as? ProjectSearchResponse)?.projects?.map { it.toPropertyListItem() } ?: emptyList()
                    } else {
                        (result.body.data as? PropertyResponse)?.properties ?: emptyList()
                    }
                    val hasNextPage = extractHasNextPage(
                        hasNextFromApi = if (isNewProjects) {
                            (result.body.data as? ProjectSearchResponse)?.pagination?.hasNextPage
                        } else {
                            (result.body.data as? PropertyResponse)?.pagination?.hasNextPage
                        },
                        loadedItemsCount = properties.size,
                        limit = limit
                    )
                    val totalCount = if (isNewProjects) {
                        (result.body.data as? ProjectSearchResponse)?.pagination?.totalProjects
                    } else {
                        (result.body.data as? PropertyResponse)?.pagination?.totalProperties
                    } ?: properties.size

                    _propertyDataState.value = if (isNewProjects) {
                        // Keep list state updated from projects/search while avoiding
                        // assigning a different envelope type into propertyDataState.
                        UiState(
                            isLoading = false,
                            code = result.code
                        )
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        UiState(
                            isLoading = false,
                            data = result.body as ApiEnvelope<PropertyResponse>,
                            code = result.code
                        )
                    }

                    _propertyPaginationState.value = _propertyPaginationState.value.copy(
                        isFirstPageLoading = false,
                        isNextPageLoading = false,
                        items = properties,
                        totalCount = totalCount,
                        currentPage = 1,
                        hasNextPage = hasNextPage,
                        errorMessage = null
                    )

                    @Suppress("UNCHECKED_CAST")
                    onResult(result as ApiResult<ApiEnvelope<PropertyResponse>>)
                }

                is ApiResult.Error -> {
                    _propertyDataState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )

                    _propertyPaginationState.value = _propertyPaginationState.value.copy(
                        isFirstPageLoading = false,
                        isNextPageLoading = false,
                        items = emptyList(),
                        totalCount = 0,
                        hasNextPage = false,
                        errorMessage = result.message
                    )

                    @Suppress("UNCHECKED_CAST")
                    onResult(result as ApiResult<ApiEnvelope<PropertyResponse>>)
                }
            }
        }
    }

    fun loadNextPropertyPage(
        onResult: (ApiResult<ApiEnvelope<PropertyResponse>>) -> Unit
    ) {
        val listingTypeId = currentListingTypeId ?: return
        if (_propertyPaginationState.value.isNextPageLoading) return
        if (!_propertyPaginationState.value.hasNextPage) return

        val nextPage = currentPage + 1

        _propertyPaginationState.value = _propertyPaginationState.value.copy(
            isNextPageLoading = true,
            errorMessage = null
        )

        val request = if (currentSearchMode == SearchMode.PROJECTS) {
            buildProjectRequest(
                page = nextPage,
                limit = currentLimit,
                amenityIds = _filterState.value.selectedAmenityIds.toList().takeIf { it.isNotEmpty() }
            )
        } else {
            buildPropertyRequest(
                listingTypeId = listingTypeId,
                page = nextPage,
                limit = currentLimit,
                amenityIds = _filterState.value.selectedAmenityIds.toList().takeIf { it.isNotEmpty() }
            )
        }

        viewModelScope.launch {
            when (val result = if (currentSearchMode == SearchMode.PROJECTS) {
                repository.projectData(request)
            } else {
                repository.propertyData(request, token = TokenManager.accessToken)
            }) {
                is ApiResult.Success -> {
                    val newItems = if (currentSearchMode == SearchMode.PROJECTS) {
                        (result.body.data as? ProjectSearchResponse)?.projects?.map { it.toPropertyListItem() } ?: emptyList()
                    } else {
                        (result.body.data as? PropertyResponse)?.properties ?: emptyList()
                    }
                    val oldItems = _propertyPaginationState.value.items
                    val updatedItems = oldItems + newItems

                    val hasNextPage = extractHasNextPage(
                        hasNextFromApi = if (currentSearchMode == SearchMode.PROJECTS) {
                            (result.body.data as? ProjectSearchResponse)?.pagination?.hasNextPage
                        } else {
                            (result.body.data as? PropertyResponse)?.pagination?.hasNextPage
                        },
                        loadedItemsCount = newItems.size,
                        limit = currentLimit
                    )
                    val totalCount = if (currentSearchMode == SearchMode.PROJECTS) {
                        (result.body.data as? ProjectSearchResponse)?.pagination?.totalProjects
                    } else {
                        (result.body.data as? PropertyResponse)?.pagination?.totalProperties
                    } ?: _propertyPaginationState.value.totalCount

                    currentPage = nextPage

                    _propertyPaginationState.value = _propertyPaginationState.value.copy(
                        isNextPageLoading = false,
                        items = updatedItems,
                        totalCount = totalCount,
                        currentPage = currentPage,
                        hasNextPage = hasNextPage,
                        errorMessage = null
                    )

                    @Suppress("UNCHECKED_CAST")
                    onResult(result as ApiResult<ApiEnvelope<PropertyResponse>>)
                }

                is ApiResult.Error -> {
                    _propertyPaginationState.value = _propertyPaginationState.value.copy(
                        isNextPageLoading = false,
                        errorMessage = result.message
                    )

                    @Suppress("UNCHECKED_CAST")
                    onResult(result as ApiResult<ApiEnvelope<PropertyResponse>>)
                }
            }
        }
    }

    /**
     * True when the user left the slider at the full master-data range (no price filter).
     * Uses a small epsilon so float slider values still match bounds.
     */
    private fun isFullPriceSelection(
        range: ClosedFloatingPointRange<Float>,
        minBound: Float,
        maxBound: Float
    ): Boolean {
        val epsilon = 1f
        return range.start <= minBound + epsilon && range.endInclusive >= maxBound - epsilon
    }

    /**
     * Sends [priceMin] and [priceMax] whenever the selection is narrower than the full range.
     * (Sending only one side often failed APIs or dropped the other bound when min == bound.)
     */
    private fun appendPriceFilters(request: HashMap<String, Any>, filter: PropertyFilterState) {
        if (isFullPriceSelection(filter.selectedPriceRange, defaultMinPriceBound, defaultMaxPriceBound)) {
            return
        }
        request["priceMin"] = filter.selectedPriceRange.start.toInt()
        request["priceMax"] = filter.selectedPriceRange.endInclusive.toInt()
    }

    private fun buildPropertyRequest(
        listingTypeId: String,
        page: Int,
        limit: Int,
        amenityIds: List<String>? = null
    ): HashMap<String, Any> {
        val filter = _filterState.value
        val request = hashMapOf<String, Any>()

        request["listingType"] = listingTypeId
        request["page"] = page
        request["limit"] = limit
        request["isCommercial"] = filter.showCommercialOnly
        if (filter.isVerifiedOnly) {
            request["isVerified"] = true
        }

        if (filter.locationQuery.isNotBlank()) {
            request["keyword"] = filter.locationQuery.trim()
        }
        if (!filter.selectedPropertyTypeId.isNullOrBlank()) {
            request["propertyType"] = filter.selectedPropertyTypeId
        }

        appendPriceFilters(request, filter)

        filter.selectedBeds?.let {
            request["bedrooms"] = it
        }

        filter.selectedBaths?.let {
            request["bathrooms"] = it
        }

        val finalAmenityIds = amenityIds ?: filter.selectedAmenityIds.toList().takeIf { it.isNotEmpty() }
        if (!finalAmenityIds.isNullOrEmpty()) {
            request["amenities"] = finalAmenityIds.joinToString(",")
        }

        if (!filter.selectedFurnishingId.isNullOrBlank()) {
            request["furnished"] = filter.selectedFurnishingId
        }

        if (filter.minSize != "Any") {
            filter.minSize.toIntOrNull()?.let { request["areaMin"] = it }
        }

        if (filter.maxSize != "Any") {
            filter.maxSize.toIntOrNull()?.let { request["areaMax"] = it }
        }

        if (!filter.sortBy.isNullOrBlank()) {
            request["sortBy"] = filter.sortBy
        }

        if (!filter.selectedVirtualViewingId.isNullOrBlank()) {
            request["virtualViewing"] = filter.selectedVirtualViewingId
        }

        return request
    }

    private fun buildProjectRequest(
        page: Int,
        limit: Int,
        amenityIds: List<String>? = null
    ): HashMap<String, Any> {
        val filter = _filterState.value
        val request = hashMapOf<String, Any>(
            "page" to page,
            "limit" to limit
        )

        if (filter.showCommercialOnly) request["hasPostHandover"] = true
        if (filter.isDldRegistered) request["isDldRegistered"] = true
        if (filter.isVerifiedOnly) request["isVerified"] = true

        if (filter.locationQuery.isNotBlank()) request["keyword"] = filter.locationQuery.trim()
        if (!filter.selectedPropertyTypeId.isNullOrBlank()) request["propertyType"] = filter.selectedPropertyTypeId
        filter.selectedBeds?.let { request["bedrooms"] = it }
        filter.selectedBaths?.let { request["bathrooms"] = it }
        appendPriceFilters(request, filter)
        if (filter.minSize != "Any") filter.minSize.toIntOrNull()?.let { request["areaMin"] = it }
        if (filter.maxSize != "Any") filter.maxSize.toIntOrNull()?.let { request["areaMax"] = it }
        val finalAmenityIds = amenityIds ?: filter.selectedAmenityIds.toList().takeIf { it.isNotEmpty() }
        if (!finalAmenityIds.isNullOrEmpty()) request["amenities"] = finalAmenityIds.joinToString(",")
        if (!filter.sortBy.isNullOrBlank()) request["sortBy"] = filter.sortBy
        if (!filter.selectedDeliveryDateId.isNullOrBlank()) {
            request["deliveryDate"] = filter.selectedDeliveryDateId
        }

        return request
    }

    private fun extractHasNextPage(
        hasNextFromApi: Boolean?,
        loadedItemsCount: Int,
        limit: Int
    ): Boolean {
        return hasNextFromApi ?: (loadedItemsCount >= limit)
    }

    fun loadDeveloperProjectsFirstPage(
        developerId: String,
        limit: Int = 18,
        bedrooms: String? = null,
        priceMin: Int? = null,
        priceMax: Int? = null,
        sortBy: String? = "newest"
    ) {
        if (developerId.isBlank()) return
        developerProjectsDeveloperId = developerId
        developerProjectsLimit = limit
        developerProjectsBedrooms = bedrooms
        developerProjectsPriceMin = priceMin
        developerProjectsPriceMax = priceMax
        developerProjectsSortBy = sortBy ?: "newest"

        _developerProjectsPaginationState.value = PaginationState(
            items = emptyList(),
            totalCount = 0,
            currentPage = 0,
            limit = limit,
            hasNextPage = false,
            isFirstPageLoading = true,
            isNextPageLoading = false,
            errorMessage = null
        )

        viewModelScope.launch {
            val request = hashMapOf<String, Any>(
                "page" to 1,
                "limit" to limit,
                "developerId" to developerId,
                "sortBy" to (sortBy ?: "newest")
            )
            if (!bedrooms.isNullOrBlank()) request["bedrooms"] = bedrooms
            if (priceMin != null) request["priceMin"] = priceMin
            if (priceMax != null) request["priceMax"] = priceMax

            when (val result = repository.projectData(request)) {
                is ApiResult.Success -> {
                    val body = result.body.data as? ProjectSearchResponse
                    val projects = body?.projects?.map { it.toPropertyListItem() }.orEmpty()
                    val pagination = body?.pagination
                    val hasNext = extractHasNextPage(
                        hasNextFromApi = pagination?.hasNextPage,
                        loadedItemsCount = projects.size,
                        limit = limit
                    )
                    val total = pagination?.totalProjects ?: projects.size
                    _developerProjectsPaginationState.value =
                        _developerProjectsPaginationState.value.copy(
                            isFirstPageLoading = false,
                            isNextPageLoading = false,
                            items = projects,
                            currentPage = 1,
                            hasNextPage = hasNext,
                            totalCount = total,
                            errorMessage = null
                        )
                }

                is ApiResult.Error -> {
                    _developerProjectsPaginationState.value =
                        _developerProjectsPaginationState.value.copy(
                            isFirstPageLoading = false,
                            isNextPageLoading = false,
                            items = emptyList(),
                            totalCount = 0,
                            hasNextPage = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }

    fun loadDeveloperProjectsNextPage() {
        val developerId = developerProjectsDeveloperId ?: return
        val prev = _developerProjectsPaginationState.value
        if (prev.isNextPageLoading || !prev.hasNextPage || prev.isFirstPageLoading) return

        val nextPage = prev.currentPage + 1
        _developerProjectsPaginationState.value = prev.copy(
            isNextPageLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val request = hashMapOf<String, Any>(
                "page" to nextPage,
                "limit" to developerProjectsLimit,
                "developerId" to developerId,
                "sortBy" to (developerProjectsSortBy ?: "newest")
            )
            if (!developerProjectsBedrooms.isNullOrBlank()) request["bedrooms"] = developerProjectsBedrooms!!
            if (developerProjectsPriceMin != null) request["priceMin"] = developerProjectsPriceMin!!
            if (developerProjectsPriceMax != null) request["priceMax"] = developerProjectsPriceMax!!

            when (val result = repository.projectData(request)) {
                is ApiResult.Success -> {
                    val body = result.body.data as? ProjectSearchResponse
                    val newItems = body?.projects?.map { it.toPropertyListItem() }.orEmpty()
                    val pagination = body?.pagination
                    val hasNext = extractHasNextPage(
                        hasNextFromApi = pagination?.hasNextPage,
                        loadedItemsCount = newItems.size,
                        limit = developerProjectsLimit
                    )
                    val current = _developerProjectsPaginationState.value
                    val total = pagination?.totalProjects ?: current.totalCount
                    _developerProjectsPaginationState.value = current.copy(
                        isNextPageLoading = false,
                        items = current.items + newItems,
                        currentPage = nextPage,
                        hasNextPage = hasNext,
                        totalCount = total,
                        errorMessage = null
                    )
                }

                is ApiResult.Error -> {
                    _developerProjectsPaginationState.value =
                        _developerProjectsPaginationState.value.copy(
                            isNextPageLoading = false,
                            errorMessage = result.message
                        )
                }
            }
        }
    }
}