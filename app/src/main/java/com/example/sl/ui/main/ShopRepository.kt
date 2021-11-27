package com.example.sl.ui.main

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ShopRepository {

    suspend fun getShopList(alreadyFetchedElements: Long): State<List<Shop>>
    suspend fun getItems(shopId: String): State<List<ItemElement>>
    suspend fun addItem(shopId: String, itemName: String, time: String): State<String>
    suspend fun removeItem(shopId: String, ie: ItemElement): State<Unit>
}

data class State<T>(
    val value: T?,
    val error: String?,
    val loading: Boolean?
) {
    fun isSuccess(): Boolean {
        return value != null
    }

    fun isError(): Boolean {
        return error != null
    }

    fun isLoading(): Boolean {
        return loading == true
    }

    companion object {
        fun <T> success(value: T): State<T> {
            return State(value = value, error = null, loading = null)
        }

        fun <T> error(message: String): State<T> {
            return State(value = null, error = message, loading = null)
        }

        fun <T> loading(): State<T> {
            return State(value = null, error = null, loading = true)
        }
    }
}

class FirebaseShopRepository(private val collection: String) : ShopRepository {
    val db = Firebase.firestore
    var lastFetched: DocumentSnapshot? = null


    override suspend fun getShopList(alreadyFetchedElements: Long): State<List<Shop>> {
        return suspendCoroutine { cont ->
            val query =
                if (lastFetched == null) {
                    db.collection(collection)
                        .orderBy("creationTime")
                        .limit(FETCH_AT_ONCE.toLong())
                } else {
                    val last: DocumentSnapshot = lastFetched!!
                    db.collection(collection)
                        .orderBy("creationTime")
                        .startAfter(last)
                        .limit(FETCH_AT_ONCE.toLong())
                }
            query.get().addOnSuccessListener {
                for (document in it) {
                    Log.d(
                        FirebaseShopRepository::class.java.name,
                        "${document.id} => ${document.data}"
                    )
                }
                if (it.documents.isNotEmpty()) {
                    lastFetched = it.documents.last()
                }
                val a = it.toObjects(Shop::class.java)
                cont.resume(State.success(a))
            }.addOnFailureListener {
                Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
                cont.resume(State.error("Please try again later"))
            }
        }
    }

    override suspend fun getItems(shopId: String): State<List<ItemElement>> {
        return suspendCoroutine { cont ->
            val query =
                db.collection(collection).document(shopId).collection("items")

            query.get().addOnSuccessListener {
                for (document in it) {
                    Log.d(
                        FirebaseShopRepository::class.java.name,
                        "${document.id} => ${document.data}"
                    )
                }
                val a = it.toObjects(ItemElement::class.java)
                cont.resume(State.success(a))
            }.addOnFailureListener {
                Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
                cont.resume(State.error("Please try again later"))
            }
        }
    }

    override suspend fun addItem(shopId: String, itemName: String, time: String): State<String> {
        return suspendCoroutine { cont ->
            db.collection(collection)
                .document(shopId)
                .collection("items")
                .add(
                    mapOf(
                        "name" to itemName,
                        "creationTime" to time
                    )
                ).addOnSuccessListener {
                    Log.d(
                        FirebaseShopRepository::class.java.name,
                        "Successfully added item with ${it.id} to shop ${shopId}"
                    )
                    cont.resume(State.success(it.id))
                }
        }
    }

    override suspend fun removeItem(shopId: String, ie: ItemElement): State<Unit> {
        return suspendCoroutine { cont ->
            db.collection(collection)
                .document(shopId)
                .collection("items")
                .document(ie.id!!)
                .delete().addOnSuccessListener {
                    cont.resume(State.success(Unit))
                }
        }
    }

    companion object {
        private const val FETCH_AT_ONCE = 20
    }

}

object ShopRepositoryMock : ShopRepository {
    private val list = mutableListOf<Shop>(

    )
    private val listItems = mutableListOf(
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "asdasd", "2020"),
        ItemElement("sad", "___________", "2020"),
    )


    override suspend fun getShopList(alreadyFetchedElements: Long): State<List<Shop>> {

        return State.success(
            listOf(
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "shopname", "Asd"),
                Shop("asd", "________________", "Asd")
            )
        )
    }

    override suspend fun getItems(shopId: String): State<List<ItemElement>> {
        return State.success(listItems)
    }

    override suspend fun addItem(shopId: String, itemName: String, time: String): State<String> {
        listItems.add(ItemElement("saed", itemName, time))
        return State.success("fdkls")
    }

    override suspend fun removeItem(shopId: String, ie: ItemElement): State<Unit> {
        listItems.remove(ie)
        return State.success(Unit)
    }

}