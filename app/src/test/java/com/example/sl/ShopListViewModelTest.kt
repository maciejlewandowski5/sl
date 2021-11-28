package com.example.sl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.sl.ui.main.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.amshove.kluent.*
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class ShopListViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner
    private lateinit var postViewModel: ShopsViewModel
    private val repositoryMock: ShopRepository = mockk()

    var stateValue: State<List<Shop>>? = null


    @Before
    fun setUp() {
        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()
        postViewModel = ShopsViewModel(repositoryMock)
        postViewModel.flowersLiveData.observe(lifeCycleTestOwner) {
            stateValue = it
        }
    }

    @After
    fun tearDown() {
        lifeCycleTestOwner.onDestroy()
    }


    @Test
    fun `error while fetching first page`() {
        coEvery { repositoryMock.getShopList(0) } returns error()

        stateValue `should equal` null
        runTest(coroutineTestRule.testDispatcher) {
            lifeCycleTestOwner.onResume()
            stateValue `should equal` State.loading()
            postViewModel.fetchFirstPage()
        }
        stateValue `should equal` error()
    }

    @Test
    fun `success while fetching first page`() {
        coEvery { repositoryMock.getShopList(0) } returns shop()

        stateValue `should equal` null
        runTest(coroutineTestRule.testDispatcher) {
            lifeCycleTestOwner.onResume()
            stateValue `should equal` State.loading()
            postViewModel.fetchFirstPage()
        }
        stateValue `should equal` shop()
    }

    @Test
    fun `error while fetching next page`() {
        coEvery { repositoryMock.getShopList(ALREADY_FETCHED) } returns error()

        stateValue `should equal` null
        runTest(coroutineTestRule.testDispatcher) {
            lifeCycleTestOwner.onResume()
            stateValue `should equal` State.loading()
            postViewModel.fetchNextPage(ALREADY_FETCHED) {}
        }
        stateValue `should equal` error()
    }

    @Test
    fun `success while fetching next page`() {
        coEvery { repositoryMock.getShopList(ALREADY_FETCHED) } returns shop()

        stateValue `should equal` null
        runTest(coroutineTestRule.testDispatcher) {
            lifeCycleTestOwner.onResume()
            stateValue `should equal` State.loading()
            postViewModel.fetchNextPage(ALREADY_FETCHED) {}
        }
        stateValue `should equal` shop()
    }

    @Test
    fun `success while fetching next page after first page`() {
        coEvery { repositoryMock.getShopList(0) } returns shop()
        coEvery { repositoryMock.getShopList(ALREADY_FETCHED) } returns shop()

        stateValue `should equal` null
        runTest(coroutineTestRule.testDispatcher) {
            lifeCycleTestOwner.onResume()
            stateValue `should equal` State.loading()
            postViewModel.fetchFirstPage()
            postViewModel.fetchNextPage(ALREADY_FETCHED) {}
        }
        stateValue `should equal` shops()
    }

    companion object {
        private const val ALREADY_FETCHED = 10L
    }
}
