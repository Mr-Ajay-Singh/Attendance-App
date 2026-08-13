package com.invictus.attendanceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.invictus.attendanceapp.feature.attendance.presentation.markattendance.MarkAttendanceScreen
import com.invictus.attendanceapp.feature.attendance.presentation.markattendance.MarkAttendanceViewModel
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.presentation.LoginScreen
import com.invictus.attendanceapp.feature.auth.presentation.LoginViewModel
import com.invictus.attendanceapp.feature.staff.presentation.addstaff.AddStaffScreen
import com.invictus.attendanceapp.feature.staff.presentation.addstaff.AddStaffViewModel
import com.invictus.attendanceapp.feature.staff.presentation.enrollment.FaceEnrollmentScreen
import com.invictus.attendanceapp.feature.staff.presentation.enrollment.FaceEnrollmentViewModel
import com.invictus.attendanceapp.feature.staff.presentation.profile.StaffProfileScreen
import com.invictus.attendanceapp.feature.staff.presentation.profile.StaffProfileViewModel
import com.invictus.attendanceapp.feature.staff.presentation.stafflist.StaffListScreen
import com.invictus.attendanceapp.feature.staff.presentation.stafflist.StaffListViewModel

object Screen {
    const val Login = "login"
    const val AdminStaffList = "admin/staff"
    const val AdminAddStaff = "admin/add-staff"
    const val AdminEnrollFace = "admin/enroll-face/{staffId}"
    const val AdminProfile = "admin/profile/{staffId}"
    const val StaffAttendance = "staff/attendance"

    fun createEnrollFaceRoute(staffId: String) = "admin/enroll-face/$staffId"
    fun createProfileRoute(staffId: String) = "admin/profile/$staffId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login,
        modifier = modifier
    ) {
        composable(Screen.Login) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { user ->
                    if (user.role == UserRole.ADMIN) {
                        navController.navigate(Screen.AdminStaffList) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.StaffAttendance) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.AdminStaffList) {
            val viewModel: StaffListViewModel = hiltViewModel()
            StaffListScreen(
                viewModel = viewModel,
                onAddStaffClick = { navController.navigate(Screen.AdminAddStaff) },
                onStaffClick = { staffId ->
                    navController.navigate(Screen.createProfileRoute(staffId))
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminAddStaff) {
            val viewModel: AddStaffViewModel = hiltViewModel()
            AddStaffScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStaffAddedNavigateToEnrollment = { staffId ->
                    navController.navigate(Screen.createEnrollFaceRoute(staffId)) {
                        popUpTo(Screen.AdminStaffList)
                    }
                }
            )
        }

        composable(
            route = Screen.AdminEnrollFace,
            arguments = listOf(navArgument("staffId") { type = NavType.StringType })
        ) {
            val viewModel: FaceEnrollmentViewModel = hiltViewModel()
            FaceEnrollmentScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEnrollmentSuccess = {
                    navController.popBackStack(Screen.AdminStaffList, inclusive = false)
                }
            )
        }

        composable(
            route = Screen.AdminProfile,
            arguments = listOf(navArgument("staffId") { type = NavType.StringType })
        ) {
            val viewModel: StaffProfileViewModel = hiltViewModel()
            StaffProfileScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEnrollFaceClick = { staffId ->
                    navController.navigate(Screen.createEnrollFaceRoute(staffId))
                }
            )
        }

        composable(Screen.StaffAttendance) {
            val viewModel: MarkAttendanceViewModel = hiltViewModel()
            MarkAttendanceScreen(
                viewModel = viewModel,
                onLogoutClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
