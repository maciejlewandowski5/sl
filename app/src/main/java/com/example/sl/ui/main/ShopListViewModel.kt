package com.example.sl.ui.main

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ShopsViewModel(val dataSource: ShopRepository) : ViewModel() {

    private val _shops = MutableLiveData<State<List<Shop>>>(State.loading())
    val flowersLiveData: LiveData<State<List<Shop>>> = _shops

    fun fetchNextPage(alreadyFetchedElements: Long, callback: () -> Unit) {
        viewModelScope.launch {
            val fetchedState = dataSource.getShopList(alreadyFetchedElements)
            when {
                fetchedState.isSuccess() -> {
                    _shops.value?.value?.toMutableList()?.let {
                        it.addAll(fetchedState.value!!)
                        _shops.value = State.success(it)
                    } ?: _shops.setValue(fetchedState)
                    callback()
                }
                fetchedState.isError() -> {
                    _shops.value = fetchedState
                }
            }
        }
    }

    fun fetchFirstPage() {
        if (_shops.value?.isLoading() == true) {
            viewModelScope.launch {
                val fetchedState = dataSource.getShopList(0)
                when {
                    fetchedState.isSuccess() -> {
                        mutableListOf<Shop>().let {
                            it.addAll(fetchedState.value!!)
                            _shops.value = State.success(it)
                        }
                    }
                    fetchedState.isError() -> {
                        _shops.value = fetchedState
                    }
                }
            }
        }
    }
}

class FlowersListViewModelFactory(private val context: Context, val collection: String) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopsViewModel(
                dataSource = FirebaseShopRepository(collection = collection)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
