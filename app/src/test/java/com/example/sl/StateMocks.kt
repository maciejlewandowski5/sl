package com.example.sl

import com.example.sl.ui.main.Shop
import com.example.sl.ui.main.State


fun error(): State<List<Shop>> =
    State.error("Example error")

fun shop() = State.success(
    listOf(
        Shop(
            id = "1",
            name = "Example shop",
            creationTime = "2007-04-05T14:30Z"
        )
    )
)

fun shops() = State.success(
    listOf(
        Shop(
            id = "1",
            name = "Example shop",
            creationTime = "2007-04-05T14:30Z"
        ),
        Shop(
            id = "1",
            name = "Example shop",
            creationTime = "2007-04-05T14:30Z"
        )
    )
)

fun loading() = State.loading<List<Shop>>()
