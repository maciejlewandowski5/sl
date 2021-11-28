package com.example.sl.ui.main

import androidx.lifecycle.*
import com.example.sl.model.ItemElement
import com.example.sl.model.State
import com.example.sl.repository.FirebaseShopRepository
import com.example.sl.repository.ShopRepository
import kotlinx.coroutines.launch

typealias MutableLiveDataItems = MutableLiveData<State<List<ItemElement>>>
typealias LiveDataItems = LiveData<State<List<ItemElement>>>

class ItemsViewModel(private val dataSource: ShopRepository) : ViewModel() {

    private val _items = MutableLiveDataItems(State.loading())
    val items: LiveDataItems = _items

    private val _shopId: MutableLiveData<String> = MutableLiveData(null)
    val shopId: LiveData<String> = _shopId

    private val _page: MutableLiveData<Int> = MutableLiveData(null)
    val page: LiveData<Int> = _page

    private val _errorMessage: MutableLiveData<String?> = MutableLiveData(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _actionsInBackground: MutableLiveData<Boolean?> = MutableLiveData(null)
    val actionsInBackground: LiveData<Boolean?> = _actionsInBackground

    fun fetchItems(shopId: String) {
        viewModelScope.launch {
            _items.value = dataSource.getItems(shopId)
        }
    }

    fun addItem(shopId: String, itemName: String, s: String) {
        viewModelScope.launch {
            if (_items.value?.isSuccess() == true) {
                _actionsInBackground.value = true
                val state = dataSource.addItem(shopId, itemName, s)
                when {
                    state.isSuccess() -> {
                        addItemLocal(state, itemName, s)
                    }
                    state.isLoading() -> {
                        _actionsInBackground.value = true
                    }
                    state.isError() -> {
                        _errorMessage.value = state.error!!
                    }
                }
            }
        }
    }

    private fun addItemLocal(
        state: State<String>,
        itemName: String,
        s: String
    ) {
        _items.value?.value?.toMutableList()?.let {
            it.add(ItemElement(state.value, itemName, s))
            _items.value = State.success(it.toList())
            _actionsInBackground.value = false
        }
    }

    fun setShopId(shopId: String) {
        _shopId.value = shopId
    }

    fun setPage(page: Int) {
        _page.value = page
    }

    fun removeItem(shopId: String, item: ItemElement) {
        viewModelScope.launch {
            if (_items.value?.isSuccess() == true) {
                _actionsInBackground.value = true
                val state = dataSource.removeItem(shopId, item)
                when {
                    state.isSuccess() -> {
                        removeItemLocal(item)
                    }
                    state.isLoading() -> {
                        _actionsInBackground.value = true
                    }
                    state.isError() -> {
                        _errorMessage.value = state.error!!
                    }
                }
            }
        }
    }

    private fun removeItemLocal(ie: ItemElement) {
        _items.value?.value?.toMutableList()?.let {
            it.remove(ie)
            _items.value = State.success(it.toList())
            _actionsInBackground.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

}

class ItemsListViewModelFactory(private val collection: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsViewModel(
                dataSource = FirebaseShopRepository(collection = collection)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}