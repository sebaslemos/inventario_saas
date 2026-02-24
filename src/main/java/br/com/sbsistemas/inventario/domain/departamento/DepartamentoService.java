package br.com.sbsistemas.inventario.domain.departamento;

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
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final TenantRepository tenantRepository;

    public List<Departamento> listarAtivos() {
        return departamentoRepository.findAllByTenantIdAndAtivoTrue(TenantContext.get());
    }

    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new NotFoundException("Departamento", id));
    }

    @Transactional
    public Departamento criar(String nome) {
        Long tenantId = TenantContext.get();
        if (departamentoRepository.existsByNomeAndTenantId(nome, tenantId)) {
            throw new BusinessException("Já existe um departamento com este nome");
        }
        var tenant = tenantRepository.getReferenceById(tenantId);
        return departamentoRepository.save(Departamento.builder().tenant(tenant).nome(nome.trim()).ativo(true).build());
    }

    @Transactional
    public Departamento atualizar(Long id, String novoNome) {
        Departamento dep = buscarPorId(id);
        if (!dep.getNome().equalsIgnoreCase(novoNome)
                && departamentoRepository.existsByNomeAndTenantId(novoNome, TenantContext.get())) {
            throw new BusinessException("Já existe um departamento com este nome");
        }
        dep.setNome(novoNome.trim());
        return departamentoRepository.save(dep);
    }

    @Transactional
    public void desativar(Long id) {
        buscarPorId(id).setAtivo(false);
    }
}
