package com.chirag_redij.lister.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.chirag_redij.lister.presentation.sign_in.SignInViewModel
import com.google.firebase.auth.FirebaseUser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import timber.log.Timber

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    userData: FirebaseUser?,
    signInViewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val signOutClick: () -> Unit = {
        coroutineScope.launch {
            signInViewModel.signOut()
            Toast.makeText(context, "Sign Out Successful", Toast.LENGTH_SHORT).show()
            navigator.popBackStack()
        }
    }

    Timber.tag("User Profile->").d((userData ?: "No url").toString())
    Timber.tag("Profile Url->").d((userData?.photoUrl ?: "No url").toString())


    // Composable-----------------------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (userData?.photoUrl != null) {
            AsyncImage(
                model = userData.photoUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (userData?.displayName != null) {
            Text(
                text = userData.displayName ?: "User Name not available",
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = signOutClick) {
            Text(text = "Sign out")
        }

    }
}