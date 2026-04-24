package com.escala.ministerial.feature.escalas

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.escalas.domain.model.Escala
import com.escala.ministerial.feature.escalas.domain.model.StatusEscala
import com.escala.ministerial.feature.escalas.domain.repository.EscalaRepository
import com.escala.ministerial.feature.escalas.domain.usecase.AprovarEscalaUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AprovarEscalaUseCaseTest {

    private lateinit var repository: EscalaRepository
    private lateinit var useCase: AprovarEscalaUseCase

    private val escalaAprovada = Escala(
        id = 1L,
        eventoId = 10L,
        eventoNome = "Missa Dominical",
        eventoData = null,
        eventoHorario = null,
        dataAtribuicao = null,
        observacao = null,
        status = StatusEscala.APROVADA,
        ministros = emptyList(),
    )

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = AprovarEscalaUseCase(repository)
    }

    @Test
    fun `returns Success when repository approves`() = runTest {
        coEvery { repository.aprovar(1L) } returns ApiResult.Success(escalaAprovada)

        val result = useCase(1L)

        assertTrue(result is ApiResult.Success)
        val success = result as ApiResult.Success
        assertTrue(success.data.status == StatusEscala.APROVADA)
        coVerify(exactly = 1) { repository.aprovar(1L) }
    }

    @Test
    fun `propagates error when repository fails`() = runTest {
        coEvery { repository.aprovar(99L) } returns ApiResult.Error("Escala não encontrada", 404)

        val result = useCase(99L)

        assertTrue(result is ApiResult.Error)
        val error = result as ApiResult.Error
        assertTrue(error.code == 404)
    }
}
