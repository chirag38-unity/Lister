package com.chirag_redij.lister.presentation.utility

import android.content.Context

fun shouldShowReview(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val lastReviewTime = sharedPref.getLong("last_review_time", 0)
    val currentTime = System.currentTimeMillis()

    // Show review dialog if more than 30 days have passed since the last time
    val daysPassed = (currentTime - lastReviewTime) / (1000 * 60 * 60 * 24)
    return daysPassed > 30 // Only show if more than 30 days have passed
}