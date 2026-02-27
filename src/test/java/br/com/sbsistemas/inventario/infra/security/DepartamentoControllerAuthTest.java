package br.com.sbsistemas.inventario.infra.security;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sbsistemas.inventario.domain.departamento.Departamento;
import br.com.sbsistemas.inventario.domain.departamento.DepartamentoController;
import br.com.sbsistemas.inventario.domain.departamento.DepartamentoService;
import br.com.sbsistemas.inventario.infra.config.AppProperties;

/**
 * Testes de autorização para DepartamentoController.
 *
 * Regras: - GET, POST, PUT → ADMIN ou GESTOR - DELETE → somente ADMIN - USUARIO
 * → sem acesso a nenhum endpoint - Não autenticado → 401
 */
@WebMvcTest(DepartamentoController.class)
@Import({ SecurityConfig.class, JwtAuthFilter.class, AppProperties.class })
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost")
class DepartamentoControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartamentoService departamentoService;

    @MockitoBean
    private JwtProvider jwtProvider;

    private static final String BASE_URL = "/api/departamentos";
    private static final String JSON_BODY = """
            {"nome": "Financeiro"}
            """;

    private Departamento fakeDepartamento() {
        return Departamento.builder().nome("Financeiro").ativo(true).build();
    }

    // ========================================================================
    // Sem autenticação → 401
    // ========================================================================
    @Test
    void semAutenticacao_listar_deveRetornar401() throws Exception {
        mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacao_buscar_deveRetornar401() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacao_criar_deveRetornar401() throws Exception {
        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(JSON_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacao_atualizar_deveRetornar401() throws Exception {
        mockMvc.perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacao_desativar_deveRetornar401() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1")).andExpect(status().isUnauthorized());
    }

    // ========================================================================
    // Role ADMIN → acesso total
    // ========================================================================
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_listar_deveRetornar200() throws Exception {
        when(departamentoService.listarAtivos()).thenReturn(List.of());
        mockMvc.perform(get(BASE_URL).with(user("admin").roles("ADMIN"))).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_buscar_deveRetornar200() throws Exception {
        when(departamentoService.buscarPorId(1L)).thenReturn(fakeDepartamento());
        mockMvc.perform(get(BASE_URL + "/1").with(user("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_criar_deveRetornar201() throws Exception {
        when(departamentoService.criar(anyString())).thenReturn(fakeDepartamento());
        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
                .with(user("admin").roles("ADMIN"))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_atualizar_deveRetornar200() throws Exception {
        when(departamentoService.atualizar(anyLong(), anyString())).thenReturn(fakeDepartamento());
        mockMvc.perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
                .with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_desativar_deveRetornar204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1").with(user("admin").roles("ADMIN"))).andExpect(status().isNoContent());
    }

    // ========================================================================
    // Role GESTOR → acesso a GET, POST, PUT; sem acesso a DELETE
    // ========================================================================
    @Test
    @WithMockUser(roles = "GESTOR")
    void gestor_listar_deveRetornar200() throws Exception {
        when(departamentoService.listarAtivos()).thenReturn(List.of());
        mockMvc.perform(get(BASE_URL).with(user("admin").roles("GESTOR"))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void gestor_buscar_deveRetornar200() throws Exception {
        when(departamentoService.buscarPorId(1L)).thenReturn(fakeDepartamento());
        mockMvc.perform(get(BASE_URL + "/1").with(user("admin").roles("GESTOR"))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void gestor_criar_deveRetornar201() throws Exception {
        when(departamentoService.criar(anyString())).thenReturn(fakeDepartamento());
        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
                .with(user("admin").roles("GESTOR"))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void gestor_atualizar_deveRetornar200() throws Exception {
        when(departamentoService.atualizar(anyLong(), anyString())).thenReturn(fakeDepartamento());
        mockMvc.perform(put(BASE_URL + "/1").contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
                .with(user("admin").roles("GESTOR"))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void gestor_desativar_deveRetornar403() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1").with(user("admin").roles("GESTOR"))).andExpect(status().isForbidden());
    }

    // ========================================================================
    // Role USUARIO → sem acesso a nenhum endpoint
    // ========================================================================
    @Test
    @WithMockUser(roles = "USUARIO")
    void usuario_listar_deveRetornar403() throws Exception {
        mockMvc.perform(get(BASE_URL).with(user("admin").roles("USUARIO"))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuario_buscar_deveRetornar403() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1").with(user("admin").roles("USUARIO"))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuario_criar_deveRetornar403() throws Exception {
        mockMvc.perform(post(BASE_URL).with(user("admin").roles("USUARIO"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuario_atualizar_deveRetornar403() throws Exception {
        mockMvc.perform(put(BASE_URL + "/1").with(user("admin").roles("USUARIO"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuario_desativar_deveRetornar403() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1").with(user("admin").roles("USUARIO"))).andExpect(status().isForbidden());
    }
}
