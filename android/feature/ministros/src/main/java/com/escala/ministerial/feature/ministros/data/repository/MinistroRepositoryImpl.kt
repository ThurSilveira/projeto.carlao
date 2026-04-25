package com.escala.ministerial.feature.ministros.data.repository

import com.escala.ministerial.core.data.database.dao.IndisponibilidadeDao
import com.escala.ministerial.core.data.database.dao.MinistroDao
import com.escala.ministerial.core.data.database.entity.IndisponibilidadeEntity
import com.escala.ministerial.core.data.database.entity.MinistroEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.ministros.data.datasource.MinistroApiService
import com.escala.ministerial.feature.ministros.data.dto.IndisponibilidadeDto
import com.escala.ministerial.feature.ministros.data.dto.MinistroDto
import com.escala.ministerial.feature.ministros.domain.model.FuncaoMinistro
import com.escala.ministerial.feature.ministros.domain.model.Indisponibilidade
import com.escala.ministerial.feature.ministros.domain.model.Ministro
import com.escala.ministerial.feature.ministros.domain.repository.MinistroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class MinistroRepositoryImpl @Inject constructor(
    private val api: MinistroApiService,
    private val dao: MinistroDao,
    private val indisponibilidadeDao: IndisponibilidadeDao,
) : MinistroRepository {

    override fun observeAll(): Flow<List<Ministro>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
            dtos.forEach { dto ->
                val id = dto.id ?: return@forEach
                indisponibilidadeDao.deleteByMinistroId(id)
                indisponibilidadeDao.upsertAll(dto.indisponibilidades.map { it.toEntity(id) })
            }
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
            indisponibilidadeDao.deleteByMinistroId(id)
        }

    override suspend fun getIndisponibilidades(ministroId: Long): ApiResult<List<Indisponibilidade>> =
        safeApiCall {
            val dtos = api.getIndisponibilidades(ministroId)
            indisponibilidadeDao.deleteByMinistroId(ministroId)
            indisponibilidadeDao.upsertAll(dtos.map { it.toEntity(ministroId) })
            dtos.map { it.toDomain(ministroId) }
        }

    override suspend fun createIndisponibilidade(ministroId: Long, indisp: Indisponibilidade): ApiResult<Indisponibilidade> =
        safeApiCall {
            val result = api.createIndisponibilidade(ministroId, indisp.toDto())
            indisponibilidadeDao.upsertAll(listOf(result.toEntity(ministroId)))
            result.toDomain(ministroId)
        }

    override suspend fun updateIndisponibilidade(ministroId: Long, indisp: Indisponibilidade): ApiResult<Indisponibilidade> =
        safeApiCall {
            val result = api.updateIndisponibilidade(ministroId, indisp.id, indisp.toDto())
            indisponibilidadeDao.upsertAll(listOf(result.toEntity(ministroId)))
            result.toDomain(ministroId)
        }

    override suspend fun deleteIndisponibilidade(ministroId: Long, id: Long): ApiResult<Unit> =
        safeApiCall {
            api.deleteIndisponibilidade(ministroId, id)
            indisponibilidadeDao.deleteById(id)
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
    funcaoEspecificada = funcaoEspecificada,
    escalasAgendadas = escalasAgendadas.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() },
    indisponibilidades = indisponibilidades.map { it.toDomain(id ?: 0L) },
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
    funcaoEspecificada = funcaoEspecificada,
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
    funcaoEspecificada = funcaoEspecificada,
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
    funcaoEspecificada = funcaoEspecificada,
)

private fun IndisponibilidadeDto.toDomain(ministroId: Long): Indisponibilidade = Indisponibilidade(
    id = id ?: 0L,
    ministroId = ministroId,
    data = runCatching { LocalDate.parse(data) }.getOrDefault(LocalDate.now()),
    horarioInicio = horarioInicio,
    horarioFim = horarioFim,
    motivo = motivo,
)

private fun IndisponibilidadeDto.toEntity(ministroId: Long): IndisponibilidadeEntity = IndisponibilidadeEntity(
    id = id ?: System.currentTimeMillis(),
    ministroId = ministroId,
    data = runCatching { LocalDate.parse(data) }.getOrDefault(LocalDate.now()),
    horarioInicio = horarioInicio,
    horarioFim = horarioFim,
    motivo = motivo,
)

private fun Indisponibilidade.toDto(): IndisponibilidadeDto = IndisponibilidadeDto(
    id = if (id == 0L) null else id,
    ministroId = ministroId,
    data = data.toString(),
    horarioInicio = horarioInicio,
    horarioFim = horarioFim,
    motivo = motivo,
)
