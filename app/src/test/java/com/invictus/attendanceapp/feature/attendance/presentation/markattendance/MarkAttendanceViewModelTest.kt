package com.invictus.attendanceapp.feature.attendance.presentation.markattendance

import androidx.lifecycle.SavedStateHandle
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import com.invictus.attendanceapp.feature.attendance.domain.model.Attendance
import com.invictus.attendanceapp.feature.attendance.domain.repository.AttendanceRepository
import com.invictus.attendanceapp.feature.attendance.domain.usecase.GetAttendanceHistoryUseCase
import com.invictus.attendanceapp.feature.attendance.domain.usecase.MarkAttendanceUseCase
import com.invictus.attendanceapp.feature.auth.domain.usecase.LogoutUseCase
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.usecase.GetStaffUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MarkAttendanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getStaffUseCase: GetStaffUseCase
    private lateinit var markAttendanceUseCase: MarkAttendanceUseCase
    private lateinit var getAttendanceHistoryUseCase: GetAttendanceHistoryUseCase
    private lateinit var attendanceRepository: AttendanceRepository
    private lateinit var tokenProvider: AuthTokenProvider
    private lateinit var logoutUseCase: LogoutUseCase

    private val staffId = "staff_001"
    private val testStaff = Staff(
        id = staffId,
        name = "John Doe",
        employeeId = "EMP001",
        faceEnrolled = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getStaffUseCase = mock()
        markAttendanceUseCase = mock()
        getAttendanceHistoryUseCase = mock()
        attendanceRepository = mock()
        tokenProvider = mock()
        logoutUseCase = mock()

        whenever(tokenProvider.getStaffId()).thenReturn(staffId)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_withTodayAttendance_setsIsMarkedTodayTrue() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val todayAttendance = Attendance(
            id = "att_1",
            staffId = staffId,
            timestamp = now,
            selfiePath = "/path/selfie.jpg",
            latitude = 26.85,
            longitude = 80.95
        )

        whenever(getStaffUseCase(staffId)).thenReturn(testStaff)
        whenever(getAttendanceHistoryUseCase(staffId)).thenReturn(flowOf(listOf(todayAttendance)))
        whenever(attendanceRepository.refreshAttendanceHistory(staffId)).thenReturn(AppResult.Success(Unit))

        val viewModel = MarkAttendanceViewModel(
            getStaffUseCase = getStaffUseCase,
            markAttendanceUseCase = markAttendanceUseCase,
            getAttendanceHistoryUseCase = getAttendanceHistoryUseCase,
            attendanceRepository = attendanceRepository,
            tokenProvider = tokenProvider,
            logoutUseCase = logoutUseCase,
            savedStateHandle = SavedStateHandle(mapOf("staffId" to staffId))
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(testStaff, state.staff)
        assertTrue(state.isMarkedToday)
        assertEquals(1, state.todayPunchCount)
        assertEquals(todayAttendance, state.latestTodayAttendance)
    }

    @Test
    fun init_withYesterdayAttendanceOnly_setsIsMarkedTodayFalse() = runTest(testDispatcher) {
        val yesterday = System.currentTimeMillis() - (24 * 60 * 60 * 1000L + 60000L)
        val yesterdayAttendance = Attendance(
            id = "att_0",
            staffId = staffId,
            timestamp = yesterday,
            selfiePath = "/path/old.jpg",
            latitude = 26.85,
            longitude = 80.95
        )

        whenever(getStaffUseCase(staffId)).thenReturn(testStaff)
        whenever(getAttendanceHistoryUseCase(staffId)).thenReturn(flowOf(listOf(yesterdayAttendance)))
        whenever(attendanceRepository.refreshAttendanceHistory(staffId)).thenReturn(AppResult.Success(Unit))

        val viewModel = MarkAttendanceViewModel(
            getStaffUseCase = getStaffUseCase,
            markAttendanceUseCase = markAttendanceUseCase,
            getAttendanceHistoryUseCase = getAttendanceHistoryUseCase,
            attendanceRepository = attendanceRepository,
            tokenProvider = tokenProvider,
            logoutUseCase = logoutUseCase,
            savedStateHandle = SavedStateHandle(mapOf("staffId" to staffId))
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(testStaff, state.staff)
        assertFalse(state.isMarkedToday)
        assertEquals(0, state.todayPunchCount)
    }

    @Test
    fun openAndCloseCamera_updatesCameraState() = runTest(testDispatcher) {
        whenever(getStaffUseCase(staffId)).thenReturn(testStaff)
        whenever(getAttendanceHistoryUseCase(staffId)).thenReturn(flowOf(emptyList()))
        whenever(attendanceRepository.refreshAttendanceHistory(staffId)).thenReturn(AppResult.Success(Unit))

        val viewModel = MarkAttendanceViewModel(
            getStaffUseCase = getStaffUseCase,
            markAttendanceUseCase = markAttendanceUseCase,
            getAttendanceHistoryUseCase = getAttendanceHistoryUseCase,
            attendanceRepository = attendanceRepository,
            tokenProvider = tokenProvider,
            logoutUseCase = logoutUseCase,
            savedStateHandle = SavedStateHandle(mapOf("staffId" to staffId))
        )

        advanceUntilIdle()

        viewModel.openCamera()
        assertTrue(viewModel.uiState.value.isCameraOpen)

        viewModel.closeCamera()
        assertFalse(viewModel.uiState.value.isCameraOpen)
    }
}
