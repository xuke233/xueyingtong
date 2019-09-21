package cn.mafangui.xueyingtong.service;


import cn.mafangui.xueyingtong.dao.FileRepository;
import cn.mafangui.xueyingtong.entity.CircFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public List<CircFile> getFileList(){
        return fileRepository.findAll();
    }

    public void save(CircFile file){
        fileRepository.save(file);
    }

    public List<CircFile> getByUserAndExp(String email,Integer expId){
        return fileRepository.findByEmailAndAndExpId(email,expId);
    }

}
