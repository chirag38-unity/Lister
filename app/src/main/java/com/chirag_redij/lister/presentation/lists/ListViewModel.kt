package com.chirag_redij.lister.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirag_redij.lister.presentation.sign_in.GoogleAuthUIClient
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    val listRepo: ListRepo,
    val googleAuthUIClient: GoogleAuthUIClient
) : ViewModel() {

    private val _listState = MutableStateFlow(listOf<ListItem>())
    val listState: StateFlow<List<ListItem>> = _listState.asStateFlow()
    private val user: FirebaseUser = googleAuthUIClient.getSignedInUser()!!

    init {
        getList()
    }

    private fun getList() {
        viewModelScope.launch {
            listRepo.getList(user.uid).collect {
                Timber.tag("Lists").d(it.toString())
                _listState.value = it
            }
        }
    }

    fun deleteItem(listItem: ListItem) {
        viewModelScope.launch {
            listRepo.deleteItem(user.uid, listItem)
        }
    }

    fun addNewItem(listItem: ListItem) {
        viewModelScope.launch {
            listRepo.addItem(user.uid, listItem)
        }
    }

    fun toggleItemState(listItem: ListItem) {
        viewModelScope.launch {
            listRepo.toggleItemState(user.uid, listItem.id!!, !listItem.isDone)
        }
    }

}