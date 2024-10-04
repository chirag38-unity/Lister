package com.chirag_redij.lister.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
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
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Github
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

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
                Toast.makeText(context,
                    context.getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show()
                navigator.navigate(HomeScreenDestination){
                    popUpTo(SignInScreenDestination){
                        inclusive = true
                    }
                }
            }
            UserState.Loading -> {}
            else -> {
                scope.launch {
                    delay(1000)
                    loadingSessionComplete = true
                }
            }
        }
    }

    // Composable-----------------------------------------------------------------------------------
    Scaffold(

    ) { scaffoldPadding ->

        Box(modifier = Modifier
            .fillMaxSize()
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

                AnimatedVisibility(
                    loadingSessionComplete,
                    label = "Load login options"
                ) {
                    OutlinedButton(
                        onClick = {
                            action.startFlow()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Login",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.login_with_google),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    }

                }

            }

            AnimatedVisibility(
                loadingSessionComplete,
                label = stringResource(R.string.load_terms_and_conditions_and_privacy_policy),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {

                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)) {
                        append(stringResource(R.string.by_using_listerrr_you_are_agreeing_to_our))
                        append(" ")
                    }

                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                        pushStringAnnotation(tag = "URL", annotation = "https://sites.google.com/view/listerrr/terms-and-conditions")
                        append(stringResource(R.string.terms_of_service))
                        pop()
                    }

                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)) {
                        append(" ")
                        append(stringResource(R.string.and))
                        append(" ")
                    }

                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp, textDecoration = TextDecoration.Underline)) {
                        pushStringAnnotation(tag = "URL", annotation = "https://sites.google.com/view/listerrr/privacy-policy")
                        append(stringResource(R.string.privacy_policy))
                        pop()
                    }
                }

                ClickableText(
                    text = annotatedText,
                    modifier = Modifier
                        .fillMaxWidth()
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



