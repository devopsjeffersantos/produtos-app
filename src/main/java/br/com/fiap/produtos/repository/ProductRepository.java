package br.com.fiap.produtos.repository;

import br.com.fiap.produtos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
