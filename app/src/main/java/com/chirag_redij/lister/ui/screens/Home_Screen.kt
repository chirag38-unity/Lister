package com.chirag_redij.lister.ui.screens

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirag_redij.lister.MainActivity
import com.chirag_redij.lister.R
import com.chirag_redij.lister.presentation.sign_in.ActionState
import com.chirag_redij.lister.presentation.sign_in.UserState
import com.chirag_redij.lister.ui.components.DropDownItem
import com.chirag_redij.lister.ui.components.ListerTopBar
import com.chirag_redij.lister.ui.components.SwipeableListItemComposable
import com.chirag_redij.lister.ui.screens.destinations.HomeScreenDestination
import com.chirag_redij.lister.ui.screens.destinations.SignInScreenDestination
import com.google.android.play.core.review.ReviewManagerFactory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userState = homeScreenViewModel.userState.collectAsState()
    val actionState by homeScreenViewModel.actionState.collectAsState()
    val list = homeScreenViewModel.notesList

    val randomThreshold = 0.2

    var openDialog by remember {
        mutableStateOf(false)
    }

    var userInfo by remember {
        mutableStateOf<UserInfo?>(null)
    }

    val deleteAccount : () -> Unit = {
        coroutineScope.launch {
            homeScreenViewModel.deleteAccount()
        }
    }

    val signOutClick: () -> Unit = {
        coroutineScope.launch {
            homeScreenViewModel.logout()
        }
    }

    val addPinnedFunction : () -> Unit = {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService<ShortcutManager>(context, ShortcutManager::class.java)!!
            if (shortcutManager.isRequestPinShortcutSupported) {
                val shortcut = ShortcutInfo.Builder(context, "pinned_shortcut")
                    .setShortLabel(context.getString(R.string.add_note))
                    .setIcon(
                        android.graphics.drawable.Icon.createWithResource(
                            context,
                            R.mipmap.short_cut_launcher
                        )
                    )
                    .setIntent(
                        Intent(context, MainActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                            putExtra("shortcut_id", "pinned")
                        }
                    )
                    .build()

                val callbackIntent = shortcutManager.createShortcutResultIntent(shortcut)
                val successPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    callbackIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                shortcutManager.requestPinShortcut(shortcut, successPendingIntent.intentSender)
            }
        }
    }

    val showReviewDialog : () -> Unit = {

        try {
            val reviewManager = ReviewManagerFactory.create(context)
            reviewManager.requestReviewFlow().addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    reviewManager.launchReviewFlow(context as MainActivity, request.result)
                }
            }
        } catch (e : Exception) {
            Timber.tag("Review").d(e.toString())
        }

    }

    val maybeTriggerInAppReview : () -> Unit = {
        // Generate a random number between 0.0 and 1.0
        val randomValue = Random.nextDouble(0.0, 1.0)

        // Check if random value is less than the threshold
        if (randomValue < randomThreshold) {
            showReviewDialog()
        }
    }

    LaunchedEffect (true) { maybeTriggerInAppReview() }

    LaunchedEffect(actionState) {
        if ( actionState == ActionState.Recieved ) {

            coroutineScope.launch {
                openDialog = true
                Timber.tag("Intent").d("Action shown")
                delay(500)
                homeScreenViewModel.acknowledgeAction()
            }

        }
    }

    LaunchedEffect(key1 = userState.value) {
        when (userState.value) {
            is UserState.Error -> {
//                Toast.makeText(context, (userState as UserState.Error).message, Toast.LENGTH_SHORT).show()
            }
            UserState.LoggedOut -> {
                Toast.makeText(context,
                    context.getString(R.string.sign_out_successful), Toast.LENGTH_SHORT).show()
                navigator.navigate(SignInScreenDestination){
                    popUpTo(HomeScreenDestination){
                        inclusive = true
                    }
                }
            }
            UserState.AccountDeleted -> {
                Toast.makeText(context,
                    context.getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT).show()
                navigator.navigate(SignInScreenDestination){
                    popUpTo(HomeScreenDestination){
                        inclusive = true
                    }
                }
            }
            is UserState.Success -> {
                userInfo = (userState.value as UserState.Success).user.userId
                homeScreenViewModel.subscribeNotesList(userInfo?.id)
            }
            else -> {}
        }
    }



    // Composable-----------------------------------------------------------------------------------

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ListerTopBar(userData = userInfo) {
                when (it) {

                    DropDownItem.DeleteAccount -> {
                        deleteAccount()
                    }
                    DropDownItem.Logout -> {
                        signOutClick()
                    }
                    DropDownItem.Shortcut -> {
                        addPinnedFunction()
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
            }
        }
    ) { scaffoldPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp),
        ) {
            items(items = list, key = { ListItem -> ListItem.id!! }) { ListItem ->

//                ListItemComposable(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp)
//                        .animateItem(
//                            fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(durationMillis = 300)
//                        ),
//                    listItem = ListItem,
//                    onDelete = {
//                        homeScreenViewModel.deleteNote(ListItem.id!!, ListItem)
//                    },
//                    onCheckClicked = {
//                        homeScreenViewModel.updateNote(it.id!!, !it.isDone)
//                    }
//                )

                SwipeableListItemComposable(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null,
                            placementSpec = tween(durationMillis = 300)
                        ),
                    listItem = ListItem,
                    onDelete = {
                        homeScreenViewModel.deleteNote(ListItem.id!!, ListItem)
                    },
                    onCheckClicked = {
                        homeScreenViewModel.updateNote(it.id!!, !it.isDone)
                    }
                )

            }
        }


        if (openDialog) {
            var note by remember {
                mutableStateOf("")
            }
            var isError by remember {
                mutableStateOf(false)
            }
            AlertDialog(
                onDismissRequest = { openDialog = false },
                confirmButton = {
                    Button(onClick = {
                        if (note.isEmpty()) {
                            isError = true
                        } else {
                            homeScreenViewModel.addNote(
                                title = note,
                                userId = userInfo?.id ?: ""
                            )
                            openDialog = false
                        }
                    }) {
                        Text(text = stringResource(R.string.add_note))
                    }
                },
                title = {
                    Text(text = stringResource(R.string.add_a_new_note))
                },
                text = {
                    TextField(
                        value = note,
                        placeholder = {
                            Text(text = stringResource(R.string.write_a_new_note))
                        },
                        onValueChange = {
                            note = it
                            isError = false
                        },
                        isError = isError,
                        maxLines = 2,
                    )
                }
            )
        }

    }
}

