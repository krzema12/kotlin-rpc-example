package it.krzeminski.todoapp.api

import kotlinx.serialization.Serializable

interface TodoAppApi {
    suspend fun listTodos(): List<Todo>
    suspend fun addToList(description: String): List<Todo>
}

@Serializable
data class Todo(
    val description: String,
    val isDone: Boolean,
    val assignee: String? = null,
)
