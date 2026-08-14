package com.invictus.attendanceapp.feature.staff.domain.usecase

import com.invictus.attendanceapp.core.common.AppError
import com.invictus.attendanceapp.core.common.AppResult
import com.invictus.attendanceapp.feature.staff.domain.model.Staff
import com.invictus.attendanceapp.feature.staff.domain.repository.StaffRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AddStaffUseCaseTest {

    private lateinit var staffRepository: StaffRepository
    private lateinit var addStaffUseCase: AddStaffUseCase

    @Before
    fun setUp() {
        staffRepository = mock()
        addStaffUseCase = AddStaffUseCase(staffRepository)
    }

    @Test
    fun addStaff_validDataWithCustomCredentials_returnsSuccessStaff() = runTest {
        val expectedStaff = Staff(
            id = "server_id_123",
            name = "John Doe",
            employeeId = "EMP001",
            faceEnrolled = false,
            faceImageUrl = null,
            faceEmbedding = null,
            faceImagePath = null
        )
        whenever(staffRepository.getStaffByEmployeeId("EMP001")).thenReturn(null)
        whenever(staffRepository.addStaff(any(), eq("john_emp"), eq("pass123"))).thenReturn(AppResult.Success(expectedStaff))

        val result = addStaffUseCase("John Doe", "EMP001", "john_emp", "pass123")

        assertTrue(result is AppResult.Success)
        val created = (result as AppResult.Success).data
        assertEquals("server_id_123", created.id)
        assertEquals("John Doe", created.name)
        assertEquals("EMP001", created.employeeId)
    }

    @Test
    fun addStaff_duplicateEmployeeId_returnsError() = runTest {
        val existing = Staff("id1", "Existing", "EMP001", false, null, null, null)
        whenever(staffRepository.getStaffByEmployeeId("EMP001")).thenReturn(existing)

        val result = addStaffUseCase("New Person", "EMP001")

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.DuplicateEmployeeId, (result as AppResult.Error).error)
    }
}
