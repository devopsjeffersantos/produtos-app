package br.com.fiap.produtos.controller;

import br.com.fiap.produtos.dto.ProductRequest;
import br.com.fiap.produtos.dto.ProductResponse;
import br.com.fiap.produtos.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

	private final ProductService productService;

	private static final Logger logger = Logger.getLogger(ProductController.class.getName());

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
		return productService.createProduct(productRequest);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_USER')")
	//@PreAuthorize("hasRole('TESTE')")
	public List<ProductResponse> getAllProducts() {
		return productService.getAllProducts();

	}

	
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_USER')")
    public ProductResponse getByIdProduct(@PathVariable("id") Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        logger.info("teste ricardo log: " + productResponse.toString());
        return productResponse;
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public String updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);

    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    @PostMapping(value="/carga", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo está vazio!");
        }
        try {
            productService.processCSV(file);
            return ResponseEntity.ok("Arquivo CSV recebido e processado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }

    @PostMapping("/{idProduto}/atualizar-estoque")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<String> atualizarEstoqueProduto(@PathVariable Long idProduto, @RequestParam Integer quantidadeVendida) {
        return productService.atualizarEstoqueProduto(idProduto, quantidadeVendida)
                .map(order -> ResponseEntity.ok("Estoque atualizado com Sucesso"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




}
