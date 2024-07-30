package com.chirag_redij.lister.presentation.sign_in

import android.content.Context
import com.chirag_redij.lister.SharedPreferenceHelper
import com.chirag_redij.lister.di.SupabaseClient.client
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInProvider @Inject constructor(
    private val sharedPref: SharedPreferenceHelper,
    private val appContext: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow<UserState>(UserState.UnAuthenticated)
    val state = _state.asStateFlow()

    init {
        getSignedInUser()
    }

    fun signUpWithPassword(
        userEmail : String,
        userPassword : String
    ) {
        scope.launch {
            try {
                client.auth.signUpWith(Email){
                    email = userEmail
                    password = userPassword
                }
                saveToken()
                _state.value = UserState.Success(
                    User(
                        userId = getToken()?.let { client.auth.retrieveUser(it) },
                        accessToken = getToken()
                    )
                )
            } catch (e : Exception) {
                _state.value = UserState.Error(e.message)
            }
        }
    }

    fun checkGoogleLoginStatus(result: NativeSignInResult) {
        scope.launch {
            when (result) {
                is NativeSignInResult.Success -> {
                    saveToken()
                    _state.value = UserState.Success(
                        User(
                            userId = getToken()?.let { client.auth.retrieveUser(it) }
                        )
                    )

                }
                is NativeSignInResult.ClosedByUser -> {}
                is NativeSignInResult.Error -> {
                    val message = result.message
                    _state.value = UserState.Error(message)
                }

                is NativeSignInResult.NetworkError -> {
                    val message = result.message
                    _state.value = UserState.Error(message)
                }
            }
        }
    }

    fun resetUserState() {
        scope.launch {
            _state.emit(UserState.UnAuthenticated)
        }
    }

    fun getSignedInUser() {
        scope.launch {
            try {
                val token = getToken()
                if (token.isNullOrEmpty()) {
                    _state.value = UserState.UnAuthenticated
                } else {
                    client.auth.retrieveUser(token)
                    client.auth.refreshCurrentSession()
                    saveToken()
                    _state.value = UserState.Success(
                        User(
                            userId = client.auth.retrieveUser(token),
                            accessToken = client.auth.currentAccessTokenOrNull()
                        )
                    )
                }
            } catch (e: RestException) {
                Timber.d(e)
                sharedPref.clearPreferences()
                _state.value = UserState.Error(e.error)
            }
        }
    }

    fun logout() {
        scope.launch {
            try {
                client.auth.signOut()
                sharedPref.clearPreferences()
                _state.value = UserState.LoggedOut
            } catch (e: Exception) {
                _state.value = UserState.Error(e.message ?: "")
            }
        }
    }

    // Tokens --------------------------------------------------------------------------------------

    private fun saveToken() {
        scope.launch {
            val accessToken = client.auth.currentAccessTokenOrNull()
            sharedPref.saveStringData("accessToken", accessToken)
        }

    }

    private fun getToken(): String? {
        return sharedPref.getStringData("accessToken")
    }

}