package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class IntegrationTests {

    @Autowired
    OrderController orderController;
    @Autowired
    ItemController itemController;
    @Autowired
    CartController cartController;
    @Autowired
    UserController userController;

    @Test
    public void test_add_remove_item_submit_order_view_history(){
        //create user
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("a");
        createUserRequest.setPassword("a");
        User user = userController.createUser(createUserRequest).getBody();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCart()).isNotNull();

        user = userController.findByUserName("a").getBody();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCart()).isNotNull();

        user = userController.findById(1L).getBody();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCart()).isNotNull();

        //get Item
        List<Item> items = itemController.getItems().getBody();
        assertThat(items.size()).isGreaterThan(0);
        Item item = items.get(0);
        assertThat(item.getId()).isGreaterThan(0);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setUsername(user.getUsername());
        cartRequest.setQuantity(2);
        Cart cart = cartController.addTocart(cartRequest).getBody();
        assertThat(cart.getItems().size()).isEqualTo(2);
        assertThat(cart.getItems().get(0)).isEqualTo(item);
        assertThat(cart.getUser().getId()).isEqualTo(user.getId());
        assertThat(cart.getTotal()).isEqualTo(item.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity())));

        cartRequest.setQuantity(1);
        cart = cartController.removeFromcart(cartRequest).getBody();
        assertThat(cart.getItems().size()).isEqualTo(1);

        UserOrder userOrder = orderController.submit(user.getUsername()).getBody();
        assertThat(userOrder).isNotNull();
        assertThat(userOrder.getTotal()).isEqualTo(cart.getTotal());
        assertThat(userOrder.getUser().getId()).isEqualTo(user.getId());

        List<UserOrder> userOrderList = orderController.getOrdersForUser(user.getUsername()).getBody();
        assertThat(userOrderList.size()).isEqualTo(1);
        assertThat(userOrderList.get(0).getId()).isEqualTo(userOrder.getId());
    }
}
