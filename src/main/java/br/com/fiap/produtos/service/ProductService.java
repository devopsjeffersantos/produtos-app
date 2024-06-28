package br.com.fiap.produtos.service;

import br.com.fiap.produtos.dto.ProductRequest;
import br.com.fiap.produtos.dto.ProductResponse;
import br.com.fiap.produtos.model.Product;
import br.com.fiap.produtos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .quantity(productRequest.quantity())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Produto cadastrado!");
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getQuantity(), product.getPrice());
    }


    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getQuantity(), product.getPrice()))
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        var produto = productRepository.findById(id);
        if (produto.isPresent()) {
            return produto.stream()
                    .map(product -> new ProductResponse(produto.get().getId(), produto.get().getName(),  produto.get().getDescription(), product.getQuantity(), produto.get().getPrice()))
                    .findFirst().get();

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }

    }


    public String updateProduct(Long id, ProductRequest productRequest) {
        var produto = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (produto != null) {
            produto.setName(productRequest.name());
            produto.setDescription(productRequest.description());
            produto.setPrice(productRequest.price());
            productRepository.save(produto);
            log.info("Produto atualizado!");
            return "Produto atualizado!";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void deleteProduct(Long id) {
        var produto = productRepository.findById(id);
        if (produto.isPresent()) {
            productRepository.delete(produto.get());
            log.info("Produto removido!");
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto não existe na base de dados!");
        }
    }

    public void processCSV(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<Product> produtos = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                Product produto = new Product();
                produto.setName(parts[0]);
                produto.setDescription(parts[1]);
                produto.setQuantity(Integer.valueOf(parts[2]));
                BigDecimal price = null;
                try {
                    price = new BigDecimal(parts[3]);
                    produto.setPrice(price);
                } catch (NumberFormatException e) {
                    System.err.println("Valor incorreto: " + parts[3]);

                }
                produtos.add(produto);
                log.info("Produto cadastrado!");
            }
            productRepository.saveAll(produtos);
        }
    }


    public Optional<Product> atualizarEstoqueProduto(Long idProduto, Integer quantidadeVendida) {
        Optional<Product> optionalProduct = productRepository.findById(idProduto);

        if(optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setQuantity(product.getQuantity() - quantidadeVendida);

            return Optional.of(productRepository.save(product));
        } else {
            return Optional.empty();
        }
    }
}
