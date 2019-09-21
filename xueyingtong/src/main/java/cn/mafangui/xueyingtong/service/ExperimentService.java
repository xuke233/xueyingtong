package cn.mafangui.xueyingtong.service;

import cn.mafangui.xueyingtong.dao.ExperimentRepository;
import cn.mafangui.xueyingtong.entity.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExperimentService {

    @Autowired
    private ExperimentRepository experimentRepository;

    public List<Experiment> getAllExperiment(){
        return experimentRepository.findAll();
    }

    public Experiment getById(Integer id){
        Optional<Experiment> experiment = experimentRepository.findById(id);
        return experiment.isPresent()?experiment.get():null;
    }


}
