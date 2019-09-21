package cn.mafangui.xueyingtong.dao;

import cn.mafangui.xueyingtong.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperimentRepository extends JpaRepository<Experiment,Integer> {
}
