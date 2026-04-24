package com.escala.ministerial.feature.feedback.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.feedback.domain.usecase.GetFeedbacksUseCase
import com.escala.ministerial.feature.feedback.domain.usecase.RefreshFeedbacksUseCase
import com.escala.ministerial.feature.feedback.domain.usecase.ResponderFeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val getFeedbacks: GetFeedbacksUseCase,
    private val refreshFeedbacks: RefreshFeedbacksUseCase,
    private val responderFeedback: ResponderFeedbackUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedbackUiState>(FeedbackUiState.Loading)
    val uiState: StateFlow<FeedbackUiState> = _uiState

    private val _events = Channel<FeedbackEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            getFeedbacks()
                .catch { e -> _uiState.value = FeedbackUiState.Error(e.message ?: "Erro") }
                .collect { feedbacks -> _uiState.value = FeedbackUiState.Success(feedbacks) }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            when (val result = refreshFeedbacks()) {
                is ApiResult.Error -> _events.send(FeedbackEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }

    fun responder(id: Long, resposta: String) {
        viewModelScope.launch {
            when (val result = responderFeedback(id, resposta)) {
                is ApiResult.Success -> _events.send(FeedbackEvent.Responded)
                is ApiResult.Error -> _events.send(FeedbackEvent.ShowMessage(result.message))
                else -> Unit
            }
        }
    }
}
