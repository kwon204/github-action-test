package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @Test
    @Rollback(value = false)
    public void itemAddAndUpdateTest() {
        // Given
        Item book = createBook("kim", 1000, 10);
        itemService.saveItem(book);
        Long bookId = book.getId();
        em.flush();
        em.clear();
//        em.detach(book);
        // When
        itemService.updateItem(bookId, "lee", 10000, 99);
        // Then
        Item findBook = itemService.findOne(bookId);

        assertEquals("lee", findBook.getName());
        assertEquals(10000, findBook.getPrice());
        assertEquals(99, findBook.getStockQuantity());
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(quantity);
        book.setPrice(price);
        return book;
    }
}