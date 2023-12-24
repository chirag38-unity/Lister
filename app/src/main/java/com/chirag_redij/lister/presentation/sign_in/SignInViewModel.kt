package com.chirag_redij.lister.presentation.sign_in

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleAuthUIClient: GoogleAuthUIClient
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    suspend fun signIn(data: Intent): SignInResult {
        return googleAuthUIClient.signInWithIntent(data)
    }

    suspend fun returnSignInIntent(): IntentSender? {
        return googleAuthUIClient.signIn()
    }

    suspend fun signOut() {
        googleAuthUIClient.signOut()
    }

    fun getSignedInUser(): FirebaseUser? {
        return googleAuthUIClient.getSignedInUser()
    }

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccess = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

}