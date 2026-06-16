package com.tuhinK.eCommerce.cartandcheckout.repositories;

import com.tuhinK.eCommerce.cartandcheckout.models.Cart;
import com.tuhinK.eCommerce.cartandcheckout.models.CartItem;
import com.tuhinK.eCommerce.product.models.Category;
import com.tuhinK.eCommerce.product.models.Product;
import com.tuhinK.eCommerce.product.repositories.CategoryRepository;
import com.tuhinK.eCommerce.product.repositories.ProductRepository;
import com.tuhinK.eCommerce.user.models.User;
import com.tuhinK.eCommerce.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Cart savedCart;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User()
                .setFirstName("Alice")
                .setEmail("alice@test.com")
                .setPassword("pass");
        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        savedCart = cartRepository.save(cart);

        Category category = new Category();
        category.setName("Electronics");
        Category savedCategory = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Phone");
        product.setBrand("Brand");
        product.setPrice(new BigDecimal("500"));
        product.setInventory(10);
        product.setDescription("Desc");
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);

        CartItem item1 = new CartItem();
        item1.setCart(savedCart);
        item1.setProduct(savedProduct);
        item1.setQuantity(1);
        item1.setUnitPrice(new BigDecimal("500"));
        item1.setTotalPrice();

        CartItem item2 = new CartItem();
        item2.setCart(savedCart);
        item2.setProduct(savedProduct);
        item2.setQuantity(2);
        item2.setUnitPrice(new BigDecimal("500"));
        item2.setTotalPrice();

        cartItemRepository.saveAll(List.of(item1, item2));
    }

    @Test
    @DisplayName("should delete all cart items by cart ID")
    void deleteAllByCartId() {
        long countBefore = cartItemRepository.count();
        assertThat(countBefore).isEqualTo(2);

        cartItemRepository.deleteAllByCartId(savedCart.getId());

        long countAfter = cartItemRepository.count();
        assertThat(countAfter).isEqualTo(0);
    }
}
