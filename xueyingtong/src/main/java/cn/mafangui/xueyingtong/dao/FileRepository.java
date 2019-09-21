package cn.mafangui.xueyingtong.dao;

import cn.mafangui.xueyingtong.entity.CircFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<CircFile,Integer> {


    List<CircFile> findByEmailAndAndExpId(String email,Integer expId);
}
