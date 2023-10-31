package jpabook.jpashop.domain.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.repository.OrderRepository;
import jpabook.jpashop.domain.repository.OrderSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        //given
        Member member = getMember();

        Book book = getBook("JPA북", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, order.getStatus());
        assertEquals("주문한 상품 수 체크", 1, order.getOrderItems().size());
        assertEquals("주문 가격은 상품가격 * 재고수량", book.getPrice() * orderCount, order.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 8, book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = getMember();
        Book book = getBook("JPA북", 10000, 10);

        int orderCount = 11;

        //when
        orderService.order(member.getId(), book.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }
    
    @Test
    public void 주문취소() throws Exception {

        //given
        Member member = getMember();
        Book book = getBook("JPA북", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);

        assertEquals("주문취소시 주문상태는 CANCEL", OrderStatus.CANCEL, order.getStatus());
        assertEquals("주문취소시 재고상태는 원복", 10, book.getStockQuantity());
    }

    @Test
    public void 주문검색조회() throws Exception {
        //given
        Member member = getMember();
        Book book = getBook("JPA북", 10000, 10);
        int orderCount = 2;
        orderService.order(member.getId(), book.getId(), orderCount);

        OrderSearch orderSearch = new OrderSearch();
        orderSearch.setOrderStatus(OrderStatus.ORDER);
        orderSearch.setMemberName("회원1");

        //when
        List<Order> orders = orderService.findOrders(orderSearch);

        //then
        assertFalse("주문 조회 정상확인", CollectionUtils.isEmpty(orders));
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "송파", "123-123"));
        em.persist(member);
        return member;
    }

}