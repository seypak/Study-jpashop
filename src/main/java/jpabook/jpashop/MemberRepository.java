package jpabook.jpashop;

import jpabook.jpashop.domaiin.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 커맨드랑 쿼리를 분리해라 원칙에 의해서
     * 저장을 하고 나면 가급적이면 사이드 이펙트를 일으키는 커맨드성이기때문에 unique한 pk값을 뽑아오도록 설정하였음
     * @param member
     * @return
     */
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
