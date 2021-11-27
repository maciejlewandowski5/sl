package com.example.sl.ui.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ShopRepository {

    suspend fun getShopList(alreadyFetchedElements: Long): List<Shop>

    fun addShop(newFlower: Shop)

    suspend fun getItems(shopId: String): List<ItemElement>
    suspend fun addItem(shopId: String, itemName: String, time: String): String
    suspend fun removeItem(shopId: String, ie: ItemElement)
}

data class State<T>(
    val value: T?,
    val error: String?,
    val isLoading: Boolean?
) {
    fun isSuccess(): Boolean {
        return value != null
    }

    fun isError(): Boolean {
        return error != null
    }

    fun isLoading(): Boolean {
        return isLoading == true
    }
}

class FirebaseShopRepository : ShopRepository {
    val db = Firebase.firestore
    var lastFetched: DocumentSnapshot? = null

    override suspend fun getShopList(alreadyFetchedElements: Long): List<Shop> {
        return suspendCoroutine { cont ->
            val query =
                if (lastFetched == null) {
                    db.collection("shops")
                        .orderBy("creationTime")
                        .limit(FETCH_AT_ONCE.toLong())
                } else {
                    val last: DocumentSnapshot = lastFetched!!
                    db.collection("shops")
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
                cont.resume(a)
            }.addOnFailureListener {
                Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
            }
        }
    }

    override fun addShop(newFlower: Shop) {
        TODO("Not yet implemented")
    }

    override suspend fun getItems(shopId: String): List<ItemElement> {
        return suspendCoroutine { cont ->
            val query =
                db.collection("shops").document(shopId).collection("items")

            query.get().addOnSuccessListener {
                for (document in it) {
                    Log.d(
                        FirebaseShopRepository::class.java.name,
                        "${document.id} => ${document.data}"
                    )
                }
                val a = it.toObjects(ItemElement::class.java)
                cont.resume(a)
            }.addOnFailureListener {
                Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
            }
        }
    }

    override suspend fun addItem(shopId: String, itemName: String, time: String): String {
        return suspendCoroutine { cont ->
            db.collection("shops")
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
                    cont.resume(it.id)
                }
        }
    }

    override suspend fun removeItem(shopId: String, ie: ItemElement) {
        return suspendCoroutine { cont ->
            db.collection("shops")
                .document(shopId)
                .collection("items")
                .document(ie.id!!)
                .delete().addOnSuccessListener {
                    cont.resume(Unit)
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


    override suspend fun getShopList(alreadyFetchedElements: Long): List<Shop> {

        return listOf(
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
    }

    override fun addShop(newFlower: Shop) {
        list.add(newFlower)
    }

    override suspend fun getItems(shopId: String): List<ItemElement> {
        return listItems
    }

    override suspend fun addItem(shopId: String, itemName: String, time: String): String {
        listItems.add(ItemElement("saed", itemName, time))
        return "fdkls"
    }

    override suspend fun removeItem(shopId: String, ie: ItemElement) {
        listItems.remove(ie)
    }

}