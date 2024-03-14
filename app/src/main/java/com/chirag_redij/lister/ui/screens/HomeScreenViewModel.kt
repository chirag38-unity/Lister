package com.chirag_redij.lister.ui.screens

import androidx.lifecycle.ViewModel
import com.chirag_redij.lister.presentation.notes.NotesClient
import com.chirag_redij.lister.presentation.sign_in.SignInProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val notesClient: NotesClient,
    private val signInProvider: SignInProvider
) : ViewModel() {

    val userState = signInProvider.state
    val notesList = notesClient.notesList

    fun logout() {
        signInProvider.logout()
    }

    fun subscribeNotesList(userId: String?) {
        notesClient.getNotesList(userId)
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
        notesClient.deleteNote(noteId)
    }

    fun updateNote (
        noteId: String,
        status: Boolean
    ) {
        notesClient.updateNote(noteId,status)
    }

}