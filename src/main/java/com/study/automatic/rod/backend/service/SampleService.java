package com.study.automatic.rod.backend.service;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Sample;
import com.study.automatic.rod.backend.repository.MaterialRepository;
import com.study.automatic.rod.backend.repository.SampleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SampleService {
    private static final Logger LOGGER = Logger.getLogger(SampleService.class.getName());
    private SampleRepository sampleRepository;

    public SampleService(SampleRepository sampleRepository){
        this.sampleRepository=sampleRepository;
    }

    public List<Sample> findAll(){
        return sampleRepository.findAll();
    }

    public List<Sample> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return sampleRepository.findAll();
        } else {
            return sampleRepository.search(stringFilter);
        }
    }

    public long count(){
        return sampleRepository.count();
    }

    public void delete(Sample sample){
        sampleRepository.delete(sample);
    }

    public void save(Sample sample) {
        if (sample == null) {
            LOGGER.log(Level.SEVERE,
                    "Sample is null. Are you sure you have connected your form to the application?");
            return;
        }
        sampleRepository.save(sample);
    }
}
