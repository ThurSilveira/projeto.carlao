package com.escala.ministerial.core.data.seed

import com.escala.ministerial.core.data.database.dao.EscalaDao
import com.escala.ministerial.core.data.database.dao.EventoDao
import com.escala.ministerial.core.data.database.dao.FeedbackDao
import com.escala.ministerial.core.data.database.dao.LogAuditoriaDao
import com.escala.ministerial.core.data.database.dao.MinistroDao
import com.escala.ministerial.core.data.database.entity.EscalaEntity
import com.escala.ministerial.core.data.database.entity.EventoEntity
import com.escala.ministerial.core.data.database.entity.FeedbackEntity
import com.escala.ministerial.core.data.database.entity.LogAuditoriaEntity
import com.escala.ministerial.core.data.database.entity.MinistroEntity
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LocalSeedDataSeeder @Inject constructor(
    private val ministroDao: MinistroDao,
    private val eventoDao: EventoDao,
    private val escalaDao: EscalaDao,
    private val feedbackDao: FeedbackDao,
    private val logAuditoriaDao: LogAuditoriaDao,
) {
    suspend fun seedIfEmpty() {
        if (ministroDao.observeAll().first().isNotEmpty()) return
        addRandom()
    }

    suspend fun addRandom() {
        val base = System.currentTimeMillis()
        val ministros = (0 until 10).map { i -> randomMinistro(base * 100 + i) }
        val eventos = (0 until 10).map { i -> randomEvento(base * 100 + i) }

        ministroDao.upsertAll(ministros)
        eventoDao.upsertAll(eventos)

        val escalas = eventos.take(6).mapIndexed { i, ev ->
            EscalaEntity(
                id = base * 100 + 200 + i,
                eventoId = ev.id,
                eventoNome = ev.nome,
                eventoData = ev.data,
                eventoHorario = ev.horario,
                dataAtribuicao = LocalDate.now().minusDays(Random.nextLong(1, 30)),
                observacao = null,
                status = listOf("PROPOSTA", "PROPOSTA", "APROVADA", "APROVADA", "CONFIRMADA", "CANCELADA")[i],
                totalMinistros = Random.nextInt(2, 6),
            )
        }
        escalaDao.upsertAll(escalas)

        val comentarios = listOf(
            "Excelente organização, muito bem executado!",
            "Foi muito boa a participação dos ministros.",
            "Ótima preparação e dedicação.",
            null, null,
        )
        val feedbacks = (0 until 6).map { i ->
            val ministro = ministros[i % ministros.size]
            val evento = eventos[i % eventos.size]
            FeedbackEntity(
                id = base * 100 + 300 + i,
                ministroId = ministro.id,
                ministroNome = ministro.nome,
                eventoId = evento.id,
                eventoNome = evento.nome,
                nota = Random.nextInt(7, 11),
                comentario = comentarios[i % comentarios.size],
                dataEnvio = LocalDateTime.now().minusDays(Random.nextLong(1, 20)),
                status = if (i < 3) "PENDENTE" else listOf("RESPONDIDO", "ARQUIVADO").random(),
                resposta = null,
            )
        }
        feedbackDao.upsertAll(feedbacks)

        val acoes = listOf("CRIADO", "ATUALIZADO", "APROVADO", "CANCELADO", "DELETADO")
        val entidades = listOf("Ministro", "Evento", "Escala", "Feedback")
        val logs = (0 until 8).map { i ->
            LogAuditoriaEntity(
                id = base * 100 + 400 + i,
                entidade = entidades[i % entidades.size],
                acao = acoes[i % acoes.size],
                statusAnterior = listOf("PROPOSTA", "PENDENTE", null)[i % 3],
                statusNovo = listOf("APROVADA", "RESPONDIDO", "CANCELADA")[i % 3],
                realizadoPorId = "admin",
                dataHora = LocalDateTime.now().minusHours(Random.nextLong(1, 48)),
            )
        }
        logAuditoriaDao.upsertAll(logs)
    }

    private fun randomMinistro(id: Long): MinistroEntity {
        val nome = "${NOMES.random()} ${SOBRENOMES.random()} ${SOBRENOMES.random()}"
        val funcoes = listOf("EUCARISTIA", "LEITURA", "ACOLHIMENTO", "MUSICA", "CATEQUESE", "ADORACAO", "OUTRO")
        val anoNasc = Random.nextInt(1960, 2005)
        val mes = Random.nextInt(1, 13)
        val dia = Random.nextInt(1, 29)
        return MinistroEntity(
            id = id,
            nome = nome,
            email = "${nome.lowercase().replace(" ", ".").take(20)}.${id % 9999}@paroquia.com",
            telefone = "(${Random.nextInt(11, 99)}) 9${Random.nextInt(1000, 9999)}-${Random.nextInt(1000, 9999)}",
            dataNascimento = LocalDate.of(anoNasc, mes, dia),
            observacoes = INDISPONIBILIDADES.random(),
            ativo = Random.nextFloat() > 0.15f,
            visitasAoInfermo = Random.nextBoolean(),
            statusCurso = Random.nextBoolean(),
            escalasMes = Random.nextInt(0, 5),
            funcao = funcoes.random(),
        )
    }

    private fun randomEvento(id: Long): EventoEntity {
        val tipos = listOf("MISSA_PAROQUIAL", "MISSA_ESPECIAL", "RETIRO", "BATIZADO", "CASAMENTO", "ADORACAO", "OUTRO")
        val tipo = tipos.random()
        val hoje = LocalDate.now()
        val diasAFrente = Random.nextInt(1, 180)
        val hora = Random.nextInt(7, 21)
        val minuto = listOf("00", "30").random()
        val locais = listOf("Igreja Matriz", "Salão Paroquial", "Praça Central", "Casa de Retiros São José", "Capelinha Nossa Senhora")
        val nomes = NOMES_EVENTOS[tipo] ?: listOf("Celebração")
        return EventoEntity(
            id = id,
            nome = nomes.random(),
            data = hoje.plusDays(diasAFrente.toLong()),
            horario = "%02d:%s".format(hora, minuto),
            tipoEvento = tipo,
            maxMinistros = Random.nextInt(2, 10),
            local = locais.random(),
            cancelado = false,
        )
    }

    private companion object {
        val NOMES = listOf(
            "João", "Maria", "Pedro", "Ana", "Carlos", "Fernanda", "Roberto", "Luciana",
            "Marcos", "Silvia", "Thiago", "Patricia", "Rafael", "Beatriz", "Felipe",
            "Camila", "Bruno", "Juliana", "Diego", "Larissa", "André", "Vanessa",
            "Gustavo", "Renata", "Rodrigo", "Cristina", "Leandro", "Mariana", "Eduardo",
            "Aline", "Fabio", "Tatiana", "Henrique", "Daniela", "Leonardo", "Sandra",
        )
        val SOBRENOMES = listOf(
            "Silva", "Santos", "Oliveira", "Souza", "Lima", "Ferreira", "Costa", "Alves",
            "Pereira", "Carvalho", "Mendes", "Ramos", "Gomes", "Vieira", "Torres",
            "Barbosa", "Prado", "Neto", "Rocha", "Cardoso", "Araújo", "Nascimento",
            "Moraes", "Martins", "Correia", "Lopes", "Cunha", "Ribeiro", "Pinto", "Cruz",
        )
        val INDISPONIBILIDADES = listOf(
            "Sem restrições de horário.",
            "Indisponível: toda segunda-feira.",
            "Indisponível: sábados de manhã.",
            "Indisponível: domingos à tarde.",
            "Indisponível: terças e quintas à noite.",
            "Indisponível: sextas-feiras.",
            "Indisponível: feriados prolongados.",
            "Disponível apenas nos fins de semana.",
            "Disponível somente às missas da manhã.",
            "Indisponível em datas de viagem (comunicar com antecedência).",
        )
        val NOMES_EVENTOS = mapOf(
            "MISSA_PAROQUIAL" to listOf("Missa Dominical — 7h", "Missa Dominical — 9h", "Missa Dominical — 11h", "Missa Dominical — 18h", "Missa Semanal"),
            "MISSA_ESPECIAL" to listOf("Missa de Corpus Christi", "Missa de Finados", "Missa de Natal", "Missa Solene", "Missa Festiva"),
            "RETIRO" to listOf("Retiro de Pentecostes", "Retiro de Advento", "Retiro de Quaresma", "Retiro Jovem", "Retiro de Casais"),
            "BATIZADO" to listOf("Batizado Comunitário", "Batizado de Adultos", "Batizado Especial"),
            "CASAMENTO" to listOf("Casamento Comunitário", "Casamento Solene"),
            "ADORACAO" to listOf("Adoração Noturna", "Adoração ao Santíssimo", "Vigília de Adoração"),
            "OUTRO" to listOf("Celebração Especial", "Encontro de Ministros", "Formação Ministerial"),
        )
    }
}
