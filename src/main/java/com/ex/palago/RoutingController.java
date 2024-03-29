package com.ex.palago;

import com.ex.palago.security.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RoutingController {

    @RequestMapping("/home")
    public String mainPage(){
        return "/index";
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용
    // 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
   @GetMapping("/user")
   public String user(Authentication authentication) {
       PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
       System.out.println("principal : " + principal.getMember().getId());
       System.out.println("principal : " + principal.getMember().getUsername());
       System.out.println("principal : " + principal.getMember().getPassword());

       return "<h1>user</h1>";
   }


    @GetMapping("/sign")
    public String loginPage() {
        return "/login";
    }

    @GetMapping("/join")
    public String joinPage() {
        return "/join";
    }

    @GetMapping("/api/v1/seller/home")
    public String sellerMainPage() {
        return "/seller/home";
    }

    @GetMapping("/api/v1/system/home")
    public String adminMainPage(){
        return "/admin/home";
    }
}
