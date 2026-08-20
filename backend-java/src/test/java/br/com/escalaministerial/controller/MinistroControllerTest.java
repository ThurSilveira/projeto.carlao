package br.com.escalaministerial.controller;

import br.com.escalaministerial.dto.response.MinistroResponse;
import br.com.escalaministerial.enums.FuncaoMinistro;
import br.com.escalaministerial.exception.ResourceNotFoundException;
import br.com.escalaministerial.service.MinistroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MinistroController.class)
class MinistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MinistroService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listar_DeveRetornar200() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new MinistroResponse(1L, "Pedro", "pedro@igreja.org", null,
                        null, null, true, false, false, 0,
                        FuncaoMinistro.LEITURA, null)
        ));

        mockMvc.perform(get("/api/ministros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Pedro"));
    }

    @Test
    void obter_QuandoExiste_DeveRetornar200() throws Exception {
        when(service.obter(1L)).thenReturn(
                new MinistroResponse(1L, "Pedro", "pedro@igreja.org", null,
                        null, null, true, false, false, 0,
                        FuncaoMinistro.LEITURA, null)
        );

        mockMvc.perform(get("/api/ministros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Pedro"));
    }

    @Test
    void obter_QuandoNaoExiste_DeveRetornar404() throws Exception {
        when(service.obter(99L)).thenThrow(new ResourceNotFoundException("Ministro", 99L));

        mockMvc.perform(get("/api/ministros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_ComDadosValidos_DeveRetornar201() throws Exception {
        when(service.criar(any())).thenReturn(
                new MinistroResponse(1L, "Pedro", "pedro@igreja.org", null,
                        null, null, true, false, false, 0,
                        FuncaoMinistro.LEITURA, null)
        );

        String json = """
                {
                    "nome": "Pedro",
                    "email": "pedro@igreja.org",
                    "funcao": "LEITURA",
                    "ativo": true
                }
                """;

        mockMvc.perform(post("/api/ministros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Pedro"));
    }

    @Test
    void criar_ComNomeVazio_DeveRetornar400() throws Exception {
        String json = """
                {
                    "nome": "",
                    "email": "pedro@igreja.org",
                    "funcao": "LEITURA"
                }
                """;

        mockMvc.perform(post("/api/ministros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletar_QuandoExiste_DeveRetornar204() throws Exception {
        doNothing().when(service).deletar(1L);

        mockMvc.perform(delete("/api/ministros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_QuandoNaoExiste_DeveRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Ministro", 99L)).when(service).deletar(99L);

        mockMvc.perform(delete("/api/ministros/99"))
                .andExpect(status().isNotFound());
    }
}
