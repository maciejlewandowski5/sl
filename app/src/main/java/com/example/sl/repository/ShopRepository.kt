package com.example.sl.repository

import android.util.Log
import com.example.sl.model.Shop
import com.example.sl.model.ItemElement
import com.example.sl.model.State
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface ShopRepository {
    suspend fun getShopList(alreadyFetchedElements: Long): State<List<Shop>>
    suspend fun getItems(shopId: String): State<List<ItemElement>>
    suspend fun addItem(shopId: String, itemName: String, time: String): State<String>
    suspend fun removeItem(shopId: String, ie: ItemElement): State<Unit>
}

class FirebaseShopRepository(private val collection: String) : ShopRepository {
    private val db = Firebase.firestore
    var lastFetched: DocumentSnapshot? = null


    override suspend fun getShopList(alreadyFetchedElements: Long): State<List<Shop>> {
        return suspendCoroutine { cont ->
            resolveLastFetched().get().addOnSuccessListener {
                handleOnSuccess(it, cont)
            }.addOnFailureListener {
                handleOnError(it, cont)
            }
        }
    }

    private fun handleOnError(
        it: Exception,
        cont: Continuation<State<List<Shop>>>
    ) {
        Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
        cont.resume(State.error("Please try again later"))
    }

    private fun handleOnSuccess(
        it: QuerySnapshot,
        cont: Continuation<State<List<Shop>>>
    ) {
        logSuccess(it)
        if (it.documents.isNotEmpty()) {
            lastFetched = it.documents.last()
        }
        cont.resume(State.success(it.toObjects(Shop::class.java)))
    }

    private fun logSuccess(it: QuerySnapshot) {
        for (document in it) {
            Log.d(
                FirebaseShopRepository::class.java.name,
                "${document.id} => ${document.data}"
            )
        }
    }

    private fun resolveLastFetched() = if (lastFetched == null) {
        db.collection(collection)
            .orderBy(CREATION_TIME)
            .limit(FETCH_AT_ONCE.toLong())
    } else {
        val last: DocumentSnapshot = lastFetched!!
        db.collection(collection)
            .orderBy(CREATION_TIME)
            .startAfter(last)
            .limit(FETCH_AT_ONCE.toLong())
    }

    override suspend fun getItems(shopId: String): State<List<ItemElement>> {
        return suspendCoroutine { cont ->
            val query =
                db.collection(collection).document(shopId).collection(ITEMS)
            query.get().addOnSuccessListener {
                logSuccess(it)
                cont.resume(State.success(it.toObjects(ItemElement::class.java)))
            }.addOnFailureListener {
                Log.w(FirebaseShopRepository::class.java.name, "Error getting documents.", it)
                cont.resume(State.error(ERROR_MESSAGE))
            }
        }
    }

    override suspend fun addItem(shopId: String, itemName: String, time: String): State<String> {
        return suspendCoroutine { cont ->
            db.collection(collection)
                .document(shopId)
                .collection(ITEMS)
                .add(ItemElement.itemAsMap(itemName, time))
                .addOnSuccessListener {
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
                .collection(ITEMS)
                .document(ie.id!!)
                .delete().addOnSuccessListener {
                    cont.resume(State.success(Unit))
                }
        }
    }

    companion object {
        private const val FETCH_AT_ONCE = 20
        private const val CREATION_TIME = "creationTime"
        private const val ITEMS = "items"
        private const val ERROR_MESSAGE = "Please try again later"
    }
}
