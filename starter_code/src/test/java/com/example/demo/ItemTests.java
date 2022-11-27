package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class ItemTests {

    @Autowired
    ItemController itemController;

    @Test
    public void getItems() {
        List<Item> items = itemController.getItems().getBody();
        assertThat(items.size()).isGreaterThan(0);
        Item i = items.get(0);
        assertThat(i.getId()).isNotNull();
        assertThat(i.getName()).isNotNull();
        assertThat(i.getDescription()).isNotNull();
        assertThat(i.getPrice()).isNotNull();
    }

    @Test
    public void getItemById(){
        Item i = itemController.getItemById(1L).getBody();
        assertThat(i.getId()).isNotNull();
        assertThat(i.getName()).isNotNull();
        assertThat(i.getDescription()).isNotNull();
        assertThat(i.getPrice()).isNotNull();
    }

    @Test
    public void getItemByName(){
        List<Item> items = itemController.getItemsByName("Round Widget").getBody();
        assertThat(items.size()).isGreaterThan(0);
        Item i = items.get(0);
        assertThat(i.getId()).isNotNull();
        assertThat(i.getName()).isNotNull();
        assertThat(i.getDescription()).isNotNull();
        assertThat(i.getPrice()).isNotNull();
    }
}
