package com.example.sl.ui.main

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ItemsViewModel(private val dataSource: ShopRepository) : ViewModel() {

    private val _items = MutableLiveData<List<ItemElement>>(emptyList())
    val flowersLiveData: LiveData<List<ItemElement>> = _items
    private val _shopId: MutableLiveData<String> = MutableLiveData(null)
    val shopId: LiveData<String> = _shopId

    private val _page: MutableLiveData<Int> = MutableLiveData(null)
    val page: LiveData<Int> = _page

    fun fetchItems(shopId: String) {
        viewModelScope.launch {
            _items.value = dataSource.getItems(shopId)
        }
    }

    fun addItem(shopId: String, itemName: String, s: String) {
        viewModelScope.launch {
            val id = dataSource.addItem(shopId, itemName, s)
            val tmp = _items.value?.toMutableList()
            tmp?.add(ItemElement(id, itemName, s))
            _items.value = tmp?.toList()
        }
    }

    fun setShopId(shopId: String) {
        _shopId.value = shopId
    }

    fun setPage(page: Int) {
        _page.value = page
    }

    fun removeItem(shopId: String, ie: ItemElement) {
        viewModelScope.launch {
            dataSource.removeItem(shopId, ie)
            val tmp = _items.value?.toMutableList()
            tmp?.remove(ie)
            _items.value = tmp?.toList()
        }
    }

}

class ItemsListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsViewModel(
                dataSource = FirebaseShopRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}