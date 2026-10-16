package com.app.propertyfinder.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// NEW
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val totalCount: Int = 0,
    val currentPage: Int = 0,
    val limit: Int = 5,
    val hasNextPage: Boolean = false,
    val isFirstPageLoading: Boolean = false,
    val isNextPageLoading: Boolean = false,
    val errorMessage: String? = null
)

// NEW
class BasePaginator<T>(private val keySelector: ((T) -> Any?)? = null) {
    private val _state = MutableStateFlow(PaginationState<T>())
    val state: StateFlow<PaginationState<T>> = _state.asStateFlow()

    private var isRequestRunning = false

    // NEW
    fun reset(limit: Int = _state.value.limit) {
        isRequestRunning = false
        _state.value = PaginationState(
            items = emptyList(),
            currentPage = 0,
            limit = limit,
            hasNextPage = false,
            isFirstPageLoading = false,
            isNextPageLoading = false,
            errorMessage = null
        )
    }

    // NEW
    fun prepareFirstPage(limit: Int = _state.value.limit): Int? {
        if (isRequestRunning) return null

        isRequestRunning = true
        _state.value = PaginationState(
            items = emptyList(),
            currentPage = 0,
            limit = limit,
            hasNextPage = false,
            isFirstPageLoading = true,
            isNextPageLoading = false,
            errorMessage = null
        )
        return 1
    }

    // NEW
    fun prepareNextPage(): Int? {
        val current = _state.value

        if (isRequestRunning) return null
        if (!current.hasNextPage) return null
        if (current.isFirstPageLoading || current.isNextPageLoading) return null

        isRequestRunning = true
        _state.value = current.copy(
            isNextPageLoading = true,
            errorMessage = null
        )
        return current.currentPage + 1
    }

    // NEW
    fun onSuccess(
        page: Int,
        newItems: List<T>,
        hasNextPage: Boolean
    ) {
        val current = _state.value

        val mergedItems = if (page == 1) {
            newItems
        } else {
            mergeItems(current.items, newItems)
        }

        _state.value = current.copy(
            items = mergedItems,
            currentPage = page,
            hasNextPage = hasNextPage,
            isFirstPageLoading = false,
            isNextPageLoading = false,
            errorMessage = null
        )
        isRequestRunning = false
    }

    // NEW
    fun onError(message: String?) {
        val current = _state.value
        _state.value = current.copy(
            isFirstPageLoading = false,
            isNextPageLoading = false,
            errorMessage = message
        )
        isRequestRunning = false
    }

    // NEW
    fun canLoadNext(): Boolean {
        val current = _state.value
        return !isRequestRunning &&
                current.hasNextPage &&
                !current.isFirstPageLoading &&
                !current.isNextPageLoading
    }

    // NEW
    private fun mergeItems(oldItems: List<T>, newItems: List<T>): List<T> {
        val combined = oldItems + newItems
        return if (keySelector != null) {
            combined.distinctBy { keySelector.invoke(it) }
        } else {
            combined
        }
    }
}