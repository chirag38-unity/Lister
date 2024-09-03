package com.chirag_redij.lister.presentation.notes

import com.chirag_redij.lister.di.SupabaseClient.client
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannelBuilder
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.postgresListDataFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


class NotesRepo @Inject constructor(

) {

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(SupabaseInternal::class)
    val supabaseChannel = client.realtime.channel("channelId") {
        this.broadcast {
            this.receiveOwnBroadcasts = true
            this.acknowledgeBroadcasts = true
        }
        build()
    }

    init {
        scope.launch {
            client.realtime.connect()
        }
    }


    @OptIn(SupabaseExperimental::class)
    suspend fun getNotesList(userId: String) : Flow<List<Note>> = callbackFlow {
        val result = supabaseChannel.postgresListDataFlow(
            schema = "public", table = "notes", primaryKey = Note::id,
            filter = FilterOperation("user_id", FilterOperator.EQ, userId)
        )

        try{

            Timber.tag("initList").d("Channel Subscribed")
            supabaseChannel.subscribe()

            result.buffer()
                .map { notesList ->
                    notesList.sortedWith(compareBy<Note> { it.isDone }.thenByDescending { it.createdAt })
                }
                .collect{ sortedList ->
                    Timber.tag("initList").d(sortedList.toString())
                    trySend(sortedList)
                }

        } catch (e : Exception) {
            Timber.d(e)
        } finally {

            awaitClose {
                scope.launch{
                    supabaseChannel.unsubscribe()
                    Timber.tag("initList").d("Channel UnSubscribed")
                }
            }

        }

    }

    fun unsubscribeNotesList() {
        scope.launch{
            supabaseChannel.unsubscribe()
            Timber.tag("initList").d("Channel UnSubscribed")
        }
    }

    fun addNote(
        title : String,
        userId : String,
        description : String? = null
    ) {
        scope.launch {
            try {
                client.postgrest["notes"].upsert(
                    Note(
                        title = title,
                        description = description,
                        userId = userId
                    )
                )
            } catch (e : Exception) {
                Timber.d(e)
            }
        }
    }

    fun delNotes (
        noteId: String
    ) {
        scope.launch {
            try {
                client.postgrest["notes"].delete {
                    filter {
                        Note::id eq noteId
                    }
                }
            } catch (e : Exception) {
                Timber.d(e)
            }
        }
    }

    fun changeNoteStatus (
        noteId: String,
        status: Boolean
    ) {
        scope.launch {
            try {
               client.postgrest["notes"].update(
                   {
                        Note::isDone setTo status
                    }
               ) {
                   filter {
                       Note::id eq noteId
                   }
               }
            } catch (e : Exception) {

            }
        }
    }



}