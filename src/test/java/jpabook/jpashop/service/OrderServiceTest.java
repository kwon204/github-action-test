package jpabook.jpashop.service;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jpabook.jpashop.Exception.NotEnoughStockException;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @PersistenceContext
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;
        //When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //Then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");

        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");

        assertEquals(10000 * 2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");

        assertEquals(10 - 2, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 12;
        // When
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // Then
        fail("상품주문 재고수량 조과");
    }

    @Test
    public void 주문취소() {
        // Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 1;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // When
        orderService.cancelOrder(orderId);
        // Then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소 시 상태는 CANCEL이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 재고가 증가해야 한다.");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(quantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }
}