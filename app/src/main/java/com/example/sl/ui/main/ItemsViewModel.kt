package com.example.sl.ui.main

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ItemsViewModel(private val dataSource: ShopRepository) : ViewModel() {

    private val _items = MutableLiveData<State<List<ItemElement>>>(State.loading())
    val flowersLiveData: LiveData<State<List<ItemElement>>> = _items

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
                val id: State<String> = dataSource.addItem(shopId, itemName, s)
                when {
                    id.isSuccess() -> {
                        _items.value?.value?.toMutableList()?.let {
                            it.add(ItemElement(id.value, itemName, s))
                            _items.value = State.success(it.toList())
                            _actionsInBackground.value = false
                        }
                    }
                    id.isLoading() -> {
                        _actionsInBackground.value = true
                    }
                    id.isError() -> {
                        _errorMessage.value = id.error!!
                    }
                }

            }
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
            if (_items.value?.isSuccess() == true) {
                val state = dataSource.removeItem(shopId, ie)
                _actionsInBackground.value = true
                when {
                    state.isSuccess() -> {
                        _items.value?.value?.toMutableList()?.let {
                            it.remove(ie)
                            _items.value = State.success(it.toList())
                            _actionsInBackground.value = false
                        }
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

    fun clearError() {
        _errorMessage.value = null
    }

}

class ItemsListViewModelFactory(private val context: Context, private val collection: String) :
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