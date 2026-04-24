package com.escala.ministerial.feature.feedback

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.feedback.domain.model.Feedback
import com.escala.ministerial.feature.feedback.domain.model.StatusFeedback
import com.escala.ministerial.feature.feedback.domain.repository.FeedbackRepository
import com.escala.ministerial.feature.feedback.domain.usecase.ResponderFeedbackUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ResponderFeedbackUseCaseTest {

    private lateinit var repository: FeedbackRepository
    private lateinit var useCase: ResponderFeedbackUseCase

    private val feedbackRespondido = Feedback(
        id = 1L,
        ministroId = 2L,
        ministroNome = "João",
        eventoId = 3L,
        eventoNome = "Missa",
        nota = 8,
        comentario = "Ótimo evento",
        dataEnvio = LocalDateTime.now(),
        status = StatusFeedback.RESPONDIDO,
        resposta = "Obrigado pelo feedback!",
    )

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = ResponderFeedbackUseCase(repository)
    }

    @Test
    fun `invokes repository with correct parameters`() = runTest {
        coEvery { repository.responder(1L, "Obrigado!") } returns ApiResult.Success(feedbackRespondido)

        val result = useCase(1L, "Obrigado!")

        assertTrue(result is ApiResult.Success)
        val success = result as ApiResult.Success
        assertEquals(StatusFeedback.RESPONDIDO, success.data.status)
        coVerify(exactly = 1) { repository.responder(1L, "Obrigado!") }
    }

    @Test
    fun `returns error on failure`() = runTest {
        coEvery { repository.responder(any(), any()) } returns ApiResult.Error("Servidor indisponível")

        val result = useCase(1L, "Resposta")

        assertTrue(result is ApiResult.Error)
    }
}
