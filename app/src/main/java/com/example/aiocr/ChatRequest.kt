package com.example.aiocr

data class ChatRequest(
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)
