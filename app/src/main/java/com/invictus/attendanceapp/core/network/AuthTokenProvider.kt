package com.invictus.attendanceapp.core.network

import android.content.Context
import com.invictus.attendanceapp.feature.auth.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

interface AuthTokenProvider {
    fun getToken(): String?
    fun saveToken(token: String)
    fun getUserRole(): UserRole?
    fun saveUserRole(role: UserRole)
    fun getStaffId(): String?
    fun saveStaffId(staffId: String)
    fun clearSession()
}

@Singleton
class AuthTokenProviderImpl @Inject constructor(
    @ApplicationContext context: Context
) : AuthTokenProvider {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun getToken(): String? {
        return prefs.getString("auth_bearer_token", null)
    }

    override fun saveToken(token: String) {
        prefs.edit { putString("auth_bearer_token", token) }
    }

    override fun getUserRole(): UserRole? {
        val roleStr = prefs.getString("auth_user_role", null) ?: return null
        return if (roleStr.equals("ADMIN", ignoreCase = true)) UserRole.ADMIN else UserRole.STAFF
    }

    override fun saveUserRole(role: UserRole) {
        prefs.edit { putString("auth_user_role", role.name) }
    }

    override fun getStaffId(): String? {
        return prefs.getString("auth_staff_id", null)
    }

    override fun saveStaffId(staffId: String) {
        prefs.edit { putString("auth_staff_id", staffId) }
    }

    override fun clearSession() {
        prefs.edit {
            remove("auth_bearer_token")
                .remove("auth_user_role")
                .remove("auth_staff_id")
        }
    }
}
