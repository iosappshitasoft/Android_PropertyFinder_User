package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.SavedPropertiesResponse
import com.app.propertyfinder.api.dto.response.AlertsResponse
import com.app.propertyfinder.api.dto.response.AlertItem
import com.app.propertyfinder.api.dto.response.ContactedPropertiesResponse
import com.app.propertyfinder.api.dto.response.InquiryData
import com.app.propertyfinder.api.dto.response.AgencyProfileResponse
import com.app.propertyfinder.api.dto.response.AgencySearchResponse
import com.app.propertyfinder.api.dto.response.AgentProfileResponse
import com.app.propertyfinder.api.dto.response.AgentSearchItem
import com.app.propertyfinder.api.dto.response.AgentSearchResponse
import com.app.propertyfinder.api.dto.response.DeveloperSearchResponse
import com.app.propertyfinder.api.dto.response.DeveloperProfileResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.app.propertyfinder.api.PaginationState
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(private val repository: ApiRepository) : ViewModel() {

    private val _addToSavedState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val addToSavedState: StateFlow<UiState<ApiEnvelope<Nothing>>> = _addToSavedState.asStateFlow()

    private val _savedPropertiesState = MutableStateFlow(UiState<ApiEnvelope<SavedPropertiesResponse>>())
    val savedPropertiesState: StateFlow<UiState<ApiEnvelope<SavedPropertiesResponse>>> =
        _savedPropertiesState.asStateFlow()

    private val _removeFromSavedState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val removeFromSavedState: StateFlow<UiState<ApiEnvelope<Nothing>>> =
        _removeFromSavedState.asStateFlow()

    private val _alertsState = MutableStateFlow(UiState<ApiEnvelope<AlertsResponse>>())
    val alertsState: StateFlow<UiState<ApiEnvelope<AlertsResponse>>> = _alertsState.asStateFlow()

    private val _createAlertState = MutableStateFlow(UiState<ApiEnvelope<AlertItem>>())
    val createAlertState: StateFlow<UiState<ApiEnvelope<AlertItem>>> = _createAlertState.asStateFlow()

    private val _updateAlertState = MutableStateFlow(UiState<ApiEnvelope<AlertItem>>())
    val updateAlertState: StateFlow<UiState<ApiEnvelope<AlertItem>>> = _updateAlertState.asStateFlow()

    private val _deleteAlertState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val deleteAlertState: StateFlow<UiState<ApiEnvelope<Nothing>>> = _deleteAlertState.asStateFlow()

    private val _deleteAllAlertsState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val deleteAllAlertsState: StateFlow<UiState<ApiEnvelope<Nothing>>> = _deleteAllAlertsState.asStateFlow()

    private val _contactedPropertiesState =
        MutableStateFlow(UiState<ApiEnvelope<ContactedPropertiesResponse>>())
    val contactedPropertiesState: StateFlow<UiState<ApiEnvelope<ContactedPropertiesResponse>>> =
        _contactedPropertiesState.asStateFlow()

    private val _deleteContactedState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val deleteContactedState: StateFlow<UiState<ApiEnvelope<Nothing>>> =
        _deleteContactedState.asStateFlow()

    private val _deleteAllContactedState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val deleteAllContactedState: StateFlow<UiState<ApiEnvelope<Nothing>>> =
        _deleteAllContactedState.asStateFlow()

    private val _createInquiryState =
        MutableStateFlow(UiState<ApiEnvelope<InquiryData>>())
    val createInquiryState: StateFlow<UiState<ApiEnvelope<InquiryData>>> =
        _createInquiryState.asStateFlow()

    private val _agencySearchState = MutableStateFlow(UiState<ApiEnvelope<AgencySearchResponse>>())
    val agencySearchState: StateFlow<UiState<ApiEnvelope<AgencySearchResponse>>> =
        _agencySearchState.asStateFlow()

    private val _agencyProfileState =
        MutableStateFlow(UiState<ApiEnvelope<AgencyProfileResponse>>())
    val agencyProfileState: StateFlow<UiState<ApiEnvelope<AgencyProfileResponse>>> =
        _agencyProfileState.asStateFlow()

    private val _developerSearchState = MutableStateFlow(UiState<ApiEnvelope<DeveloperSearchResponse>>())
    val developerSearchState: StateFlow<UiState<ApiEnvelope<DeveloperSearchResponse>>> =
        _developerSearchState.asStateFlow()
    private val _developerProfileState = MutableStateFlow(UiState<ApiEnvelope<DeveloperProfileResponse>>())
    val developerProfileState: StateFlow<UiState<ApiEnvelope<DeveloperProfileResponse>>> =
        _developerProfileState.asStateFlow()

    private val _agentSearchPaginationState = MutableStateFlow(PaginationState<AgentSearchItem>(limit = 20))
    val agentSearchPaginationState: StateFlow<PaginationState<AgentSearchItem>> =
        _agentSearchPaginationState.asStateFlow()

    private val _agentProfileState = MutableStateFlow(UiState<ApiEnvelope<AgentProfileResponse>>())
    val agentProfileState: StateFlow<UiState<ApiEnvelope<AgentProfileResponse>>> =
        _agentProfileState.asStateFlow()

    private var currentAgentSearchPage = 1
    private var hasMoreAgentSearch = false
    private var latestAgentSearchToken: String? = null
    private var latestAgentSearchRequest: Map<String, Any> = emptyMap()

    fun addToSaved(token: String, propertyId: String, onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}) {
        val request = hashMapOf<String, Any>(
            "propertyId" to propertyId
        )

        viewModelScope.launch {
            _addToSavedState.value = UiState(isLoading = true)

            when (val result = repository.addToSaved(token, request)) {
                is ApiResult.Success -> {
                    _addToSavedState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _addToSavedState.value = UiState(
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
    fun clearAddToSavedState() {
        _addToSavedState.value = UiState()
    }

    fun getSavedProperties(
        token: String,
        page: Int = 1,
        limit: Int = 15,
        search: String? = null
    ) {
        val request = hashMapOf<String, Any>(
            "page" to page,
            "limit" to limit
        )

        if (!search.isNullOrBlank()) {
            request["search"] = search
        }

        viewModelScope.launch {
            _savedPropertiesState.value = UiState(isLoading = true)

            when (val result = repository.getSavedProperties(token, request)) {
                is ApiResult.Success -> {
                    _savedPropertiesState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _savedPropertiesState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearSavedPropertiesState() {
        _savedPropertiesState.value = UiState()
    }

    fun removeFromSaved(
        token: String,
        propertyId: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _removeFromSavedState.value = UiState(isLoading = true)

            when (val result = repository.removeFromSaved(token, propertyId)) {
                is ApiResult.Success -> {
                    _removeFromSavedState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _removeFromSavedState.value = UiState(
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

    fun clearRemoveFromSavedState() {
        _removeFromSavedState.value = UiState()
    }

    fun getUserAlerts(
        token: String,
        page: Int = 1,
        limit: Int = 20
    ) {
        viewModelScope.launch {
            _alertsState.value = UiState(isLoading = true)

            when (val result = repository.getUserAlerts(token, page, limit)) {
                is ApiResult.Success -> {
                    _alertsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _alertsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearAlertsState() {
        _alertsState.value = UiState()
    }

    fun createUserAlert(
        token: String,
        request: Map<String, Any>,
        onResult: (ApiResult<ApiEnvelope<AlertItem>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _createAlertState.value = UiState(isLoading = true)

            when (val result = repository.createUserAlert(token, request)) {
                is ApiResult.Success -> {
                    _createAlertState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _createAlertState.value = UiState(
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

    fun clearCreateAlertState() {
        _createAlertState.value = UiState()
    }

    fun updateUserAlert(
        token: String,
        id: String,
        request: Map<String, Any>,
        onResult: (ApiResult<ApiEnvelope<AlertItem>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _updateAlertState.value = UiState(isLoading = true)

            when (val result = repository.updateUserAlert(token, id, request)) {
                is ApiResult.Success -> {
                    _updateAlertState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _updateAlertState.value = UiState(
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

    fun clearUpdateAlertState() {
        _updateAlertState.value = UiState()
    }

    fun deleteUserAlert(
        token: String,
        id: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _deleteAlertState.value = UiState(isLoading = true)

            when (val result = repository.deleteUserAlert(token, id)) {
                is ApiResult.Success -> {
                    _deleteAlertState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _deleteAlertState.value = UiState(
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

    fun clearDeleteAlertState() {
        _deleteAlertState.value = UiState()
    }

    fun deleteAllUserAlerts(
        token: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _deleteAllAlertsState.value = UiState(isLoading = true)

            when (val result = repository.deleteAllUserAlerts(token)) {
                is ApiResult.Success -> {
                    _deleteAllAlertsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _deleteAllAlertsState.value = UiState(
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

    fun clearDeleteAllAlertsState() {
        _deleteAllAlertsState.value = UiState()
    }

    fun getContactedProperties(
        token: String,
        page: Int = 1,
        limit: Int = 10
    ) {
        viewModelScope.launch {
            _contactedPropertiesState.value = UiState(isLoading = true)

            when (val result = repository.getContactedProperties(token, page, limit)) {
                is ApiResult.Success -> {
                    _contactedPropertiesState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _contactedPropertiesState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearContactedPropertiesState() {
        _contactedPropertiesState.value = UiState()
    }

    fun deleteContactedProperties(
        token: String,
        ids: List<String>,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _deleteContactedState.value = UiState(isLoading = true)

            when (val result = repository.deleteContactedProperties(token, ids)) {
                is ApiResult.Success -> {
                    _deleteContactedState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _deleteContactedState.value = UiState(
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

    fun clearDeleteContactedState() {
        _deleteContactedState.value = UiState()
    }

    fun deleteAllContactedProperties(
        token: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _deleteAllContactedState.value = UiState(isLoading = true)

            when (val result = repository.deleteAllContactedProperties(token)) {
                is ApiResult.Success -> {
                    _deleteAllContactedState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _deleteAllContactedState.value = UiState(
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

    fun clearDeleteAllContactedState() {
        _deleteAllContactedState.value = UiState()
    }

    fun createInquiry(
        token: String?,
        request: Map<String, Any>,
        onResult: (ApiResult<ApiEnvelope<InquiryData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _createInquiryState.value = UiState(isLoading = true)

            when (val result = repository.createInquiry(token, request)) {
                is ApiResult.Success -> {
                    _createInquiryState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _createInquiryState.value = UiState(
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

    fun clearCreateInquiryState() {
        _createInquiryState.value = UiState()
    }

    fun getAgencySearch(
        minRating: Int = 0,
        page: Int = 1,
        limit: Int = 15,
        name: String? = null,
        serviceNeeded: String? = null
    ) {
        viewModelScope.launch {
            _agencySearchState.value = UiState(isLoading = true)
            val request = hashMapOf<String, Any>(
                "minRating" to minRating,
                "page" to page,
                "limit" to limit
            )
            if (!name.isNullOrBlank()) {
                request["name"] = name
            }
            if (!serviceNeeded.isNullOrBlank()) {
                request["serviceNeeded"] = serviceNeeded
            }

            when (val result = repository.searchAgencies(request)) {
                is ApiResult.Success -> {
                    _agencySearchState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _agencySearchState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearAgencySearchState() {
        _agencySearchState.value = UiState()
    }

    fun getAgencyProfile(
        companyId: String,
        page: Int? = null,
        limit: Int? = null,
        agentPage: Int? = null,
        agentLimit: Int? = null,
        listingType: String? = null,
        sortBy: String? = null
    ) {
        viewModelScope.launch {
            _agencyProfileState.value = UiState(isLoading = true)

            when (val result = repository.getAgencyProfile(
                companyId = companyId,
                page = page,
                limit = limit,
                agentPage = agentPage,
                agentLimit = agentLimit,
                listingType = listingType,
                sortBy = sortBy
            )) {
                is ApiResult.Success -> {
                    _agencyProfileState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }

                is ApiResult.Error -> {
                    _agencyProfileState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearAgencyProfileState() {
        _agencyProfileState.value = UiState()
    }

    fun getDeveloperSearch(
        page: Int = 1,
        limit: Int = 20,
        name: String? = null
    ) {
        viewModelScope.launch {
            _developerSearchState.value = UiState(isLoading = true)
            val request = hashMapOf<String, Any>(
                "page" to page,
                "limit" to limit
            )
            if (!name.isNullOrBlank()) {
                request["name"] = name
            }
            when (val result = repository.searchDevelopers(request)) {
                is ApiResult.Success -> {
                    _developerSearchState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }
                is ApiResult.Error -> {
                    _developerSearchState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearDeveloperSearchState() {
        _developerSearchState.value = UiState()
    }

    fun getDeveloperProfile(developerId: String) {
        viewModelScope.launch {
            _developerProfileState.value = UiState(isLoading = true)
            when (val result = repository.getDeveloperProfile(developerId)) {
                is ApiResult.Success -> {
                    _developerProfileState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                }
                is ApiResult.Error -> {
                    _developerProfileState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                }
            }
        }
    }

    fun clearDeveloperProfileState() {
        _developerProfileState.value = UiState()
    }

    fun searchAgents(
        token: String? = null,
        request: Map<String, Any>,
        onResult: (ApiResult<ApiEnvelope<AgentSearchResponse>>) -> Unit = {}
    ) {
        latestAgentSearchToken = token
        latestAgentSearchRequest = request
        currentAgentSearchPage = 1
        hasMoreAgentSearch = false
        _agentSearchPaginationState.value = PaginationState(
            items = emptyList(),
            totalCount = 0,
            currentPage = 0,
            limit = 20,
            hasNextPage = false,
            isFirstPageLoading = true,
            isNextPageLoading = false,
            errorMessage = null
        )

        val pagedRequest = HashMap(request).apply {
            this["page"] = 1
            this["limit"] = 20
        }

        viewModelScope.launch {
            when (val result = repository.searchAgents(token, pagedRequest)) {
                is ApiResult.Success -> {
                    val data = result.body.data
                    val items = data?.items.orEmpty()
                    val pagination = data?.pagination
                    hasMoreAgentSearch = (pagination?.page ?: 1) < (pagination?.totalPages ?: 1)
                    _agentSearchPaginationState.value = PaginationState(
                        items = items,
                        totalCount = pagination?.total ?: items.size,
                        currentPage = pagination?.page ?: 1,
                        limit = pagination?.limit ?: 20,
                        hasNextPage = hasMoreAgentSearch,
                        isFirstPageLoading = false,
                        isNextPageLoading = false,
                        errorMessage = null
                    )
                    currentAgentSearchPage = pagination?.page ?: 1
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _agentSearchPaginationState.value = _agentSearchPaginationState.value.copy(
                        isFirstPageLoading = false,
                        errorMessage = result.message
                    )
                    onResult(result)
                }
            }
        }
    }

    fun searchAgentsNextPage(onResult: (ApiResult<ApiEnvelope<AgentSearchResponse>>) -> Unit = {}) {
        if (!hasMoreAgentSearch || _agentSearchPaginationState.value.isNextPageLoading || _agentSearchPaginationState.value.isFirstPageLoading) {
            return
        }

        val nextPage = currentAgentSearchPage + 1
        _agentSearchPaginationState.value = _agentSearchPaginationState.value.copy(
            isNextPageLoading = true,
            errorMessage = null
        )

        val pagedRequest = HashMap(latestAgentSearchRequest).apply {
            this["page"] = nextPage
            this["limit"] = 20
        }

        viewModelScope.launch {
            when (val result = repository.searchAgents(latestAgentSearchToken, pagedRequest)) {
                is ApiResult.Success -> {
                    val data = result.body.data
                    val newItems = data?.items.orEmpty()
                    val pagination = data?.pagination
                    val merged = _agentSearchPaginationState.value.items + newItems
                    hasMoreAgentSearch = (pagination?.page ?: nextPage) < (pagination?.totalPages ?: nextPage)
                    _agentSearchPaginationState.value = _agentSearchPaginationState.value.copy(
                        items = merged,
                        totalCount = pagination?.total ?: merged.size,
                        currentPage = pagination?.page ?: nextPage,
                        limit = pagination?.limit ?: 20,
                        hasNextPage = hasMoreAgentSearch,
                        isNextPageLoading = false,
                        errorMessage = null
                    )
                    currentAgentSearchPage = pagination?.page ?: nextPage
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _agentSearchPaginationState.value = _agentSearchPaginationState.value.copy(
                        isNextPageLoading = false,
                        errorMessage = result.message
                    )
                    onResult(result)
                }
            }
        }
    }

    fun clearAgentSearchState() {
        currentAgentSearchPage = 1
        hasMoreAgentSearch = false
        latestAgentSearchToken = null
        latestAgentSearchRequest = emptyMap()
        _agentSearchPaginationState.value = PaginationState(limit = 20)
    }

    fun getAgentProfile(
        agentId: String,
        page: Int = 1,
        limit: Int = 20,
        listingType: String? = null,
        sortBy: String? = null,
        onResult: (ApiResult<ApiEnvelope<AgentProfileResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _agentProfileState.value = UiState(isLoading = true)
            when (val result = repository.getAgentProfile(agentId, page, limit, listingType, sortBy)) {
                is ApiResult.Success -> {
                    _agentProfileState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _agentProfileState.value = UiState(
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

    fun clearAgentProfileState() {
        _agentProfileState.value = UiState()
    }
}