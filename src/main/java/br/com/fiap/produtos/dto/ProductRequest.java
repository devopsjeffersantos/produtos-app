package br.com.fiap.produtos.dto;

import java.math.BigDecimal;

public record ProductRequest(Long id, String name, String description, Integer quantity, BigDecimal price) {

}
