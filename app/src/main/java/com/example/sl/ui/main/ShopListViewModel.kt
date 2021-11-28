package com.example.sl.ui.main

import androidx.lifecycle.*
import com.example.sl.model.Shop
import com.example.sl.model.State
import com.example.sl.repository.FirebaseShopRepository
import com.example.sl.repository.ShopRepository
import kotlinx.coroutines.launch

typealias MutableLiveDataShops = MutableLiveData<State<List<Shop>>>
typealias LiveDataShops = LiveData<State<List<Shop>>>

class ShopsViewModel(val dataSource: ShopRepository) : ViewModel() {

    private val _shops = MutableLiveDataShops(State.loading())
    val shops: LiveDataShops = _shops

    var tab: Int = 0

    fun fetchNextPage(alreadyFetchedElements: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            handleResult(dataSource.getShopList(alreadyFetchedElements), onSuccess)
        }
    }

    private fun handleResult(
        fetchedState: State<List<Shop>>,
        onSuccess: () -> Unit
    ) {
        when {
            fetchedState.isSuccess() -> {
                attachResult(fetchedState)
                onSuccess()
            }
            fetchedState.isError() -> {
                _shops.value = fetchedState
            }
        }
    }

    private fun attachResult(
        fetchedState: State<List<Shop>>,
    ) {
        _shops.value?.value?.toMutableList()?.let { join(it, fetchedState) } ?: _shops.setValue(
            fetchedState
        )
    }

    private fun join(
        it: MutableList<Shop>,
        fetchedState: State<List<Shop>>
    ) {
        it.addAll(fetchedState.value!!)
        _shops.value = State.success(it)
    }

    fun fetchFirstPage() {
        if (_shops.value?.isLoading() == true) {
            viewModelScope.launch { _shops.value = dataSource.getShopList(0) }
        }
    }
}

class ShopsViewModelFactory(private val collection: String) :
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
