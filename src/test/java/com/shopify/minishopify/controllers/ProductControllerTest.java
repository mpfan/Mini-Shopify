package com.shopify.minishopify.controllers;

import com.shopify.minishopify.model.Product;
import com.shopify.minishopify.model.Shop;
import com.shopify.minishopify.repository.CategoryRepository;
import com.shopify.minishopify.repository.ProductRepository;
import com.shopify.minishopify.repository.ShopRepository;
import com.shopify.minishopify.repository.TagRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private TagRepository tagRepository;

    // Test objects
    private Product product;
    private Shop shop;
    private JSONObject productJsonBody;

    @BeforeAll
    public void initialize() throws JSONException {
        shop = new Shop();
        product = new Product("product", "description", 1.0f, 1, "data:image/png;base64,image");
        product.setShop(shop);

        productJsonBody = new JSONObject();
        productJsonBody.put("name", product.getName());
        productJsonBody.put("description", product.getDescription());
        productJsonBody.put("quantity", product.getQuantity());
        productJsonBody.put("price", product.getPrice());
        productJsonBody.put("image", product.getImage());
    }

    @Test
    public void whenFindExistingProductById_thenReturnProduct() throws Exception {

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        mvc.perform(get("/api/products/" + product.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void whenFindNonExistingProductById_thenReturnNotFound() throws Exception {

        mvc.perform(get("/api/products/2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenFindProductsByIds_thenReturnProducts() throws Exception {

        when(productRepository.findAllById(any())).thenReturn(new ArrayList<Product>());

        mvc.perform(get("/api/products?ids=1,2"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateExistingProduct_thenReturnProduct() throws Exception {

        // mock repository behaviour
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        mvc.perform(post("/api/products/update/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJsonBody.toString()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdateNonExistingProduct_thenReturnNotFound() throws Exception {

        mvc.perform(post("/api/products/update/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJsonBody.toString()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenCreateNewProduct_thenReturnProduct() throws Exception {

        when(productRepository.save(any())).thenReturn(product);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        mvc.perform(post("/api/shop/" + shop.getId() + "/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJsonBody.toString()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    public void whenDeleteNewProduct_thenRemoveFromShopAndDelete() throws Exception {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        mvc.perform(delete("/api/products/delete/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()));
        assertFalse(shop.getProducts().contains(product));
        assertNotEquals(shop, product.getShop());
        verify(productRepository).deleteById(product.getId());
    }
}
