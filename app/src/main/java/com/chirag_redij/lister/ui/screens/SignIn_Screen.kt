package com.chirag_redij.lister.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chirag_redij.lister.R
import com.chirag_redij.lister.di.SupabaseClient.client
import com.chirag_redij.lister.presentation.sign_in.UserState
import com.chirag_redij.lister.ui.screens.destinations.HomeScreenDestination
import com.chirag_redij.lister.ui.screens.destinations.SignInScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import timber.log.Timber

@OptIn(AuthUiExperimental::class, ExperimentalMaterial3Api::class)
@RootNavGraph(
    start = true
)
@Destination
@Composable
fun SignInScreen(
    navigator: DestinationsNavigator,
    signInViewModel: SignInScreenViewModel = hiltViewModel(),
) {
    // Lottie States -------------------------------------------------------------------------------
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.list_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )
    val scope = rememberCoroutineScope()

    var loadingSessionComplete by remember {
        mutableStateOf(false)
    }

    // Auth States ---------------------------------------------------------------------------------
    val context = LocalContext.current
    val state by signInViewModel.userState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val action = client.composeAuth.rememberSignInWithGoogle(
        onResult = { result -> signInViewModel.checkGoogleLoginStatus(result) },
        fallback = {}
    )

    val signInWithGithub : () -> Unit = {
        coroutineScope.launch {
            client.auth.signInWith(Github)

        }
    }

    var userEmail by remember {
        mutableStateOf("")
    }

    var userPassword by remember {
        mutableStateOf("")
    }

    // State listeners------------------------------------------------------------------------------

    LaunchedEffect(key1 = state) {
        when (state) {
            is UserState.Error -> {
                Toast.makeText(context, (state as UserState.Error).message, Toast.LENGTH_SHORT).show()
                Timber.tag("LoginError").d((state as UserState.Error).toString())
                signInViewModel.resetUserState()
            }
            is UserState.Success -> {
                Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                navigator.navigate(HomeScreenDestination){
                    popUpTo(SignInScreenDestination){
                        inclusive = true
                    }
                }
            }
            is UserState.UnAuthenticated -> {
                scope.launch {
                    delay(1000)
                    loadingSessionComplete = true
                }
            }
            UserState.LoggedOut -> {
                scope.launch {
                    delay(1000)
                    loadingSessionComplete = true
                }
            }
            UserState.AccountDeleted -> {
                scope.launch {
                    delay(1000)
                    loadingSessionComplete = true
                }
            }
            else -> {}
        }
    }

    // Composable-----------------------------------------------------------------------------------
    Scaffold(

    ) { scaffoldPadding ->

        Box(modifier = Modifier.fillMaxSize()
            .padding(scaffoldPadding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    modifier = Modifier,
                    composition = lottieComposition,
                    progress = { progress }
                )

                AnimatedVisibility(loadingSessionComplete) {
                    OutlinedButton(onClick = {
                        action.startFlow()
//                signInWithGithub()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Login",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Login with Google",
                            color = Color.Black
                        )

                    }

//                    Column (
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//
//                        OutlinedEmailField(
//                            value = userEmail,
//                            onValueChange = {
//                                userEmail = it
//                            }
//                        )
//
//                        OutlinedTextField(
//                            placeholder = {
//                                Text("Enter Password")
//                            },
//                            value = userPassword,
//                            onValueChange = {
//                                userPassword = it
//                            }
//                        )
//
//                        OutlinedButton(onClick = {
//                            signInViewModel.signInWithEmailPassword(userEmail, userPassword)
//                        }) {
//                            Text(
//                                text = "Login",
//                                color = Color.Black
//                            )
//                        }
//
//                    }

                }



            }

            AnimatedVisibility(
                loadingSessionComplete,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {

                val annotatedText = buildAnnotatedString {
                    append("By using Listerrr, you are agreeing to our ")

                    withStyle(style = SpanStyle(color = Color.Blue, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                        pushStringAnnotation(tag = "URL", annotation = "https://sites.google.com/view/listerrr/terms-and-conditions")
                        append("Terms of Service")
                        pop()
                    }

                    append(" and ")

                    withStyle(style = SpanStyle(color = Color.Blue, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                        pushStringAnnotation(tag = "URL", annotation = "https://sites.google.com/view/listerrr/privacy-policy")
                        append("Privacy Policy")
                        pop()
                    }
                }

                ClickableText(
                    text = annotatedText,
                    modifier = Modifier.fillMaxWidth()
                        .padding(20.dp),
                    softWrap = true,
                    overflow = TextOverflow.Visible,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let { annotation ->
//                                println("Clicked on: ${annotation.item}")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )

            }

        }


    }

}



