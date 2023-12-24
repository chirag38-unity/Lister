package com.chirag_redij.lister.presentation.sign_in

data class SignInState(
    val isSignInSuccess: Boolean = false,
    val signInError: String? = null
)
