package com.example.sl

object Keys {
    const val SHOP_ID = "shop_id"
    const val IS_ARCHIVED = "is_archived"
    const val COLLECTION_SHOPS = "shops"
    const val COLLECTION_ARCHIVE = "archive"
    const val DEFAULT_ARCHIVE_PAGE = 2
}

fun resolveCollection(page: Int) = if (page != Keys.DEFAULT_ARCHIVE_PAGE) {
    Keys.COLLECTION_SHOPS
} else {
    Keys.COLLECTION_ARCHIVE
}