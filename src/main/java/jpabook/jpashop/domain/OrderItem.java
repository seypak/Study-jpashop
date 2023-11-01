package jpabook.jpashop.domain;

import jpabook.jpashop.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = " order_id")
    private Order order;

    private int orderPrice; // 주문가격

    private int count; // 주문수량

    /**
     * 생성자를 protected 로 선언하는것까지 JPA 에서 지원해주고,
     * protected 로 선언하면 외부에서 new Order()로 객체를 조작할 수 없도록 제약을 걸어줄 수 있다.
     * 해당 생성자는 @NoArgsConstructor(access = AccessLevel.PROTECTED) 로 대체할 수 있다.
     */
//    protected Order() {
//    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /**
     * 주문상품 전체 가격 조회
     * @return
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
