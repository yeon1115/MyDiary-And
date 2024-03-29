package com.google.firebase.quickstart.database.kotlin.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

@IgnoreExtraProperties
data class Post(
    var uid: String? = "",
    var author: String? = "",
    var title: String? = "",
    var body: String? = "",
    var photoUrl: String? = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {

    var id: String? = ""

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "id" to id,
                "uid" to uid,
                "author" to author,
                "title" to title,
                "body" to body,
                "photoUrl" to photoUrl,
                "starCount" to starCount,
                "stars" to stars
        )
    }
}
