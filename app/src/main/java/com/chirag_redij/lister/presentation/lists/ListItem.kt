package com.chirag_redij.lister.presentation.lists

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import java.time.Instant

@Keep
data class ListItem(

    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String? = null,
    val timeInMillis: Long = Instant.now().toEpochMilli(),
    var title: String = "Error loading",
    var description: String? = null,
    @get:PropertyName("isDone")
    @set:PropertyName("isDone")
    var isDone: Boolean = false
)
