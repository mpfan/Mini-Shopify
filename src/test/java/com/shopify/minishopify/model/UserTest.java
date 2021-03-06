package com.shopify.minishopify.model;

import com.shopify.minishopify.repository.ShopRepository;
import com.shopify.minishopify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;

    @BeforeEach
    public void prepareDB() {
        shopRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testPersistence() {
        User testUser = new User("TEST_USER", "JUNIT", "Password");
        userRepository.save(testUser);

        List<User> dbUsers = userRepository.findAll();
        assertEquals(dbUsers.size(), 1);
        User readUser = dbUsers.get(0);
        assertEquals("TEST_USER", readUser.getName());
        assertEquals("JUNIT", readUser.getEmail());
        assertEquals("Password", readUser.getPassword());

        userRepository.delete(readUser);
        dbUsers = userRepository.findAll();
        assertEquals(dbUsers.size(), 0);
    }
}
