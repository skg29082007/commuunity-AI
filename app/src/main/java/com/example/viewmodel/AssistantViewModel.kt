package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val role: String,
    val text: String,
    val isLoading: Boolean = false
)

class AssistantViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(query: String) {
        val userMessage = ChatMessage(
            id = java.util.UUID.randomUUID().toString(),
            role = "user",
            text = query
        )
        
        val assistantMessageId = java.util.UUID.randomUUID().toString()
        val loadingMessage = ChatMessage(
            id = assistantMessageId,
            role = "assistant",
            text = "",
            isLoading = true
        )

        _messages.value = _messages.value + listOf(userMessage, loadingMessage)

        viewModelScope.launch {
            try {
                // Build history for the context
                val history = _messages.value.filter { !it.isLoading && it.text.isNotEmpty() }.map { msg ->
                    Content(
                        role = if (msg.role == "user") "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }
                
                val systemInstruction = Content(
                    parts = listOf(Part(text = "You are an AI-powered Decision Intelligence Platform assistant. Your role is to help individuals, communities, and city stakeholders analyze data, generate insights, predict outcomes, and make better decisions. You answer questions about urban mobility, public safety, healthcare, education, environmental sustainability, energy efficiency, and citizen engagement. Keep your answers concise, clear, and professional."))
                )

                val request = GenerateContentRequest(
                    contents = history,
                    systemInstruction = systemInstruction
                )

                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I am unable to analyze the data at this time."

                // Update the loading message with the actual response
                _messages.value = _messages.value.map {
                    if (it.id == assistantMessageId) {
                        it.copy(text = responseText, isLoading = false)
                    } else {
                        it
                    }
                }
            } catch (e: Exception) {
                _messages.value = _messages.value.map {
                    if (it.id == assistantMessageId) {
                        it.copy(text = "Error communicating with AI engine: ${e.message}", isLoading = false)
                    } else {
                        it
                    }
                }
            }
        }
    }
}
