package com.chirag_redij.lister

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.chirag_redij.lister.di.SupabaseClient
import com.chirag_redij.lister.presentation.sign_in.SignInProvider
import com.chirag_redij.lister.ui.screens.NavGraphs
import com.chirag_redij.lister.ui.theme.ListerTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.requestUpdateFlow
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.gotrue.handleDeeplinks
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var signInProvider: SignInProvider

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        checkUpdates()

        super.onCreate(savedInstanceState)
        Timber.tag("Intent").d("Intent Action -> %s", intent.action)
        Timber.tag("Intent").d(intent.data?.getQueryParameter("code"))

        signInProvider.parseViewAction(intent.action)

        SupabaseClient.client.handleDeeplinks(intent){
            signInProvider.saveUserSession(it)
        }
        setContent {
            ListerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    private fun checkUpdates() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val isUpdateAllowed = when(updateType) {
                    AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                    AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                    else -> false
                }

                if (isUpdateAvailable && isUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        this,
                        123
                    )
                }

            }.addOnFailureListener { exception ->
                Timber.e(exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                Timber.e("Something went wrong while updating the app")
            }
        }

    }

}