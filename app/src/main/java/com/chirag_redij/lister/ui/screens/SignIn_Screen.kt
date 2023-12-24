package com.chirag_redij.lister.ui.screens

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chirag_redij.lister.R
import com.chirag_redij.lister.presentation.sign_in.SignInViewModel
import com.chirag_redij.lister.ui.screens.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination(
    start = true
)
@Composable
fun SignInScreen(
    navigator: DestinationsNavigator,
    signInViewModel: SignInViewModel = hiltViewModel(),
) {
    // Lottie States -------------------------------------------------------------------------------
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.list_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    // Auth States ---------------------------------------------------------------------------------
    val context = LocalContext.current
    val state by signInViewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = signInViewModel.signIn(result.data ?: return@launch)
                    signInViewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    val onSignInClick: () -> Unit = {
        coroutineScope.launch {
            val signInIntentSender = signInViewModel.returnSignInIntent()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    // State listeners------------------------------------------------------------------------------
    LaunchedEffect(key1 = Unit) {
        if (signInViewModel.getSignedInUser() != null) {
            navigator.navigate(HomeScreenDestination(signInViewModel.getSignedInUser()))
        }
    }

    LaunchedEffect(key1 = state.isSignInSuccess) {
        if (state.isSignInSuccess) {
            Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
            navigator.navigate(HomeScreenDestination(signInViewModel.getSignedInUser()))
            signInViewModel.resetState()
        }

    }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    // Composable-----------------------------------------------------------------------------------
    Scaffold(

    ) { scaffoldPadding ->
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
            OutlinedButton(onClick = onSignInClick) {
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

        }
    }

}



