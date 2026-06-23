package br.com.escalaministerial.service;

import br.com.escalaministerial.audit.AuditPublisher;
import br.com.escalaministerial.dto.request.MinistroRequest;
import br.com.escalaministerial.dto.response.MinistroResponse;
import br.com.escalaministerial.enums.FuncaoMinistro;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.model.Ministro;
import br.com.escalaministerial.repository.MinistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import br.com.escalaministerial.enums.TipoAcao;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinistroServiceTest {

    @Mock
    private MinistroRepository repository;
    @Mock
    private AuditPublisher auditPublisher;

    private MinistroService service;

    @BeforeEach
    void setUp() {
        service = new MinistroService(repository, auditPublisher);
    }

    @Test
    void listar_DeveRetornarLista() {
        when(repository.findAll()).thenReturn(List.of(
                new Ministro("Pedro", "pedro@igreja.org", FuncaoMinistro.LEITURA)
        ));

        List<MinistroResponse> result = service.listar();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nome()).isEqualTo("Pedro");
    }

    @Test
    void obter_QuandoExiste_DeveRetornar() {
        Ministro m = new Ministro("Pedro", "pedro@igreja.org", FuncaoMinistro.LEITURA);
        m.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(m));

        MinistroResponse result = service.obter(1L);

        assertThat(result.nome()).isEqualTo("Pedro");
    }

    @Test
    void obter_QuandoNaoExiste_DeveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obter(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criar_DeveSalvarEPublicarAuditoria() {
        MinistroRequest request = new MinistroRequest(
                "Pedro", "pedro@igreja.org", null, null, null,
                true, false, false, 0, FuncaoMinistro.LEITURA, null
        );
        when(repository.save(any())).thenAnswer(invocation -> {
            Ministro m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MinistroResponse result = service.criar(request);

        assertThat(result.nome()).isEqualTo("Pedro");
        assertThat(result.email()).isEqualTo("pedro@igreja.org");
        verify(auditPublisher).publicar("Ministro", TipoAcao.CRIADO, null, "Pedro");
    }

    @Test
    void deletar_QuandoNaoExiste_DeveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
