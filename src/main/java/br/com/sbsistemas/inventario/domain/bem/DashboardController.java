package br.com.sbsistemas.inventario.domain.bem;

import br.com.sbsistemas.inventario.shared.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final BemRepository bemRepository;

    @GetMapping
    @Operation(summary = "Resumo patrimonial para o dashboard")
    public DashboardResponse resumo() {
        Long tenantId = TenantContext.get();

        Map<String, Long> porEstado = new HashMap<>();
        long total = 0;
        for (Object[] row : bemRepository.contarPorEstado(tenantId)) {
            String estado = ((EstadoBem) row[0]).name();
            Long qtd = (Long) row[1];
            porEstado.put(estado, qtd);
            total += qtd;
        }

        BigDecimal valorAquisicao = bemRepository.somarValorAquisicao(tenantId);

        return new DashboardResponse(total, valorAquisicao, porEstado);
    }

    public record DashboardResponse(long totalBens, BigDecimal valorTotalAquisicao, Map<String, Long> bensPorEstado) {
    }
}
