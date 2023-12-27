package com.chirag_redij.lister.presentation.lists

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepo @Inject constructor(
    val firestore: FirebaseFirestore
) {
    suspend fun getList(userId: String): Flow<List<ListItem>> =
        firestore.collection("users")
            .document(userId)
            .collection("list")
            .orderBy("timeInMillis", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map {
                    var item = it.toObject(ListItem::class.java)
                    item?.id = it.id
                    item!!
                }

            }
            .catch { exception ->
                Timber.tag("exception").d(exception)
                emit(emptyList())
            }

    fun toggleItemState(userId: String, documentId: String, state: Boolean) {
        firestore.collection("users")
            .document(userId)
            .collection("list")
            .document(documentId)
            .update("isDone", state)
    }

    fun deleteItem(userId: String, listItem: ListItem) {
        firestore.collection("users")
            .document(userId)
            .collection("list")
            .document(listItem.id!!)
            .delete()
    }

    fun addItem(userId: String, listItem: ListItem) {
        firestore.collection("users")
            .document(userId)
            .collection("list")
            .add(listItem)
    }

    private fun checkIfUserEntryExists(userId: String): Boolean {
        var exists = false
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        exists = true
                        return@addOnCompleteListener
                    } else {
                        return@addOnCompleteListener
                    }
                } else {
                    return@addOnCompleteListener
                }

            }
        return exists
    }

    fun createEntryIfNotExist(userId: String) {
        if (checkIfUserEntryExists(userId)) {
            return
        } else {
            val listRef = firestore.collection("users")
                .document(userId).collection("list")

            listRef.add(
                ListItem(
                    timeInMillis = Instant.now().toEpochMilli(),
                    title = "Hello World",
                    description = "This is a dummy description",
                )
            )
        }
    }

}