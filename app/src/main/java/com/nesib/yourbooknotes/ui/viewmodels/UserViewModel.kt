package com.nesib.yourbooknotes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.nesib.yourbooknotes.data.local.SharedPreferencesRepository
import com.nesib.yourbooknotes.data.repositories.UserRepository
import com.nesib.yourbooknotes.models.*
import com.nesib.yourbooknotes.utils.Constants
import com.nesib.yourbooknotes.utils.Constants.CODE_AUTHENTICATION_FAIL
import com.nesib.yourbooknotes.utils.Constants.CODE_CREATION_SUCCESS
import com.nesib.yourbooknotes.utils.Constants.CODE_SERVER_ERROR
import com.nesib.yourbooknotes.utils.Constants.CODE_SUCCESS
import com.nesib.yourbooknotes.utils.Constants.CODE_VALIDATION_FAIL
import com.nesib.yourbooknotes.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private var userQuoteList = mutableListOf<Quote>()
    private val _user = MutableLiveData<DataState<UserResponse>>()
    val user: LiveData<DataState<UserResponse>>
        get() = _user

    private val _userQuotes = MutableLiveData<DataState<QuotesResponse>>()
    val userQuotes: LiveData<DataState<QuotesResponse>>
        get() = _userQuotes

    private val _users = MutableLiveData<DataState<UsersResponse>>()
    val users: LiveData<DataState<UsersResponse>>
        get() = _users

    private val sharedPreferencesRepository = SharedPreferencesRepository(getApplication())

    fun getMoreUserQuotes(userId: String? = null, page: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _userQuotes.postValue(DataState.Loading())
            val id = userId ?: sharedPreferencesRepository.getUser().userId!!
            val response = UserRepository.getMoreUserQuotes(id, page)
            handleQuotesResponse(response)
        }

    fun getUser(userId: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        if (_user.value == null) {
            _user.postValue(DataState.Loading())
            val id = userId ?: sharedPreferencesRepository.getUser().userId!!
            val response = UserRepository.getUser(id)
            handleUserResponse(response)
        }
    }

    fun getUsers(searchQuery: String = "") = viewModelScope.launch(Dispatchers.IO) {
        _users.postValue(DataState.Loading())
        val response = UserRepository.getUsers(searchQuery)
        handleUsersResponse(response)
    }

    fun clearUser() {
        sharedPreferencesRepository.clearUser()
    }

    fun followOrUnfollowUser(userId: String) {

    }

    fun saveFollowingGenres(genres: Map<String, String>) {

    }

    fun updateUser(
        username: String?,
        fullname: String?,
        email: String?,
        password: String?,
        bio: String?
    ) {

    }


    fun getSavedQuotes(userId: String) {

    }

    fun postSavedQuote(quoteId: String) {

    }

    private fun handleQuotesResponse(response: Response<QuotesResponse>) {
        when (response.code()) {
            CODE_SUCCESS -> {
                response.body()!!.quotes.forEach { quote -> userQuoteList.add(quote) }
                _userQuotes.postValue(DataState.Success(QuotesResponse(userQuoteList.toList())))
            }
            CODE_CREATION_SUCCESS -> {

            }
            CODE_VALIDATION_FAIL -> {

            }
            CODE_SERVER_ERROR -> {
                _userQuotes.postValue(DataState.Fail(message = "Server error"))
            }
            CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _userQuotes.postValue(DataState.Fail(message = authFailResponse.message))
            }
        }
    }

    private fun handleUsersResponse(response: Response<UsersResponse>) {
        when (response.code()) {
            CODE_SUCCESS -> {
                _users.postValue(DataState.Success(response.body()))
            }
            CODE_SERVER_ERROR -> {
                _users.postValue(DataState.Fail(message = "Server error"))
            }
        }
    }

    private fun handleUserResponse(response: Response<UserResponse>) {
        when (response.code()) {
            CODE_SUCCESS -> {
                userQuoteList = response.body()!!.user!!.quotes!!.toMutableList()
                _user.postValue(DataState.Success(response.body()))
            }
            CODE_CREATION_SUCCESS -> {
                _user.postValue(DataState.Success(response.body()))
            }
            CODE_VALIDATION_FAIL -> {
                val authFailResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _user.postValue(DataState.Fail(message = authFailResponse.message))
            }
            CODE_SERVER_ERROR -> {
                _user.postValue(DataState.Fail(message = "Server error"))
            }
            Constants.CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _user.postValue(DataState.Fail(message = authFailResponse.message))
            }
        }
    }

}