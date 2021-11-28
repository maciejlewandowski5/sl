package com.example.sl.model

import com.google.firebase.firestore.DocumentId

data class ItemElement(
    @DocumentId
    var id: String?,
    var name: String?,
    var creationTime: String?
) {
    constructor() : this(id = null, name = null, creationTime = null)

    companion object {
        fun itemAsMap(itemName: String, time: String): Map<String, String> {
            return mapOf(
                "name" to itemName,
                "creationTime" to time
            )
        }
    }
}
