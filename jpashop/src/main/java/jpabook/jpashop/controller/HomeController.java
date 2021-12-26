package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    //로그 찍어보자.
    //Logger log = LoggerFactory.getLogger(getClass());
    //-> 위 코드를 @Slf4j로 해줄 수 있다. (lombok)

    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home";
    }
}
