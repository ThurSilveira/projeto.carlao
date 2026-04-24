package com.exemplo.escala.service;

import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.enums.FuncaoMinistro;
import com.exemplo.escala.model.enums.TipoEvento;
import com.exemplo.escala.service.EscalaService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Runner autossuficiente para validar o algoritmo de sorteio.
 *
 * Compile:  mvn test-compile
 * Execute:  java -cp target/test-classes:target/classes com.exemplo.escala.SimulacaoEscalasMain
 *
 * Não requer junit-platform-launcher nem surefire — roda com o JDK puro.
 */
public class SimulacaoEscalasMain {

    // Usamos EscalaService diretamente: o método selecionarMinistros é
    // package-private e não acessa nenhum repositório (lógica pura).
    private static final EscalaService servico = new EscalaService();

    private static final String[] HORARIOS = {
        "07:00", "08:00", "09:30", "10:00", "11:00",
        "15:00", "16:30", "18:00", "19:00", "19:30"
    };
    private static final int[] VAGAS = {4, 6, 6, 8, 6, 10, 6, 4, 8, 6};
    private static final TipoEvento[] TIPOS = TipoEvento.values();
    private static final FuncaoMinistro[] FUNCOES = FuncaoMinistro.values();

    private static int passaram = 0;
    private static int falharam = 0;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Simulação: 100 Ministros × 30 Eventos               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        testar("SEM REPETIÇÃO no mesmo evento",              SimulacaoEscalasMain::semRepeticaoNoMesmoEvento);
        testar("RESPEITA maxMinistros do evento",            SimulacaoEscalasMain::respeitaMaxMinistros);
        testar("POOL MENOR que vagas não estoura",           SimulacaoEscalasMain::poolMenorQueVagas);
        testar("POOL VAZIO retorna lista vazia",             SimulacaoEscalasMain::poolVazioRetornaListaVazia);
        testar("PRIORIDADE: menos escalas são escolhidos",   SimulacaoEscalasMain::prioridadeMenosEscalas);
        testar("IMBALANCE ≤ 1 após cada evento",             SimulacaoEscalasMain::imbalanceMaximo1);
        testar("TODOS ESCALADOS ao menos 1x em 30 eventos",  SimulacaoEscalasMain::todosEscaladosEmMesCompleto);
        testar("DISTRIBUIÇÃO JUSTA: imbalance final ≤ 1",    SimulacaoEscalasMain::distribuicaoJusta);
        testar("FUNÇÕES DIVERSAS cobertas nos 30 eventos",   SimulacaoEscalasMain::diversasFuncoes);
        testar("DOIS PERÍODOS no mesmo dia sem repetição",   SimulacaoEscalasMain::doisPeriodosNoMesmoDia);
        testar("SIMULAÇÃO COMPLETA com relatório",           SimulacaoEscalasMain::simulacaoComRelatorio);

        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  Resultado: %d passaram   %d falharam%n", passaram, falharam);
        System.out.println("══════════════════════════════════════════════════════");

        if (falharam > 0) {
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------
    // Runner de testes
    // -------------------------------------------------------------------------

    @FunctionalInterface
    interface Teste { void executar(); }

    private static void testar(String nome, Teste teste) {
        System.out.printf("  %-52s", nome + " ...");
        try {
            teste.executar();
            System.out.println("PASSOU ✓");
            passaram++;
        } catch (AssertionError | RuntimeException e) {
            System.out.println("FALHOU ✗");
            System.out.println("    → " + e.getMessage());
            falharam++;
        }
    }

    private static void assertEquals(int esperado, int obtido, String msg) {
        if (esperado != obtido) {
            throw new AssertionError(msg + " — esperado: " + esperado + ", obtido: " + obtido);
        }
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    // -------------------------------------------------------------------------
    // Testes
    // -------------------------------------------------------------------------

    static void semRepeticaoNoMesmoEvento() {
        List<Ministro> pool = ministros(100);
        List<Ministro> sel = servico.selecionarMinistros(pool, 10);
        Set<Long> ids = sel.stream().map(Ministro::getId).collect(Collectors.toSet());
        assertEquals(sel.size(), ids.size(), "IDs únicos esperados — há duplicatas no mesmo evento");
    }

    static void respeitaMaxMinistros() {
        List<Ministro> pool = ministros(100);
        for (int vagas : VAGAS) {
            List<Ministro> sel = servico.selecionarMinistros(pool, vagas);
            assertEquals(vagas, sel.size(), "Vagas=" + vagas);
        }
    }

    static void poolMenorQueVagas() {
        List<Ministro> pool = ministros(3);
        List<Ministro> sel = servico.selecionarMinistros(pool, 10);
        assertEquals(3, sel.size(), "Deve retornar no máximo 3 (tamanho do pool)");
    }

    static void poolVazioRetornaListaVazia() {
        List<Ministro> sel = servico.selecionarMinistros(Collections.emptyList(), 6);
        assertEquals(0, sel.size(), "Pool vazio deve retornar lista vazia");
    }

    static void prioridadeMenosEscalas() {
        List<Ministro> pool = ministros(100);
        // Dá 5 escalas para os primeiros 10
        pool.subList(0, 10).forEach(m -> m.setEscalasMes(5));

        // Em 20 rodadas, nenhum dos 10 "sobrecarregados" deve ser sorteado
        // (há 90 com 0 escalas disponíveis)
        for (int r = 0; r < 20; r++) {
            List<Ministro> sel = servico.selecionarMinistros(pool, 6);
            boolean algumSobrecarregado = sel.stream().anyMatch(m -> m.getEscalasMes() == 5);
            assertTrue(!algumSobrecarregado,
                "Rodada " + r + ": ministro com 5 escalas foi selecionado com 90 disponíveis com 0");
        }
    }

    static void imbalanceMaximo1() {
        List<Ministro> pool = ministros(100);
        List<Evento> eventos = eventos(30);

        for (int i = 0; i < eventos.size(); i++) {
            int vagas = VAGAS[i % VAGAS.length];
            List<Ministro> sel = servico.selecionarMinistros(pool, vagas);
            sel.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));

            int max = pool.stream().mapToInt(m -> m.getEscalasMes()).max().orElse(0);
            int min = pool.stream().mapToInt(m -> m.getEscalasMes()).min().orElse(0);
            assertTrue(max - min <= 1,
                "Evento " + (i + 1) + ": imbalance=" + (max - min) + " (max=" + max + ", min=" + min + ")");
        }
    }

    static void todosEscaladosEmMesCompleto() {
        List<Ministro> pool = ministros(100);
        List<Evento> eventos = eventos(30);
        int totalAtribuicoes = 0;

        for (int i = 0; i < eventos.size(); i++) {
            int vagas = VAGAS[i % VAGAS.length];
            List<Ministro> sel = servico.selecionarMinistros(pool, vagas);
            sel.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));
            totalAtribuicoes += vagas;
        }

        long semEscala = pool.stream().filter(m -> m.getEscalasMes() == 0).count();
        assertTrue(semEscala == 0,
            totalAtribuicoes + " atribuições para 100 ministros — " + semEscala + " ficaram sem escala");
    }

    static void distribuicaoJusta() {
        List<Ministro> pool = ministros(100);
        List<Evento> eventos = eventos(30);

        for (int i = 0; i < eventos.size(); i++) {
            int vagas = VAGAS[i % VAGAS.length];
            List<Ministro> sel = servico.selecionarMinistros(pool, vagas);
            sel.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));
        }

        int max = pool.stream().mapToInt(m -> m.getEscalasMes()).max().orElse(0);
        int min = pool.stream().mapToInt(m -> m.getEscalasMes()).min().orElse(0);
        assertTrue(max - min <= 1,
            "Imbalance final=" + (max - min) + " (max=" + max + ", min=" + min + ") — esperado ≤ 1");
    }

    static void diversasFuncoes() {
        List<Ministro> pool = ministros(100);
        Set<FuncaoMinistro> encontradas = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            List<Ministro> sel = servico.selecionarMinistros(pool, VAGAS[i % VAGAS.length]);
            sel.forEach(m -> {
                encontradas.add(m.getFuncao());
                m.setEscalasMes(m.getEscalasMes() + 1);
            });
        }

        Set<FuncaoMinistro> todasFuncoes = new HashSet<>(Arrays.asList(FuncaoMinistro.values()));
        Set<FuncaoMinistro> faltando = new HashSet<>(todasFuncoes);
        faltando.removeAll(encontradas);
        assertTrue(faltando.isEmpty(),
            "Funções não cobertas nos 30 eventos: " + faltando);
    }

    static void doisPeriodosNoMesmoDia() {
        List<Ministro> pool = ministros(100);

        List<Ministro> manha = servico.selecionarMinistros(pool, 8);
        manha.forEach(m -> m.setEscalasMes(m.getEscalasMes() + 1));

        List<Ministro> noite = servico.selecionarMinistros(pool, 8);

        Set<Long> idsManha = manha.stream().map(Ministro::getId).collect(Collectors.toSet());
        Set<Long> idsNoite = noite.stream().map(Ministro::getId).collect(Collectors.toSet());
        Set<Long> intersecao = new HashSet<>(idsManha);
        intersecao.retainAll(idsNoite);

        assertTrue(intersecao.isEmpty(),
            "Com 100 ministros e 8 vagas/período, não deve repetir no mesmo dia. Repetidos: " + intersecao.size());
    }

    static void simulacaoComRelatorio() {
        List<Ministro> pool = ministros(100);
        List<Evento> eventos = eventos(30);
        Map<Long, Integer> contador = new LinkedHashMap<>();
        int totalAtribuicoes = 0;

        for (int i = 0; i < eventos.size(); i++) {
            Evento ev = eventos.get(i);
            List<Ministro> sel = servico.selecionarMinistros(pool, ev.getMaxMinistros());

            // Sem duplicatas no mesmo evento
            Set<Long> ids = sel.stream().map(Ministro::getId).collect(Collectors.toSet());
            assertTrue(ids.size() == sel.size(),
                "Evento " + (i + 1) + " tem duplicatas!");

            sel.forEach(m -> {
                m.setEscalasMes(m.getEscalasMes() + 1);
                contador.merge(m.getId(), 1, Integer::sum);
            });
            totalAtribuicoes += sel.size();
        }

        int max = pool.stream().mapToInt(m -> m.getEscalasMes()).max().orElse(0);
        int min = pool.stream().mapToInt(m -> m.getEscalasMes()).min().orElse(0);

        // Relatório
        System.out.println();
        System.out.println("    ┌─────────────────────────────────────────────┐");
        System.out.printf( "    │ Total de atribuições  : %-20d│%n", totalAtribuicoes);
        System.out.printf( "    │ Ministros escalados   : %-20s│%n", contador.size() + " / 100");
        System.out.printf( "    │ Média de escalas/min  : %-20.1f│%n", totalAtribuicoes / 100.0);
        System.out.printf( "    │ Máximo de escalas     : %-20d│%n", max);
        System.out.printf( "    │ Mínimo de escalas     : %-20d│%n", min);
        System.out.printf( "    │ Imbalance final       : %-20d│%n", max - min);
        System.out.println("    ├─────────────────────────────────────────────┤");

        Map<Integer, Long> hist = pool.stream()
            .collect(Collectors.groupingBy(Ministro::getEscalasMes, Collectors.counting()));
        hist.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e ->
            System.out.printf("    │  %2d escala(s) → %3d ministros               │%n",
                e.getKey(), e.getValue()));
        System.out.println("    └─────────────────────────────────────────────┘");
        System.out.println();

        assertTrue(max - min <= 1, "Imbalance final > 1");
    }

    // -------------------------------------------------------------------------
    // Fábricas
    // -------------------------------------------------------------------------

    private static List<Ministro> ministros(int n) {
        List<Ministro> lista = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Ministro m = new Ministro();
            m.setId((long) i);
            m.setNome(String.format("Ministro %03d", i));
            m.setEmail("ministro" + i + "@paroquia.org");
            m.setTelefone("(11) 9" + String.format("%08d", i));
            m.setAtivo(true);
            m.setEscalasMes(0);
            m.setFuncao(FUNCOES[(i - 1) % FUNCOES.length]);
            m.setStatusCurso(i % 3 != 0);
            m.setVisitasAoInfermo(i % 5 == 0);
            lista.add(m);
        }
        return lista;
    }

    private static List<Evento> eventos(int n) {
        List<Evento> lista = new ArrayList<>();
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        for (int i = 0; i < n; i++) {
            Evento e = new Evento();
            e.setId((long)(i + 1));
            e.setNome(String.format("Evento %02d", i + 1));
            e.setData(inicio.plusDays(i));
            e.setHorario(HORARIOS[i % HORARIOS.length]);
            e.setTipoEvento(TIPOS[i % TIPOS.length]);
            e.setMaxMinistros(VAGAS[i % VAGAS.length]);
            e.setLocal(i % 2 == 0 ? "Igreja Matriz" : "Capelinha São José");
            e.setCancelado(false);
            lista.add(e);
        }
        return lista;
    }
}
