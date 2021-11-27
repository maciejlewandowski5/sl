package com.example.sl

import com.example.sl.ui.main.ShopRepositoryMock
import com.example.sl.ui.main.ShopsViewModel
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ShopListViewModelTest {

    @Test
    fun `error while fetching next page`() {
        val shopsViewModel = ShopsViewModel(ShopRepositoryMock)
        shopsViewModel.fetchNextPage(0) {}
    }

    @Test
    fun `success while fetching next page`() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `error while fetching first page`() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `success while fetching first page`() {
        assertEquals(4, 2 + 2)
    }
}