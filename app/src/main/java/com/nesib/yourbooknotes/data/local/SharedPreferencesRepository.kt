package com.nesib.yourbooknotes.data.local

import android.content.Context
import android.util.Log
import com.nesib.yourbooknotes.models.UserAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class SharedPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private var _currentUser: UserAuth? = null

    init {
        Log.d("mytag", "SharedPreferencesRepository is initialized")

    }
    fun getCurrentUser(): UserAuth? {
        if(_currentUser != null){
            return _currentUser
        }
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        val profileImage = sharedPreferences.getString("profileImage", "")
        val userId = sharedPreferences.getString("userId", null)
        val token = sharedPreferences.getString("token", null)
        val followingGenres = sharedPreferences.getString("genres", "") ?: ""
        _currentUser = UserAuth(username, email, profileImage, userId, token, followingGenres)
        return _currentUser
    }

    fun saveUser(userId: String, token: String) {
        editor.putString("userId", userId)
        editor.putString("token", token)
        editor.apply()
    }

    fun saveExtraUserDetail(username: String, email: String, profileImage: String) {
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("profileImage", profileImage)
        editor.apply()
    }

    fun clearUser() {
        editor.remove("userId")
        editor.remove("token")
        editor.remove("genres")
        editor.remove("username")
        editor.remove("email")
        editor.remove("profileImage")
        editor.apply()
    }

    fun saveFollowingGenres(genres: String) {
        editor.putString("genres", genres)
        editor.apply()
    }

//    private fun getFollowingGenres(): String {
//        return sharedPreferences.getString("genres", "")!!
//    }


}