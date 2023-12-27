package com.chirag_redij.lister.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirag_redij.lister.presentation.lists.ListItem
import com.chirag_redij.lister.presentation.lists.ListViewModel
import com.chirag_redij.lister.presentation.sign_in.SignInViewModel
import com.chirag_redij.lister.ui.components.DropDownItem
import com.chirag_redij.lister.ui.components.ListItemComposable
import com.chirag_redij.lister.ui.components.ListerTopBar
import com.google.firebase.auth.FirebaseUser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    userData: FirebaseUser?,
    signInViewModel: SignInViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val list = listViewModel.listState.collectAsState()
    var openDialog by remember {
        mutableStateOf(false)
    }

    val signOutClick: () -> Unit = {
        coroutineScope.launch {
            signInViewModel.signOut()
            Toast.makeText(context, "Sign Out Successful", Toast.LENGTH_SHORT).show()
            navigator.popBackStack()
        }
    }

    // Composable-----------------------------------------------------------------------------------

    Scaffold(
        topBar = {
            ListerTopBar(userData = userData) {
                when (it) {
                    DropDownItem.Settings -> {
                        Toast.makeText(context, "Settings Button Clicked", Toast.LENGTH_SHORT)
                            .show()
                    }

                    DropDownItem.Logout -> {
                        signOutClick()
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openDialog = true },
                containerColor = Color.Yellow,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { scaffoldPadding ->


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp),
        ) {
            items(list.value) { ListItem ->
                ListItemComposable(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                listViewModel.deleteItem(ListItem)
                            },
                        ),
                    listItem = ListItem
                ) {
                    listViewModel.toggleItemState(it)
                }
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
                            listViewModel.addNewItem(
                                ListItem(
                                    title = note
                                )
                            )
                            openDialog = false
                        }
                    }) {
                        Text(text = "Add Note")
                    }
                },
                title = {
                    Text(text = "Add a new note")
                },
                text = {
                    TextField(
                        value = note,
                        placeholder = {
                            Text(text = "Write a new note")
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

