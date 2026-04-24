package com.exemplo.escala.service;

import com.exemplo.escala.dto.EscalaDTO;
import com.exemplo.escala.model.Escala;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.enums.FuncaoMinistro;
import com.exemplo.escala.model.enums.StatusEscala;
import com.exemplo.escala.model.enums.TipoEvento;
import com.exemplo.escala.repository.EscalaRepository;
import com.exemplo.escala.repository.EventoRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testa o algoritmo de sorteio de escalas com 100 ministros e 30 eventos.
 *
 * Propriedades verificadas:
 *  1. Nenhum ministro aparece duas vezes no mesmo evento
 *  2. O número de ministros sorteados respeita maxMinistros
 *  3. Ministros com menos escalas no mês são priorizados
 *  4. Imbalance máximo entre ministros ≤ 1 após qualquer evento
 *  5. Distribuição correta em simulação com horários e funções variadas
 */
@ExtendWith(MockitoExtension.class)
class EscalaServiceTest {

    @Mock private EscalaRepository escalaRepository;
    @Mock private EventoRepository eventoRepository;
    @Mock private MinistroRepository ministroRepository;
    @Mock private LogAuditoriaService auditoriaService;
    @Mock private EventoService eventoService;

    @InjectMocks
    private EscalaService escalaService;

    // Horários e capacidades variados para simular um mês real
    private static final String[] HORARIOS = {
        "07:00", "08:00", "09:30", "10:00", "11:00",
        "15:00", "16:30", "18:00", "19:00", "19:30"
    };
    private static final int[] VAGAS_POR_EVENTO = {4, 6, 6, 8, 6, 10, 6, 4, 8, 6};
    private static final TipoEvento[] TIPOS = TipoEvento.values();
    private static final FuncaoMinistro[] FUNCOES = FuncaoMinistro.values();

    private List<Ministro> pool100;
    private List<Evento> eventos30;

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        pool100 = criarMinistros(100);
        eventos30 = criarEventos30Dias();
    }

    // -------------------------------------------------------------------------
    // Testes unitários do algoritmo puro (sem mocks)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Nenhum ministro se repete no mesmo evento")
    void naoRepetirMinistroNoMesmoEvento() {
        List<Ministro> selecionados = escalaService.selecionarMinistros(pool100, 8);

        Set<Long> ids = selecionados.stream()
            .map(Ministro::getId)
            .collect(Collectors.toSet());

        assertThat(ids)
            .as("IDs únicos — sem duplicatas no mesmo evento")
            .hasSize(selecionados.size());
    }

    @Test
    @DisplayName("Respeita maxMinistros do evento")
    void respeitaMaxMinistros() {
        for (int vagas : VAGAS_POR_EVENTO) {
            List<Ministro> selecionados = escalaService.selecionarMinistros(pool100, vagas);
            assertThat(selecionados.size())
                .as("Evento com %d vagas deve selecionar exatamente %d ministros", vagas, vagas)
                .isEqualTo(vagas);
        }
    }

    @Test
    @DisplayName("Funciona quando há menos ministros do que vagas")
    void funcionaComMenosMinistrosQueVagas() {
        List<Ministro> poucos = criarMinistros(3);
        List<Ministro> selecionados = escalaService.selecionarMinistros(poucos, 10);

        assertThat(selecionados)
            .as("Deve selecionar todos os 3 disponíveis (não pode exceder o pool)")
            .hasSize(3);
    }

    @Test
    @DisplayName("Pool vazio retorna lista vazia")
    void poolVazioRetornaListaVazia() {
        List<Ministro> selecionados = escalaService.selecionarMinistros(Collections.emptyList(), 6);
        assertThat(selecionados).isEmpty();
    }

    @Test
    @DisplayName("Ministros com menos escalas são priorizados sobre os com mais")
    void ministrosComMenosEscalasSaoPriorizados() {
        // 10 ministros com 5 escalas, 90 com 0
        List<Ministro> pool = criarMinistros(100);
        pool.subList(0, 10).forEach(m -> m.setEscalasMes(5));

        // Repetindo 20x para garantir que os com 0 sejam sempre escolhidos primeiro
        for (int rodada = 0; rodada < 20; rodada++) {
            List<Ministro> selecionados = escalaService.selecionarMinistros(pool, 6);

            boolean algumComCinco = selecionados.stream().anyMatch(m -> m.getEscalasMes() == 5);
            assertThat(algumComCinco)
                .as("Rodada %d: não deve selecionar ministros com 5 escalas quando há 90 com 0", rodada)
                .isFalse();
        }
    }

    @Test
    @DisplayName("Imbalance máximo entre ministros é ≤ 1 após cada evento")
    void imbalanceMaximo1AposEach() {
        List<Ministro> pool = criarMinistros(100);

        for (int evento = 0; evento < eventos30.size(); evento++) {
            int vagas = VAGAS_POR_EVENTO[evento % VAGAS_POR_EVENTO.length];
            List<Ministro> selecionados = escalaService.selecionarMinistros(pool, vagas);
            selecionados.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));

            int max = pool.stream().mapToInt(m -> m.getEscalasMes()).max().orElse(0);
            int min = pool.stream().mapToInt(m -> m.getEscalasMes()).min().orElse(0);

            assertThat(max - min)
                .as("Após evento %d: imbalance deve ser ≤ 1 (max=%d, min=%d)", evento + 1, max, min)
                .isLessThanOrEqualTo(1);
        }
    }

    // -------------------------------------------------------------------------
    // Simulação completa: 100 ministros × 30 eventos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Simulação completa — 30 eventos, 100 ministros, horários variados")
    void simulacaoCompleta30Eventos100Ministros() {
        List<Ministro> pool = criarMinistros(100);
        int totalAtribuicoes = 0;
        Map<Long, Integer> contadorPorMinistro = new HashMap<>();

        for (int i = 0; i < eventos30.size(); i++) {
            Evento evento = eventos30.get(i);
            int vagas = evento.getMaxMinistros();

            List<Ministro> selecionados = escalaService.selecionarMinistros(pool, vagas);

            // 1. Tamanho correto
            assertThat(selecionados.size())
                .as("Evento '%s' às %s deve ter %d ministros", evento.getNome(), evento.getHorario(), vagas)
                .isEqualTo(vagas);

            // 2. Sem duplicatas no mesmo evento
            Set<Long> idsEvento = selecionados.stream().map(Ministro::getId).collect(Collectors.toSet());
            assertThat(idsEvento.size())
                .as("Evento %d não deve ter ministros duplicados", i + 1)
                .isEqualTo(selecionados.size());

            // 3. Todos selecionados são do pool original
            Set<Long> idsPool = pool.stream().map(Ministro::getId).collect(Collectors.toSet());
            assertThat(idsPool).as("Todos os selecionados devem pertencer ao pool").containsAll(idsEvento);

            // Atualizar contadores
            selecionados.forEach(m -> {
                m.setEscalasMes(m.getEscalasMes() + 1);
                contadorPorMinistro.merge(m.getId(), 1, Integer::sum);
            });
            totalAtribuicoes += vagas;
        }

        // 4. Total de atribuições bate com o esperado
        int somaContadores = contadorPorMinistro.values().stream().mapToInt(Integer::intValue).sum();
        assertThat(somaContadores)
            .as("Soma de todas as atribuições deve bater com o total de vagas preenchidas")
            .isEqualTo(totalAtribuicoes);

        // 5. Nenhum ministro ficou sem nenhuma atribuição (com 180+ slots para 100 ministros)
        int ministrosSemEscala = (int)(pool.stream().filter(m -> m.getEscalasMes() == 0).count());
        assertThat(ministrosSemEscala)
            .as("Com %d atribuições para %d ministros, todos devem ser escalados ao menos 1x",
                totalAtribuicoes, pool.size())
            .isZero();

        // 6. Distribuição justa: imbalance final ≤ 1
        int max = pool.stream().mapToInt(m -> m.getEscalasMes()).max().orElse(0);
        int min = pool.stream().mapToInt(m -> m.getEscalasMes()).min().orElse(0);
        assertThat(max - min)
            .as("Imbalance final (max=%d, min=%d) deve ser ≤ 1", max, min)
            .isLessThanOrEqualTo(1);

        // Relatório para acompanhamento
        System.out.printf("%n=== Simulação 30 Eventos / 100 Ministros ===%n");
        System.out.printf("Total de atribuições  : %d%n", totalAtribuicoes);
        System.out.printf("Ministros escalados   : %d / 100%n", contadorPorMinistro.size());
        System.out.printf("Média de escalas/min  : %.1f%n", totalAtribuicoes / 100.0);
        System.out.printf("Máximo de escalas     : %d%n", max);
        System.out.printf("Mínimo de escalas     : %d%n", min);
        System.out.printf("Imbalance final       : %d%n", max - min);

        // Histograma de distribuição
        Map<Integer, Long> histograma = pool.stream()
            .collect(Collectors.groupingBy(Ministro::getEscalasMes, Collectors.counting()));
        histograma.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> System.out.printf("  %d escala(s) — %d ministros%n", e.getKey(), e.getValue()));
    }

    @Test
    @DisplayName("Funções ministeriais diversas são cobertas nos 30 eventos")
    void diversasFuncoesCobertas() {
        List<Ministro> pool = criarMinistros(100); // distribui todas as funções

        Set<FuncaoMinistro> funcoesEscaladas = new HashSet<>();
        for (int i = 0; i < eventos30.size(); i++) {
            int vagas = VAGAS_POR_EVENTO[i % VAGAS_POR_EVENTO.length];
            List<Ministro> selecionados = escalaService.selecionarMinistros(pool, vagas);
            selecionados.forEach(m -> {
                funcoesEscaladas.add(m.getFuncao());
                m.setEscalasMes(m.getEscalasMes() + 1);
            });
        }

        assertThat(funcoesEscaladas)
            .as("Todas as funções ministeriais devem aparecer ao longo dos 30 eventos")
            .containsAll(Arrays.asList(FuncaoMinistro.values()));
    }

    @Test
    @DisplayName("Eventos em horários distintos no mesmo dia não repetem ministros entre si")
    void eventosNoMesmoDiaNaoRepetemMinistros() {
        List<Ministro> pool = criarMinistros(100);

        // Simula dois eventos no mesmo dia (manhã e noite)
        List<Ministro> manha = escalaService.selecionarMinistros(pool, 8);
        manha.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));

        List<Ministro> noite = escalaService.selecionarMinistros(pool, 8);

        Set<Long> idsManha = manha.stream().map(Ministro::getId).collect(Collectors.toSet());
        Set<Long> idsNoite = noite.stream().map(Ministro::getId).collect(Collectors.toSet());

        // Como o algoritmo prioriza quem tem menos escalas, ninguém que foi de manhã
        // deve ser sorteado à noite (enquanto houver outros disponíveis)
        Set<Long> intersecao = new HashSet<>(idsManha);
        intersecao.retainAll(idsNoite);

        assertThat(intersecao)
            .as("Com 100 ministros e apenas 8 vagas por período, não deve haver repetição no mesmo dia")
            .isEmpty();
    }

    // -------------------------------------------------------------------------
    // Teste do método gerarEscala (com mocks de repositório)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("gerarEscala persiste a escala e retorna DTO com ministros sorteados")
    void gerarEscalaPersisteERetornaDTO() {
        Evento evento = eventos30.get(0); // 6 vagas
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(ministroRepository.findByAtivoTrue()).thenReturn(pool100);
        when(escalaRepository.save(any(Escala.class))).thenAnswer(inv -> {
            Escala e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        when(eventoService.toDTO(any())).thenReturn(null); // não é o foco desse teste
        when(ministroRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any());

        EscalaDTO resultado = escalaService.gerarEscala(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEscalaMinistros())
            .as("Deve conter exatamente %d ministros sorteados", evento.getMaxMinistros())
            .hasSize(evento.getMaxMinistros());
        assertThat(resultado.getStatus()).isEqualTo(StatusEscala.PROPOSTA.name());

        verify(escalaRepository, times(1)).save(any(Escala.class));
        verify(ministroRepository, times(1)).saveAll(anyList());
        verify(auditoriaService, times(1)).registrar(eq("Escala"), eq("CRIADO"), isNull(), eq("PROPOSTA"));
    }

    @Test
    @DisplayName("gerarEscala lança exceção para evento cancelado")
    void gerarEscalaEventoCancelado() {
        Evento cancelado = new Evento();
        cancelado.setId(99L);
        cancelado.setCancelado(true);
        cancelado.setNome("Evento cancelado");

        when(eventoRepository.findById(99L)).thenReturn(Optional.of(cancelado));

        assertThatThrownBy(() -> escalaService.gerarEscala(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cancelado");
    }

    @Test
    @DisplayName("gerarEscala lança exceção quando não há ministros ativos")
    void gerarEscalaSemMinistros() {
        Evento evento = eventos30.get(0);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(ministroRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> escalaService.gerarEscala(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nenhum ministro ativo");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<Ministro> criarMinistros(int quantidade) {
        List<Ministro> lista = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            Ministro m = new Ministro();
            m.setId((long) i);
            m.setNome("Ministro " + String.format("%03d", i));
            m.setEmail("ministro" + i + "@paroquia.org");
            m.setTelefone("(11) 9" + String.format("%08d", i));
            m.setAtivo(true);
            m.setEscalasMes(0);
            m.setFuncao(FUNCOES[(i - 1) % FUNCOES.length]); // distribui todas as funções
            m.setStatusCurso(i % 3 != 0);                    // 2/3 com curso
            m.setVisitasAoInfermo(i % 5 == 0);               // 20% visitam enfermos
            lista.add(m);
        }
        return lista;
    }

    private List<Evento> criarEventos30Dias() {
        List<Evento> lista = new ArrayList<>();
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);

        for (int i = 0; i < 30; i++) {
            Evento e = new Evento();
            e.setId((long)(i + 1));
            e.setNome("Evento " + String.format("%02d", i + 1));
            e.setData(inicio.plusDays(i));
            e.setHorario(HORARIOS[i % HORARIOS.length]);
            e.setTipoEvento(TIPOS[i % TIPOS.length]);
            e.setMaxMinistros(VAGAS_POR_EVENTO[i % VAGAS_POR_EVENTO.length]);
            e.setLocal(i % 2 == 0 ? "Igreja Matriz" : "Capelinha São José");
            e.setCancelado(false);
            lista.add(e);
        }
        return lista;
    }
}
