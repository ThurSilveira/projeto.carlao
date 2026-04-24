package com.escala.ministerial.feature.ministros.data.repository

import com.escala.ministerial.core.data.database.dao.MinistroDao
import com.escala.ministerial.core.data.database.entity.MinistroEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.ministros.data.datasource.MinistroApiService
import com.escala.ministerial.feature.ministros.data.dto.MinistroDto
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class MinistroRepositoryImpl @Inject constructor(
    private val api: MinistroApiService,
    private val dao: MinistroDao,
) : MinistroRepository {

    override fun observeAll(): Flow<List<Ministro>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }

    override suspend fun getById(id: Long): ApiResult<Ministro> =
        safeApiCall { api.getById(id).toDomain() }

    override suspend fun create(ministro: Ministro): ApiResult<Ministro> =
        safeApiCall {
            val result = api.create(ministro.toDto())
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun update(id: Long, ministro: Ministro): ApiResult<Ministro> =
        safeApiCall {
            val result = api.update(id, ministro.toDto())
            dao.upsertAll(listOf(result.toEntity()))
            result.toDomain()
        }

    override suspend fun delete(id: Long): ApiResult<Unit> =
        safeApiCall {
            api.delete(id)
            dao.deleteById(id)
        }
}

private fun MinistroDto.toDomain(): Ministro = Ministro(
    id = id ?: 0L,
    nome = nome,
    email = email,
    telefone = telefone,
    dataNascimento = dataNascimento?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    observacoes = observacoes,
    ativo = ativo,
    visitasAoInfermo = visitasAoInfermo,
    statusCurso = statusCurso,
    escalasMes = escalasMes,
    funcao = runCatching { FuncaoMinistro.valueOf(funcao) }.getOrDefault(FuncaoMinistro.LEITURA),
)

private fun MinistroDto.toEntity(): MinistroEntity = MinistroEntity(
    id = id ?: 0L,
    nome = nome,
    email = email,
    telefone = telefone,
    dataNascimento = dataNascimento?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    observacoes = observacoes,
    ativo = ativo,
    visitasAoInfermo = visitasAoInfermo,
    statusCurso = statusCurso,
    escalasMes = escalasMes,
    funcao = funcao,
)

private fun MinistroEntity.toDomain(): Ministro = Ministro(
    id = id,
    nome = nome,
    email = email,
    telefone = telefone,
    dataNascimento = dataNascimento,
    observacoes = observacoes,
    ativo = ativo,
    visitasAoInfermo = visitasAoInfermo,
    statusCurso = statusCurso,
    escalasMes = escalasMes,
    funcao = runCatching { FuncaoMinistro.valueOf(funcao) }.getOrDefault(FuncaoMinistro.LEITURA),
)

private fun Ministro.toDto(): MinistroDto = MinistroDto(
    id = if (id == 0L) null else id,
    nome = nome,
    email = email,
    telefone = telefone,
    dataNascimento = dataNascimento?.toString(),
    observacoes = observacoes,
    ativo = ativo,
    visitasAoInfermo = visitasAoInfermo,
    statusCurso = statusCurso,
    escalasMes = escalasMes,
    funcao = funcao.name,
)
