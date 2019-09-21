package cn.mafangui.xueyingtong.service;

import cn.mafangui.xueyingtong.dao.UserRepository;
import cn.mafangui.xueyingtong.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User userLogin(String email,String password){
        return userRepository.findByEmailAndPassword(email,password);
    }

}
