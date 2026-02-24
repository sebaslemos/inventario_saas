package br.com.sbsistemas.inventario.domain.categoria;

import br.com.sbsistemas.inventario.domain.tenant.Tenant;
import br.com.sbsistemas.inventario.domain.tenant.TenantRepository;
import br.com.sbsistemas.inventario.shared.TenantContext;
import br.com.sbsistemas.inventario.shared.exception.BusinessException;
import br.com.sbsistemas.inventario.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final TenantRepository tenantRepository;

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findAllByTenantIdAndAtivoTrue(TenantContext.get());
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new NotFoundException("Categoria", id));
    }

    @Transactional
    public Categoria criar(CategoriaRequest request) {
        Long tenantId = TenantContext.get();
        if (categoriaRepository.existsByNomeAndTenantId(request.nome(), tenantId)) {
            throw new BusinessException("Já existe uma categoria com este nome");
        }
        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        Categoria categoria = Categoria.builder()
                .tenant(tenant)
                .nome(request.nome().trim())
                .taxaAnual(request.taxaAnual())
                .vidaUtilAnos(request.vidaUtilAnos())
                .revisarEmAnos(request.revisarEmAnos())
                .ativo(true)
                .build();
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);
        // Verifica duplicidade somente se o nome mudou
        if (!categoria.getNome().equalsIgnoreCase(request.nome())
                && categoriaRepository.existsByNomeAndTenantId(request.nome(), TenantContext.get())) {
            throw new BusinessException("Já existe uma categoria com este nome");
        }
        categoria.setNome(request.nome().trim());
        categoria.setTaxaAnual(request.taxaAnual());
        categoria.setVidaUtilAnos(request.vidaUtilAnos());
        categoria.setRevisarEmAnos(request.revisarEmAnos());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void desativar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setAtivo(false);
    }
}
