package com.escala.ministerial.feature.ministros

import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import com.escala.ministerial.feature.ministros.domain.usecase.SaveMinistroUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SaveMinistroUseCaseTest {

    private lateinit var repository: MinistroRepository
    private lateinit var useCase: SaveMinistroUseCase

    private val ministroNovo = Ministro(
        id = 0L,
        nome = "Novo Ministro",
        email = "novo@test.com",
        telefone = null,
        dataNascimento = null,
        observacoes = null,
        ativo = true,
        visitasAoInfermo = false,
        statusCurso = false,
        escalasMes = 0,
        funcao = FuncaoMinistro.LEITURA,
    )

    private val ministroExistente = ministroNovo.copy(id = 5L)

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = SaveMinistroUseCase(repository)
    }

    @Test
    fun `calls create when ministro id is 0`() = runTest {
        coEvery { repository.create(ministroNovo) } returns ApiResult.Success(ministroNovo.copy(id = 10L))

        val result = useCase(ministroNovo)

        assertTrue(result is ApiResult.Success)
        coVerify(exactly = 1) { repository.create(ministroNovo) }
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `calls update when ministro id is non-zero`() = runTest {
        coEvery { repository.update(5L, ministroExistente) } returns ApiResult.Success(ministroExistente)

        val result = useCase(ministroExistente)

        assertTrue(result is ApiResult.Success)
        coVerify(exactly = 1) { repository.update(5L, ministroExistente) }
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `propagates error from repository`() = runTest {
        coEvery { repository.create(ministroNovo) } returns ApiResult.Error("Erro de rede")

        val result = useCase(ministroNovo)

        assertTrue(result is ApiResult.Error)
        require(result is ApiResult.Error)
        assertTrue(result.message.contains("Erro"))
    }
}
