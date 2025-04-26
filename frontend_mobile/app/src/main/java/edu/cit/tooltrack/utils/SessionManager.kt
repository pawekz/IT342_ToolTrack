package edu.cit.tooltrack.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import edu.cit.tooltrack.api.TokenResponse
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.Date

/**
 * SessionManager handles JWT token storage and retrieval
 * It also provides methods to decode the JWT token and extract user information
 * Token is persisted until expiration or explicit logout
 */
class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val PREF_NAME = "ToolTrack_Prefs"
        const val USER_TOKEN = "user_token"
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_NAME = "user_name" // Keeping for backward compatibility
        const val USER_FIRST_NAME = "user_first_name"
        const val USER_LAST_NAME = "user_last_name"
        const val USER_EMAIL = "user_email"
        const val USER_ROLE = "user_role"
        const val TOKEN_EXPIRY = "token_expiry" // Store token expiration timestamp
    }

    /**
     * Function to save auth token and user details
     */
    fun saveAuthToken(token: TokenResponse) {
        editor.putString(USER_TOKEN, token.token)
        editor.putBoolean(IS_LOGGED_IN, true)

        // Decode and save user details from token
        try {
            val payload = decodeJWT(token.token)

            // Extract firstName and lastName directly from the token
            val firstName = payload.optString("firstName", "")
            val lastName = payload.optString("lastName", "")

            // Construct full name from firstName and lastName for backward compatibility
            val fullName = if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
                "$firstName $lastName".trim()
            } else {
                payload.optString("name", "") // Fallback to name field if it exists
            }

            // Save both full name (for backward compatibility) and split names
            editor.putString(USER_NAME, fullName)
            editor.putString(USER_FIRST_NAME, firstName)
            editor.putString(USER_LAST_NAME, lastName)
            editor.putString(USER_EMAIL, payload.optString("sub", ""))
            editor.putString(USER_ROLE, payload.optString("role", ""))

            // Save token expiration time if available
            if (payload.has("exp")) {
                val expiryTime = payload.getLong("exp") * 1000 // Convert seconds to milliseconds
                editor.putLong(TOKEN_EXPIRY, expiryTime)
                Log.d("SessionManager", "Token will expire at: ${Date(expiryTime)}")
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "Error decoding JWT", e)
        }

        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Function to check if user is logged in
     * Also checks if the token has expired
     */
    fun isLoggedIn(): Boolean {
        val isLoggedInFlag = prefs.getBoolean(IS_LOGGED_IN, false)

        // If not logged in according to flag, return false immediately
        if (!isLoggedInFlag) return false

        // Check token expiration
        if (isTokenExpired()) {
            // If token is expired, clear the session and return false
            Log.d("SessionManager", "Token has expired, logging out")
            clearSession()
            return false
        }

        return true
    }

    /**
     * Function to check if the token has expired
     */
    fun isTokenExpired(): Boolean {
        val expiryTime = prefs.getLong(TOKEN_EXPIRY, 0)

        // If expiration time is not stored or is 0, try to extract it from the token
        if (expiryTime == 0L) {
            val token = fetchAuthToken() ?: return true // No token means "expired"

            try {
                val payload = decodeJWT(token)
                if (payload.has("exp")) {
                    val extractedExpiry = payload.getLong("exp") * 1000 // Convert to milliseconds

                    // Save for future reference
                    editor.putLong(TOKEN_EXPIRY, extractedExpiry)
                    editor.apply()

                    return System.currentTimeMillis() > extractedExpiry
                }
            } catch (e: Exception) {
                Log.e("SessionManager", "Error checking token expiration", e)
                return true // If we can't decode the token, consider it expired
            }

            // If no expiration in token, default to not expired
            return false
        }

        // Check if current time is past expiration time
        return System.currentTimeMillis() > expiryTime
    }

    /**
     * Function to get user name (full name)
     * If the full name is not available, it will combine first and last name
     */
    fun getUserName(): String {
        val storedName = prefs.getString(USER_NAME, "")
        if (!storedName.isNullOrEmpty()) {
            return storedName
        }

        // If full name is not available, combine first and last name
        val firstName = getUserFirstName()
        val lastName = getUserLastName()
        return if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            "$firstName $lastName".trim()
        } else {
            ""
        }
    }

    /**
     * Function to get user first name
     */
    fun getUserFirstName(): String {
        return prefs.getString(USER_FIRST_NAME, "") ?: ""
    }

    /**
     * Function to get user last name
     */
    fun getUserLastName(): String {
        return prefs.getString(USER_LAST_NAME, "") ?: ""
    }

    /**
     * Function to get user email
     */
    fun getUserEmail(): String {
        return prefs.getString(USER_EMAIL, "") ?: ""
    }

    /**
     * Function to get user role
     */
    fun getUserRole(): String {
        return prefs.getString(USER_ROLE, "") ?: ""
    }

    /**
     * Function to clear session (logout)
     * This removes all stored user data and tokens
     */
    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    /**
     * Function to decode JWT token
     */
    private fun decodeJWT(token: String): JSONObject {
        try {
            // Split the token into parts
            val parts = token.split(".")
            if (parts.size < 2) {
                throw Exception("Invalid JWT token format")
            }

            // Get the payload part (second part)
            val payload = parts[1]

            // Base64 decode the payload
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, StandardCharsets.UTF_8)

            // Parse the JSON
            return JSONObject(decodedString)
        } catch (e: Exception) {
            Log.e("SessionManager", "Error decoding JWT", e)
            throw e
        }
    }
}
