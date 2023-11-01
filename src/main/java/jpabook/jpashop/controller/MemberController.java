package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String memberForm(Model model) {
        model.addAttribute("form", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if(result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    /**
     * 요구사항이 정말 단순할 때는 멤버 폼 없이 멤버 엔티티를 그대로 써도 되지만,
     * 실무는 단순하지 않기때문에 화면 기능 때문에 엔티티가 결국 유지 보수하기가 어려워진다.
     * 그래서 엔티티는 핵심 비즈니스 로직만 가지고 있고
     * 화면을 위한 로직은 폼 객체나 DTO(Data Transfer Object) 을 사용해야한다.
     * 또한, API를 만들 때는 이유를 불문하고 절대 엔티티를 넘기면 안된다.
     * 보안적인 정보가 노출되는 문제도 있고 또 하나는 API 스펙이 변해버리는 문제가 있다.
     * => 아래 예제는 테스트 코드이므로 단순하게 엔티티를 그대로 반환하였다.
     */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
