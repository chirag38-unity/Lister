package com.chirag_redij.lister.presentation.notes

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Note(
    val id : String? = null,
    @SerialName("created_at")
    val createdAt : String? = null,
    var title : String? = null,
    var description : String? = null,
    @SerialName("is_done")
    var isDone : Boolean = false,
    @SerialName("user_id")
    val userId : String? = null
)
