package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    /***
     * 인젝션 주입받는 방법
     * 1. 필드 @Autowired : 테스트할 때 바꿀 수 없는 단점이 있다 ( Mock 객체를 사용할 수 없기 때문에 )
     * 2. setter @Autowired: runtime 시점에 변경될 수 있는 단점이 있다.
     * 3. 생성자 @Autowired: new 생성자를 사용하면 세팅 조립이 끝나버려 안전하다.
     * 4. final 변수로 생성: @autowired 어노테이션이 없어도 자동으로 인젝션 주입을 해준다.
     * 5. (권장) 롬복의 @RequiredArgsConstructor: final이 있는 필드만 가지고 생성자를 만들어준다.
     */
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        // 같은 이름의 회원이 안된다고 중복회원 검증 로직 추가
        validateDuplicateMember(member);

        memberRepository.save(member);

        return member.getId();
    }

    /**
     * 중복회원 검증
     * @param member
     */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 단건 조회
     * @param memberId
     * @return
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}
