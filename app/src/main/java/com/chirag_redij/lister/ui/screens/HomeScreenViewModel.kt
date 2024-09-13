package com.chirag_redij.lister.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.chirag_redij.lister.MainActivity
import com.chirag_redij.lister.R
import com.chirag_redij.lister.presentation.notes.NotesClient
import com.chirag_redij.lister.presentation.sign_in.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val notesClient: NotesClient,
    private val signInProvider: SignInProvider
) : ViewModel() {

    val userState = signInProvider.state
    val actionState = signInProvider.actionState
    val notesList = notesClient.notesList

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
        noteId : String
    ) {
        Timber.tag("Note").d("Deleting note $noteId")
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