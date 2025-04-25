package edu.cit.tooltrack.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * SessionManager handles JWT token storage and retrieval
 * It also provides methods to decode the JWT token and extract user information
 */
class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val PREF_NAME = "ToolTrack_Prefs"
        const val USER_TOKEN = "user_token"
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_ROLE = "user_role"
    }

    /**
     * Function to save auth token and user details
     */
    fun saveAuthToken(token: String) {
        editor.putString(USER_TOKEN, token)
        editor.putBoolean(IS_LOGGED_IN, true)
        
        // Decode and save user details from token
        try {
            val payload = decodeJWT(token)
            editor.putString(USER_NAME, payload.optString("name", ""))
            editor.putString(USER_EMAIL, payload.optString("sub", ""))
            editor.putString(USER_ROLE, payload.optString("role", ""))
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
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    /**
     * Function to get user name
     */
    fun getUserName(): String {
        return prefs.getString(USER_NAME, "") ?: ""
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