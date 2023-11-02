package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * @return orderId
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
//      OrderItem order = new OrderItem(); // 제약조건을 걸어 사용자가 별도의 생성자로 사용할 수 없도록 제어할 것.
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
//      Order order = new Order(); // 제약조건을 걸어 사용자가 별도의 생성자로 사용할 수 없도록 제어할 것.
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {

        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        /**
         * [김영한] - 추가설명
         * JPA를 활용하면 이렇게 엔티티 안에 있는 데이터들만 바꿔주면
         * JPA가 알아서 바뀐 변경 포인트들을 이제 Dirty Checking이라고 그러고 변경내역 감지라고 저는 번역을 하는데
         * 그 변경 내역 감지가 일어나면서 변경된 내역들을 찾아서 데이터베이스에 업데이트 쿼리가 쫙쫙 날라갑니다
         * 이게 JPA를 사용할 때 진짜 엄청 큰 장점이라고 볼 수 있습니다.
         */
        order.cancel();
    }

    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
