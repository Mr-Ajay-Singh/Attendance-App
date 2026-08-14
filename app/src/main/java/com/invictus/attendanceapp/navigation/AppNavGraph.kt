package com.invictus.attendanceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.invictus.attendanceapp.core.network.AuthTokenProvider
import com.invictus.attendanceapp.feature.attendance.presentation.markattendance.MarkAttendanceScreen
import com.invictus.attendanceapp.feature.attendance.presentation.markattendance.MarkAttendanceViewModel
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import com.invictus.attendanceapp.feature.auth.presentation.LoginScreen
import com.invictus.attendanceapp.feature.auth.presentation.LoginViewModel
import com.invictus.attendanceapp.feature.auth.presentation.RoleSelectionScreen
import com.invictus.attendanceapp.feature.auth.presentation.setupadmin.SetupAdminScreen
import com.invictus.attendanceapp.feature.auth.presentation.setupadmin.SetupAdminViewModel
import com.invictus.attendanceapp.feature.staff.presentation.addstaff.AddStaffScreen
import com.invictus.attendanceapp.feature.staff.presentation.addstaff.AddStaffViewModel
import com.invictus.attendanceapp.feature.staff.presentation.enrollment.FaceEnrollmentScreen
import com.invictus.attendanceapp.feature.staff.presentation.enrollment.FaceEnrollmentViewModel
import com.invictus.attendanceapp.feature.staff.presentation.profile.StaffProfileScreen
import com.invictus.attendanceapp.feature.staff.presentation.profile.StaffProfileViewModel
import com.invictus.attendanceapp.feature.staff.presentation.stafflist.StaffListScreen
import com.invictus.attendanceapp.feature.staff.presentation.stafflist.StaffListViewModel

object Screen {
    const val RoleSelection = "role_selection"
    const val Login = "login/{role}"
    const val AdminSetup = "admin/setup"
    const val AdminStaffList = "admin/staff"
    const val AdminAddStaff = "admin/add-staff"
    const val AdminEnrollFace = "admin/enroll-face/{staffId}"
    const val AdminProfile = "admin/profile/{staffId}"
    const val StaffAttendance = "staff/attendance"

    fun createLoginRoute(role: UserRole) = "login/${role.name}"
    fun createEnrollFaceRoute(staffId: String) = "admin/enroll-face/$staffId"
    fun createProfileRoute(staffId: String) = "admin/profile/$staffId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    tokenProvider: AuthTokenProvider,
    modifier: Modifier = Modifier
) {
    val initialDestination = remember(tokenProvider) {
        val token = tokenProvider.getToken()
        val role = tokenProvider.getUserRole()
        when {
            token.isNullOrBlank() -> Screen.RoleSelection
            role == UserRole.ADMIN -> Screen.AdminStaffList
            else -> Screen.StaffAttendance
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination,
        modifier = modifier
    ) {
        composable(Screen.RoleSelection) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate(Screen.createLoginRoute(role))
                }
            )
        }

        composable(
            route = Screen.Login,
            arguments = listOf(navArgument("role") { type = NavType.StringType; defaultValue = "STAFF" })
        ) { backStackEntry ->
            val roleStr = backStackEntry.arguments?.getString("role") ?: "STAFF"
            val selectedRole = if (roleStr.equals("ADMIN", ignoreCase = true)) UserRole.ADMIN else UserRole.STAFF
            val viewModel: LoginViewModel = hiltViewModel()

            LoginScreen(
                selectedRole = selectedRole,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSetupAdminClick = { navController.navigate(Screen.AdminSetup) },
                onLoginSuccess = { user ->
                    if (user.role == UserRole.ADMIN) {
                        navController.navigate(Screen.AdminStaffList) {
                            popUpTo(Screen.RoleSelection) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.StaffAttendance) {
                            popUpTo(Screen.RoleSelection) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.AdminSetup) {
            val viewModel: SetupAdminViewModel = hiltViewModel()
            SetupAdminScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onAdminCreatedSuccess = {
                    navController.navigate(Screen.AdminStaffList) {
                        popUpTo(Screen.RoleSelection) { inclusive = true }
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
                    tokenProvider.clearSession()
                    navController.navigate(Screen.RoleSelection) {
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
                    tokenProvider.clearSession()
                    navController.navigate(Screen.RoleSelection) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
