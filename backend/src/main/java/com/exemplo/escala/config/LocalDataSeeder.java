package com.exemplo.escala.config;

import com.exemplo.escala.model.Evento;
import com.exemplo.escala.model.Ministro;
import com.exemplo.escala.model.enums.FuncaoMinistro;
import com.exemplo.escala.model.enums.TipoEvento;
import com.exemplo.escala.repository.EventoRepository;
import com.exemplo.escala.repository.MinistroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
@Profile("local")
public class LocalDataSeeder implements CommandLineRunner {

    private final MinistroRepository ministroRepository;
    private final EventoRepository eventoRepository;

    private static final String[] NOMES = {
        "João", "Maria", "Pedro", "Ana", "Carlos", "Fernanda", "Roberto", "Luciana",
        "Marcos", "Silvia", "Thiago", "Patricia", "Rafael", "Beatriz", "Felipe",
        "Camila", "Bruno", "Juliana", "Diego", "Larissa", "André", "Vanessa",
        "Gustavo", "Renata", "Rodrigo", "Cristina", "Leandro", "Mariana", "Eduardo", "Aline",
    };
    private static final String[] SOBRENOMES = {
        "Silva", "Santos", "Oliveira", "Souza", "Lima", "Ferreira", "Costa", "Alves",
        "Pereira", "Carvalho", "Mendes", "Ramos", "Gomes", "Vieira", "Torres",
        "Barbosa", "Prado", "Neto", "Rocha", "Cardoso", "Araújo", "Nascimento",
        "Moraes", "Martins", "Correia", "Lopes", "Cunha", "Ribeiro", "Pinto", "Cruz",
    };
    private static final String[] INDISPONIBILIDADES = {
        "Sem restrições de horário.",
        "Indisponível: toda segunda-feira.",
        "Indisponível: sábados de manhã.",
        "Indisponível: domingos à tarde.",
        "Indisponível: terças e quintas à noite.",
        "Indisponível: sextas-feiras.",
        "Disponível apenas nos fins de semana.",
        "Disponível somente às missas da manhã.",
    };
    private static final String[][] NOMES_EVENTOS = {
        {"Missa Dominical — 7h", "Missa Dominical — 9h", "Missa Dominical — 11h", "Missa Dominical — 18h"},
        {"Missa de Corpus Christi", "Missa de Finados", "Missa Solene", "Missa Festiva"},
        {"Retiro de Pentecostes", "Retiro de Advento", "Retiro de Quaresma", "Retiro Jovem"},
        {"Batizado Comunitário", "Batizado de Adultos"},
        {"Casamento Comunitário", "Casamento Solene"},
        {"Adoração Noturna", "Adoração ao Santíssimo", "Vigília de Adoração"},
        {"Celebração Especial", "Encontro de Ministros", "Formação Ministerial"},
    };
    private static final String[] LOCAIS = {
        "Igreja Matriz", "Salão Paroquial", "Praça Central",
        "Casa de Retiros São José", "Capelinha Nossa Senhora",
    };
    private static final FuncaoMinistro[] FUNCOES = FuncaoMinistro.values();
    private static final TipoEvento[] TIPOS_EVENTO = TipoEvento.values();
    private static final int[] HORARIOS_H = {7, 8, 9, 10, 11, 15, 16, 18, 19, 20};

    public LocalDataSeeder(MinistroRepository ministroRepository, EventoRepository eventoRepository) {
        this.ministroRepository = ministroRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void run(String... args) {
        if (ministroRepository.count() > 0) return;
        addRandom(10);
    }

    public void addRandom(int quantidade) {
        ministroRepository.saveAll(gerarMinistros(quantidade));
        eventoRepository.saveAll(gerarEventos(quantidade));
    }

    public List<Ministro> gerarMinistros(int n) {
        Random rnd = new Random();
        return java.util.stream.IntStream.range(0, n).mapToObj(i -> {
            String nome = NOMES[rnd.nextInt(NOMES.length)]
                    + " " + SOBRENOMES[rnd.nextInt(SOBRENOMES.length)]
                    + " " + SOBRENOMES[rnd.nextInt(SOBRENOMES.length)];
            Ministro m = new Ministro();
            m.setNome(nome);
            m.setEmail(nome.toLowerCase().replace(" ", ".").substring(0, Math.min(20, nome.length()))
                    + "." + System.nanoTime() % 99999 + "@paroquia.com");
            m.setTelefone(String.format("(%02d) 9%04d-%04d",
                    rnd.nextInt(89) + 11, rnd.nextInt(9000) + 1000, rnd.nextInt(9000) + 1000));
            m.setDataNascimento(LocalDate.of(rnd.nextInt(45) + 1960, rnd.nextInt(12) + 1, rnd.nextInt(28) + 1));
            m.setAtivo(rnd.nextFloat() > 0.15f);
            m.setVisitasAoInfermo(rnd.nextBoolean());
            m.setStatusCurso(rnd.nextBoolean());
            m.setEscalasMes(rnd.nextInt(5));
            m.setFuncao(FUNCOES[rnd.nextInt(FUNCOES.length)]);
            m.setObservacoes(INDISPONIBILIDADES[rnd.nextInt(INDISPONIBILIDADES.length)]);
            return m;
        }).toList();
    }

    public List<Evento> gerarEventos(int n) {
        Random rnd = new Random();
        return java.util.stream.IntStream.range(0, n).mapToObj(i -> {
            TipoEvento tipo = TIPOS_EVENTO[rnd.nextInt(TIPOS_EVENTO.length)];
            String[] nomesDoTipo = NOMES_EVENTOS[tipo.ordinal() % NOMES_EVENTOS.length];
            int hora = HORARIOS_H[rnd.nextInt(HORARIOS_H.length)];
            Evento e = new Evento();
            e.setNome(nomesDoTipo[rnd.nextInt(nomesDoTipo.length)]);
            e.setData(LocalDate.now().plusDays(rnd.nextInt(180) + 1));
            e.setHorario(String.format("%02d:%s", hora, rnd.nextBoolean() ? "00" : "30"));
            e.setTipoEvento(tipo);
            e.setMaxMinistros(rnd.nextInt(8) + 2);
            e.setLocal(LOCAIS[rnd.nextInt(LOCAIS.length)]);
            return e;
        }).toList();
    }
}
