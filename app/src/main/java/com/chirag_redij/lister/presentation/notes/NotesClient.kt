package com.chirag_redij.lister.presentation.notes

import com.chirag_redij.lister.di.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesClient @Inject constructor(
    private val notesRepo: NotesRepo
) {

    private val _notesList = MutableStateFlow(listOf<Note>())
    val notesList = _notesList.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun addNote(
        title : String,
        userId : String,
        description : String? = null
    ) {
        notesRepo.addNote(title, userId, description)
    }

    fun getNotesList(userId: String?) {

        scope.launch {
            if (userId != null) {

                notesRepo.getNotesList(userId).collect{
                    Timber.tag("NotesList").d(it.toString())
                    _notesList.emit(it)
                }

            }
        }
    }

    fun deleteNote(
        noteId: String
    ) {
        notesRepo.delNotes(noteId)
    }

    fun updateNote (
        noteId: String,
        status: Boolean
    ) {
        notesRepo.changeNoteStatus(noteId,status)
    }

}