package com.chirag_redij.lister.presentation.sign_in

import io.github.jan.supabase.gotrue.user.UserInfo

sealed class UserState {
    data object Loading: UserState()
    data object UnAuthenticated : UserState()
    data object LoggedOut : UserState()
    data class Success(val user: User): UserState()
    data class Error(val message: String?): UserState()
}

data class User(
    val userId: UserInfo? = null,
    val accessToken: String? = null
)
