package com.shopify.minishopify.controllers;

import com.shopify.minishopify.model.Product;
import com.shopify.minishopify.model.Shop;
import com.shopify.minishopify.repository.ProductRepository;
import com.shopify.minishopify.repository.ShopRepository;
import com.shopify.minishopify.util.ImageValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @GetMapping("/shop/{id}/products")
    public Iterable<Product> getAllProducts(@PathVariable int id) {
        return productRepository.findByShopId(id);
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable int id) {
        Optional<Product> optional = productRepository.findById(id);

        if(optional.isPresent()) {
            return optional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No product found with id: %d", id));
        }
    }

    @GetMapping("/products")
    public Iterable<Product> getProductsByIds(@RequestParam List<Integer> ids) {

        return productRepository.findAllById(ids);
    }

    @PostMapping("/shop/{id}/products/create")
    public Product createProduct(@PathVariable int id, @Valid @RequestBody Product product) {
        Shop shop = null;
        Optional<Shop> optional = shopRepository.findById(id);

        if(optional.isPresent()) {
            shop = optional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No shop found with id: %d", id));
        }

        if(!ImageValidator.isValidImage(product.getImage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid image"));
        }

        product.setShop(shop);

        return productRepository.save(product);
    }

    @PostMapping("/products/update/{id}")
    public Product updateProductById(@PathVariable int id, @Valid @RequestBody Product updatedProduct) {
        Optional<Product> optional = productRepository.findById(id);

        if(optional.isPresent()) {
            Product product = optional.get();

            if(!ImageValidator.isValidImage(updatedProduct.getImage())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid image"));
            }

            product.setImage(updatedProduct.getImage());
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setQuantity(updatedProduct.getQuantity());

            return productRepository.save(product);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No product found with id: %d", id));
        }
    }

    @DeleteMapping("/products/delete/{id}")
    public Product deleteProductById(@PathVariable int id) {
        Optional<Product> optional = productRepository.findById(id);

        if(optional.isPresent()) {
            Product product = optional.get();
            product.getShop().removeProduct(product);
            productRepository.deleteById(product.getId());

            return product;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No product found with id: %d", id));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
