package jpabook.jpashop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long orderId) {
        return em.find(Order.class, orderId);
    }

    //==동적쿼리 방법 3가지==//
    /**
     * 1. 문자열로 JPQL을 만드는 방법
     * => JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다
     * @param orderSearch
     * @return
     */
    @Deprecated
    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        // 1) 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        // 2) 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건

        // 1-1) 주문상태에 따른 동적 파라미터 바인딩
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        // 2-1) 회원이름에 따른 동적 파라미터 바인딩
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * 2. JPA Criteria ( 치명적 단점: 유지보수 어려움 )
     * => JPA Criteria 는 JPA 표준 스펙이지만 실무에서 사용하기에 너무 복잡하다. 결국 다른 대안이 필요하다.
     * 많은 개발자가 비슷한 고민을 했지만, 가장 멋진 해결책은 Querydsl 이 제시했다.
     */
    @Deprecated
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<Predicate>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    /**
     * 3. JPA QueryDSL
     * => 강좌에서는 JPQL, Criteria 두가지만 설명하고 querydsl은 별도로 강의한다고 하여
     * 개인적으로 gradle 설정과 querydsl을 사용해보았다.
     * 좀 더 추가 리팩토링 해야할 것으로 보인다.
     * ( Q객체 생성원리, JPAQueryFactory를 인스턴스화할 수 있는방법, 파라미터 별도 Exception처리 등 )
     */
    public List<Order> findAllByQueryDsl(OrderSearch orderSearch) {
        JPAQueryFactory jpf = new JPAQueryFactory(em);
        QOrder qOrder = QOrder.order;
        QMember qMember = QMember.member;

        return jpf.query()
                .select(qOrder)
                .from(qOrder)
                .join(qOrder.member, qMember)
                .where(qOrder.status.eq(orderSearch.getOrderStatus())
                        , qMember.name.like(orderSearch.getMemberName())
                ).limit(1000)
                .fetch();
    }

}
