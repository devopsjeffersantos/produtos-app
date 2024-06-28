package br.com.fiap.produtos.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, Integer quantity, BigDecimal price) {

}
