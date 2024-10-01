package com.chirag_redij.lister.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.chirag_redij.lister.MainActivity
import com.chirag_redij.lister.R
import com.chirag_redij.lister.SharedPreferenceHelper
import com.chirag_redij.lister.di.ServiceClient.serviceClient
import com.chirag_redij.lister.di.SupabaseClient.client
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState = _actionState.asStateFlow()

    private val _state = MutableStateFlow<UserState>(UserState.Loading)
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
                    client.auth.refreshCurrentSession()
                    _state.value = UserState.Success(
                        User(
                            userId = getToken()?.let { client.auth.retrieveUser(it) }
                        )
                    )

                    addDynamicShortCut()

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
            Timber.tag("Provider").d("Getting User")
            try {
                val token = getToken()
                if (token.isNullOrEmpty()) {
                    _state.value = UserState.UnAuthenticated

                    removeDynamicShortCut()

                } else {
                    val user = client.auth.retrieveUser(token)
                    client.auth.refreshCurrentSession()

                    Timber.tag("USER_ROLE").d("Role -> " + user?.role)
                    Timber.tag("USER_ROLE").d("AUD -> " + user?.aud)
                    Timber.tag("USER_ROLE").d("DATA -> " + user?.userMetadata)

                    saveToken()
                    _state.value = UserState.Success(
                        User(
                            userId = client.auth.retrieveUser(token),
                            accessToken = client.auth.currentAccessTokenOrNull()
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.tag("Provider").d(e)

                removeDynamicShortCut()

                sharedPref.clearPreferences()
                _state.value = UserState.Error(e.message)
            }
        }
    }

    fun logout() {
        scope.launch {
            try {
                client.auth.signOut()
                sharedPref.clearPreferences()
                removeDynamicShortCut()
                _state.value = UserState.LoggedOut
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAccount() {
        scope.launch {
            try {
                val user = client.auth.currentUserOrNull()
//                client.auth.admin.deleteUser(user?.id ?: "")
                serviceClient.auth.admin.deleteUser(user?.id ?: "")
                sharedPref.clearPreferences()
                _state.value = UserState.AccountDeleted
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveUserSession(userSession: UserSession) {


        scope.launch {

            Timber.tag("Provider").d("User Logged -> " + userSession.user)
            Timber.tag("Provider").d("User Token -> " + userSession.accessToken)

            sharedPref.saveStringData("accessToken", userSession.accessToken)
            _state.emit(UserState.Success(
                User(
                    userId = userSession.user
                )
            ))

        }

    }

    fun parseViewAction(action: String?) {
        if (action == Intent.ACTION_VIEW) {
            Timber.tag("Intent").d("Action set")
            scope.launch {
                _actionState.emit(ActionState.Recieved)
            }
        } else {
            Timber.tag("Intent").d("Action reset")
            scope.launch {
                _actionState.emit(ActionState.Idle)
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

    private fun addDynamicShortCut() {
        val shortcut = ShortcutInfoCompat.Builder(appContext, "write_shortcut")
            .setShortLabel("Add Note")
            .setIcon(IconCompat.createWithResource(appContext, R.drawable.write_shortcut))
            .setAlwaysBadged()
            .setIntent(
                Intent(appContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                }
            )
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(appContext, shortcut)
    }

    private fun removeDynamicShortCut() {

        scope.launch {
            Timber.tag("Intent").d("Action reset")
            _actionState.emit(ActionState.Idle)
        }

        ShortcutManagerCompat.removeDynamicShortcuts(appContext, listOf("write_shortcut"))
    }

}