package br.com.sbsistemas.inventario.shared.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope de resposta paginada padronizado para a API.
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast());
    }
}
