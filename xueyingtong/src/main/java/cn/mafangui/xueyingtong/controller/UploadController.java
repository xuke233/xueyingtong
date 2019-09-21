package cn.mafangui.xueyingtong.controller;


import cn.mafangui.xueyingtong.entity.CircFile;
import cn.mafangui.xueyingtong.service.FileService;
import cn.mafangui.xueyingtong.tools.TrueTableUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


@Controller
@Component
public class UploadController {

    @Resource
    FileService fileService;

    @PostMapping("/upload/{id}")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
								   @PathVariable  int id,HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()||!file.getOriginalFilename().endsWith(".circ")) {
            redirectAttributes.addFlashAttribute("message", "请选择一个正确的文件上传");
            return "redirect:/experiment/"+id;
        }
        File cp = null;
        try {
            cp = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(!cp.exists()) {
            cp = new File("");
        }
        String str = "upload/"+new Date().getTime();
        File upload = new File(cp.getAbsolutePath(),"static/"+str);
        if(!upload.exists()) {
            upload.mkdirs();
        }
            // Get the file and save it somewhere
        byte[] bytes = new byte[0];
		Path path = null;
        try {
            bytes = file.getBytes();
            path = Paths.get(upload.getAbsolutePath()+"/"+file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File csf = new File(upload.getAbsolutePath()+"/"+file.getOriginalFilename());
        int score = 0;
        CircFile f = new CircFile();
        if (csf.exists()){
            try {
                File table = ResourceUtils.getFile("classpath:static/truth/1.txt");
                score = TrueTableUtil.getTrueTable(csf,table.getPath());
            }catch (Error | Exception e){
                System.out.println(e.getMessage());
                redirectAttributes.addFlashAttribute("message", "上传失败");
                return "redirect:/experiment/"+id;
            }
        }
        if (score<0){
            redirectAttributes.addFlashAttribute("message", "上传失败");
            return "redirect:/experiment/"+id;
        }
        f.setExpId(id);
        f.setFilename(file.getOriginalFilename());
        f.setScore(score);
        f.setPath(str+"/"+file.getOriginalFilename());
        f.setTime(new Date());
        f.setEmail((String) session.getAttribute("email"));
        fileService.save(f);
        redirectAttributes.addFlashAttribute("score",f.getScore());
        redirectAttributes.addFlashAttribute("name",f.getFilename());
        redirectAttributes.addFlashAttribute("path",f.getPath());
        redirectAttributes.addFlashAttribute("message",
                "成功上传文件:");

		return "redirect:/experiment/"+id;
    }

}
