package com.escala.ministerial.feature.ministros

import app.cash.turbine.test
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import com.escala.ministerial.feature.ministros.domain.usecase.GetMinistrosUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMinistrosUseCaseTest {

    private lateinit var repository: MinistroRepository
    private lateinit var useCase: GetMinistrosUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetMinistrosUseCase(repository)
    }

    @Test
    fun `invoke emits list from repository`() = runTest {
        val ministros = listOf(
            Ministro(1L, "João Silva", "joao@test.com", null, null, null, true, false, false, 0, FuncaoMinistro.LEITURA),
            Ministro(2L, "Maria Santos", "maria@test.com", null, null, null, true, false, false, 1, FuncaoMinistro.EUCARISTIA),
        )
        every { repository.observeAll() } returns flowOf(ministros)

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("João Silva", result[0].nome)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits empty list when no ministros`() = runTest {
        every { repository.observeAll() } returns flowOf(emptyList())

        useCase().test {
            assertEquals(emptyList<Ministro>(), awaitItem())
            awaitComplete()
        }
    }
}
