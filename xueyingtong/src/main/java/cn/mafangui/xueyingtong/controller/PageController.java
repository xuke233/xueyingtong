package cn.mafangui.xueyingtong.controller;

import cn.mafangui.xueyingtong.entity.CircFile;
import cn.mafangui.xueyingtong.entity.Experiment;
import cn.mafangui.xueyingtong.service.ExperimentService;
import cn.mafangui.xueyingtong.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    ExperimentService experimentService;
    @Autowired
    FileService fileService;

    @RequestMapping("/")
    public String index(HttpSession session,Model model){
        String email = (String) session.getAttribute("email");
        model.addAttribute("email",email);
        model.addAttribute("exp_list",experimentService.getAllExperiment());
        return "index";
    }

    @RequestMapping(path = "/login")
    public String toLogin(HttpSession session, RedirectAttributes redirectAttributes){
        String email = (String) session.getAttribute("email");
        if (!StringUtils.isEmpty(email)){
            redirectAttributes.addFlashAttribute("email",email);
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(path = "/signup")
    public String toSignUp(Model model){
        model.addAttribute("message","");
        return "register";
    }

    @RequestMapping(path = "/wires")
    public String toWires(){
        return "wires";
    }


    @RequestMapping(path = "/experiment/{id}")
    public String toExp(HttpSession session,RedirectAttributes redirectAttributes,Model model,@PathVariable Integer id){
        String email = (String) session.getAttribute("email");
        if (StringUtils.isEmpty(email)) {
            redirectAttributes.addFlashAttribute("message","请先登录！");
            return "redirect:/login";
        }
        Experiment exp = experimentService.getById(id);
        List<CircFile> files = fileService.getByUserAndExp(email,id);
        model.addAttribute("email",email);
        model.addAttribute("exp",exp);
        model.addAttribute("files",files);
        return "experiment";
    }
}
