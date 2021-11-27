package com.example.sl.ui.main

import com.google.firebase.firestore.DocumentId

data class Shop(
    @DocumentId
    var id: String?,
    var name: String?,
    var creationTime: String?
) {
    constructor() : this(id = null, name = null, creationTime = null)
}

data class ItemElement(
    @DocumentId
    var id: String?,
    var name: String?,
    var creationTime: String?
) {
    constructor() : this(id = null, name = null, creationTime = null)
}