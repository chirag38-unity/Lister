package com.chirag_redij.lister.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirag_redij.lister.MainActivity
import com.chirag_redij.lister.R
import com.chirag_redij.lister.presentation.notes.Note
import com.chirag_redij.lister.presentation.notes.NotesClient
import com.chirag_redij.lister.presentation.sign_in.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val notesClient: NotesClient,
    private val signInProvider: SignInProvider
) : ViewModel() {

    val userState = signInProvider.state
    val actionState = signInProvider.actionState

    private val _notesList = mutableStateListOf<Note>()
    val notesList : List<Note>
        get() = _notesList

    init {

        viewModelScope.launch {
            notesClient.notesList.collectLatest { list ->
                _notesList.clear().also {
                    _notesList.addAll(list)
                }
            }
        }

    }

    fun logout() {
        notesClient.unsubscribeNotesList()
        signInProvider.logout()
    }

    fun deleteAccount() {
        notesClient.unsubscribeNotesList()
        signInProvider.deleteAccount()
    }

    fun subscribeNotesList(userId: String?) {
        notesClient.getNotesList(userId)
    }

    fun acknowledgeAction() {
        Timber.tag("Intent").d("Action acknowledged")
        signInProvider.parseViewAction(null)
    }

    fun addNote(
        title : String,
        userId : String,
        description : String? = null
    ) {
        notesClient.addNote(title, userId, description)
    }

    fun deleteNote (
        noteId : String,
        note: Note
    ) {
        Timber.tag("Note").d("Deleting note $noteId")
        viewModelScope.launch {
            _notesList.remove(note)
        }
        notesClient.deleteNote(noteId)
    }

    fun updateNote (
        noteId: String,
        status: Boolean
    ) {
        Timber.tag("Note").d("Updating note $noteId")
        notesClient.updateNote(noteId,status)
    }

    fun addPinnedShortcut(context: Context) : ShortcutInfo?{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null
        }

        val shortcutManager = getSystemService<ShortcutManager>(context, ShortcutManager::class.java)!!

        if (shortcutManager.isRequestPinShortcutSupported) {
            val shortcutInfo = ShortcutInfo.Builder(context, "pinned_shortcut")
                .setShortLabel("Add Note")
                .setIcon(Icon.createWithResource(context, R.drawable.write_shortcut))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                    }
                )
                .build()

            return shortcutInfo

        }

        return null

    }

}