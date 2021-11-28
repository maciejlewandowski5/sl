package com.example.sl.model


data class State<T>(
    val value: T?,
    val error: String?,
    val loading: Boolean?
) {
    fun isSuccess(): Boolean {
        return value != null
    }

    fun isError(): Boolean {
        return error != null
    }

    fun isLoading(): Boolean {
        return loading == true
    }

    companion object {
        fun <T> success(value: T): State<T> {
            return State(value = value, error = null, loading = null)
        }

        fun <T> error(message: String): State<T> {
            return State(value = null, error = message, loading = null)
        }

        fun <T> loading(): State<T> {
            return State(value = null, error = null, loading = true)
        }
    }
}