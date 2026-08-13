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
    fun addStaff_validData_returnsSuccessStaff() = runTest {
        whenever(staffRepository.getStaffByEmployeeId("EMP001")).thenReturn(null)
        whenever(staffRepository.addStaff(any())).thenReturn(AppResult.Success(Unit))

        val result = addStaffUseCase("John Doe", "EMP001")

        assertTrue(result is AppResult.Success)
        val created = (result as AppResult.Success).data
        assertEquals("John Doe", created.name)
        assertEquals("EMP001", created.employeeId)
    }

    @Test
    fun addStaff_duplicateEmployeeId_returnsError() = runTest {
        val existing = Staff("id1", "Existing", "EMP001", null, null)
        whenever(staffRepository.getStaffByEmployeeId("EMP001")).thenReturn(existing)

        val result = addStaffUseCase("New Person", "EMP001")

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.DuplicateEmployeeId, (result as AppResult.Error).error)
    }
}
