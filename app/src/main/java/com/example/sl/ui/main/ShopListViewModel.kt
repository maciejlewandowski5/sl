package com.example.sl.ui.main

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ShopsViewModel(val dataSource: ShopRepository) : ViewModel() {

    private val _shops = MutableLiveData<List<Shop>>(emptyList())
    val flowersLiveData: LiveData<List<Shop>> = _shops

    fun insertFlower(flowerName: String?, flowerDescription: String?) {
        if (flowerName == null || flowerDescription == null) {
            return
        }

        val newFlower = Shop(
            "",
            "ShopName",
           ""
        )

        dataSource.addShop(newFlower)
    }

    fun fetchNextPage(alreadyFetchedElements: Long, callback: () -> Unit) {
        viewModelScope.launch {
            val addAll = _shops.value?.toMutableList()
            addAll?.addAll(dataSource.getShopList(alreadyFetchedElements))
            addAll?.let {
                _shops.value = it
                callback()
            }
        }
    }
}

class FlowersListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopsViewModel(
                dataSource = FirebaseShopRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
