package com.chirag_redij.lister.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirag_redij.lister.presentation.sign_in.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInScreenViewModel @Inject constructor(
    private val signInProvider: SignInProvider
) : ViewModel() {

    val userState = signInProvider.state

    fun signInWithEmailPassword(userEmail : String, userPassword : String) {
        viewModelScope.launch {
            signInProvider.signUpWithPassword(userEmail, userPassword)
        }
    }

    fun checkGoogleLoginStatus(result: NativeSignInResult){
        viewModelScope.launch {
            signInProvider.checkGoogleLoginStatus(result)
        }
    }

    fun resetUserState() {
        signInProvider.resetUserState()
    }



}