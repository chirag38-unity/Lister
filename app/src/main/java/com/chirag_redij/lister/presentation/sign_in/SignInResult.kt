package com.chirag_redij.lister.presentation.sign_in

import com.google.firebase.auth.FirebaseUser

class SignInResult(
    val data: FirebaseUser?,
    val errorMessage: String? = null
)
