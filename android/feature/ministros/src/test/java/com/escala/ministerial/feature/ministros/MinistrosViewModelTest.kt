package com.escala.ministerial.feature.ministros

import app.cash.turbine.test
import com.escala.ministerial.core.data.seed.LocalSeedDataSeeder
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import com.escala.ministerial.feature.ministros.domain.usecase.DeleteMinistroUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.GetMinistrosUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.RefreshMinistrosUseCase
import com.escala.ministerial.feature.ministros.domain.usecase.SaveMinistroUseCase
import com.escala.ministerial.feature.ministros.presentation.MinistroEvent
import com.escala.ministerial.feature.ministros.presentation.MinistrosUiState
import com.escala.ministerial.feature.ministros.presentation.MinistrosViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MinistrosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getMinistros: GetMinistrosUseCase
    private lateinit var refreshMinistros: RefreshMinistrosUseCase
    private lateinit var saveMinistro: SaveMinistroUseCase
    private lateinit var deleteMinistro: DeleteMinistroUseCase
    private lateinit var repository: MinistroRepository
    private lateinit var seeder: LocalSeedDataSeeder
    private lateinit var viewModel: MinistrosViewModel

    private val ministros = listOf(
        Ministro(1L, "Ana Costa", "ana@test.com", null, null, null, true, false, false, 0, FuncaoMinistro.LEITURA),
        Ministro(2L, "Bruno Lima", "bruno@test.com", null, null, null, false, false, false, 0, FuncaoMinistro.ACOLHIMENTO),
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getMinistros = mockk<GetMinistrosUseCase>()
        refreshMinistros = mockk<RefreshMinistrosUseCase>()
        saveMinistro = mockk<SaveMinistroUseCase>()
        deleteMinistro = mockk<DeleteMinistroUseCase>()
        repository = mockk<MinistroRepository>()
        seeder = mockk<LocalSeedDataSeeder>()

        every { getMinistros() } returns flowOf(ministros)
        coEvery { refreshMinistros() } returns ApiResult.Success(Unit)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading then Success`() = runTest {
        viewModel = MinistrosViewModel(getMinistros, refreshMinistros, saveMinistro, deleteMinistro, repository, seeder)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is MinistrosUiState.Success)
        val state = viewModel.uiState.value as MinistrosUiState.Success
        assertEquals(2, state.ministros.size)
    }

    @Test
    fun `search filters by nome`() = runTest {
        viewModel = MinistrosViewModel(getMinistros, refreshMinistros, saveMinistro, deleteMinistro, repository, seeder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.search("Ana")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as MinistrosUiState.Success
        assertEquals(1, state.filteredMinistros.size)
        assertEquals("Ana Costa", state.filteredMinistros[0].nome)
    }

    @Test
    fun `toggleSoAtivos filters inactive ministros`() = runTest {
        viewModel = MinistrosViewModel(getMinistros, refreshMinistros, saveMinistro, deleteMinistro, repository, seeder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSoAtivos(true)

        val state = viewModel.uiState.value as MinistrosUiState.Success
        assertTrue(state.filteredMinistros.all { it.ativo })
        assertEquals(1, state.filteredMinistros.size)
    }

    @Test
    fun `save emits Saved event on success`() = runTest {
        val ministro = ministros[0]
        coEvery { saveMinistro(ministro) } returns ApiResult.Success(ministro)
        viewModel = MinistrosViewModel(getMinistros, refreshMinistros, saveMinistro, deleteMinistro, repository, seeder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.save(ministro)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(MinistroEvent.Saved, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete emits ShowMessage on error`() = runTest {
        coEvery { deleteMinistro(1L) } returns ApiResult.Error("Não foi possível excluir")
        viewModel = MinistrosViewModel(getMinistros, refreshMinistros, saveMinistro, deleteMinistro, repository, seeder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.delete(1L)
            testDispatcher.scheduler.advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is MinistroEvent.ShowMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
