package com.escala.ministerial.feature.auditoria.data.repository

import com.escala.ministerial.core.data.database.dao.LogAuditoriaDao
import com.escala.ministerial.core.data.database.entity.LogAuditoriaEntity
import com.escala.ministerial.core.network.model.ApiResult
import com.escala.ministerial.core.network.model.safeApiCall
import com.escala.ministerial.feature.auditoria.data.datasource.AuditoriaApiService
import com.escala.ministerial.feature.auditoria.data.dto.LogAuditoriaDto
import com.escala.ministerial.feature.auditoria.domain.model.LogAuditoria
import com.escala.ministerial.feature.auditoria.domain.model.TipoAcao
import com.escala.ministerial.feature.auditoria.domain.repository.AuditoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class AuditoriaRepositoryImpl @Inject constructor(
    private val api: AuditoriaApiService,
    private val dao: LogAuditoriaDao,
) : AuditoriaRepository {

    override fun observeAll(): Flow<List<LogAuditoria>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh(): ApiResult<Unit> =
        safeApiCall {
            val dtos = api.getAll()
            dao.deleteAll()
            dao.upsertAll(dtos.map { it.toEntity() })
        }
}

private fun LogAuditoriaDto.toDomain(): LogAuditoria = LogAuditoria(
    id = id,
    entidade = entidade,
    acao = runCatching { TipoAcao.valueOf(acao) }.getOrDefault(TipoAcao.ATUALIZADO),
    statusAnterior = statusAnterior,
    statusNovo = statusNovo,
    realizadoPorId = realizadoPorId,
    dataHora = runCatching { LocalDateTime.parse(dataHora) }.getOrDefault(LocalDateTime.now()),
)

private fun LogAuditoriaDto.toEntity(): LogAuditoriaEntity = LogAuditoriaEntity(
    id = id,
    entidade = entidade,
    acao = acao,
    statusAnterior = statusAnterior,
    statusNovo = statusNovo,
    realizadoPorId = realizadoPorId,
    dataHora = runCatching { LocalDateTime.parse(dataHora) }.getOrDefault(LocalDateTime.now()),
)

private fun LogAuditoriaEntity.toDomain(): LogAuditoria = LogAuditoria(
    id = id,
    entidade = entidade,
    acao = runCatching { TipoAcao.valueOf(acao) }.getOrDefault(TipoAcao.ATUALIZADO),
    statusAnterior = statusAnterior,
    statusNovo = statusNovo,
    realizadoPorId = realizadoPorId,
    dataHora = dataHora,
)
