package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hello!");
        return "hello"; //어떤 파일로 매핑되는지 설정값 바꾸고 싶으면, 스프링 메뉴얼로 가서 prefix와 suffix 메뉴얼대로 바꿀 수 있다.
    }
}
