package br.com.escalaministerial.controller;

import br.com.escalaministerial.enums.FuncaoMinistro;
import br.com.escalaministerial.enums.TipoEvento;
import br.com.escalaministerial.model.Evento;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.EventoRepository;
import br.com.escalaministerial.repository.MinistroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seed")
public class SeedController {

    private final MinistroRepository ministroRepository;
    private final EventoRepository eventoRepository;

    public SeedController(MinistroRepository ministroRepository, EventoRepository eventoRepository) {
        this.ministroRepository = ministroRepository;
        this.eventoRepository = eventoRepository;
    }

    @GetMapping
    public ResponseEntity<String> criarSeed() {
        if (!ministroRepository.findAll().isEmpty() || !eventoRepository.findAll().isEmpty()) {
            return ResponseEntity.ok("Seed já aplicada");
        }

        Ministro m1 = new Ministro("Pedro Silva", "pedro@igreja.org", FuncaoMinistro.LEITURA);
        Ministro m2 = new Ministro("Maria Costa", "maria@igreja.org", FuncaoMinistro.EUCARISTIA);
        ministroRepository.saveAll(List.of(m1, m2));

        Evento e1 = new Evento("Missa de Domingo", LocalDate.now().plusDays(3), "10:00", TipoEvento.MISSA_PAROQUIAL);
        Evento e2 = new Evento("Retiro de Jovens", LocalDate.now().plusWeeks(1), "08:30", TipoEvento.RETIRO);
        eventoRepository.saveAll(List.of(e1, e2));

        return ResponseEntity.ok("Seed local criada");
    }
}
