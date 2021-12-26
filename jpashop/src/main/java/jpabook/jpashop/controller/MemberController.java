package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm()); //화면에서, 이 새로 생성한 MemberForm객체에 접근할 수 있다.
        //컨트롤러에서 뷰로 넘어갈 때, 이 데이터를 넘긴다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm){
        //@Valid : MemberForm에 있는 javax validation을 쓴다는 것을 인지하게 한다. -> @NotEmpty등 다양한 어노테이션을 쓸 수 있다.
        //validation을 쓸 수 있다.
        //만일 여
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }

}
