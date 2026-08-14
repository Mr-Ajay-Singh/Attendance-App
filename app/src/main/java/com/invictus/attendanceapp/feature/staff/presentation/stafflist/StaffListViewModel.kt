package com.invictus.attendanceapp.feature.staff.presentation.stafflist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invictus.attendanceapp.feature.auth.domain.usecase.LogoutUseCase
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import com.invictus.attendanceapp.feature.staff.domain.usecase.GetStaffListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffListViewModel @Inject constructor(
    private val getStaffListUseCase: GetStaffListUseCase,
    private val staffRepository: StaffRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(StaffListUiState(isLoading = true))
    val uiState: StateFlow<StaffListUiState> = _uiState.asStateFlow()

    init {
        refresh()

        viewModelScope.launch {
            combine(getStaffListUseCase(), _searchQuery) { list, query ->
                if (query.isBlank()) {
                    list
                } else {
                    list.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.employeeId.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredList ->
                _uiState.update {
                    it.copy(
                        staffList = filteredList,
                        isLoading = false,
                        searchQuery = _searchQuery.value
                    )
                }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            staffRepository.refreshStaffList()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onComplete()
        }
    }
}
