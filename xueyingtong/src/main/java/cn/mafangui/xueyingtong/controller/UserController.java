package cn.mafangui.xueyingtong.controller;


import cn.mafangui.xueyingtong.dao.UserRepository;
import cn.mafangui.xueyingtong.entity.User;
import cn.mafangui.xueyingtong.service.UserService;
import cn.mafangui.xueyingtong.tools.MD5Utils;
import org.attoparser.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.TextUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = "/login")
    public String login(RedirectAttributes attributes,HttpSession session,
                     @RequestParam("email") String email,
                     @RequestParam("password") String password){
        User user = userService.userLogin(email,MD5Utils.MD5Encode(password,"utf8"));
        if (user!=null){
            attributes.addFlashAttribute("email",email);
            session.setAttribute("email",email);
            return "redirect:/";
        }
        attributes.addFlashAttribute("message","登录失败");
        return "redirect:/login";
    }


    @RequestMapping(path = "/register")
    public String register(RedirectAttributes redirectAttributes,HttpSession session,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("password2") String password2){
        if (!password.equals(password2)){
            redirectAttributes.addFlashAttribute("message","输入的两次密码不一致！");
            return "redirect:/signup";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(MD5Utils.MD5Encode(password,"utf8"));
        user.setType(0);
        User user1 =userRepository.save(user);
        if (user1==null){
            redirectAttributes.addFlashAttribute("message","注册失败！");
            return "redirect:/signup";
        }
        session.setAttribute("email",email);
        redirectAttributes.addFlashAttribute("email",email);
        return "redirect:/";
    }


}
