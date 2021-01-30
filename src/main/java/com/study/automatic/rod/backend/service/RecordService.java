package com.study.automatic.rod.backend.service;

import com.study.automatic.rod.backend.entity.Record;
import com.study.automatic.rod.backend.entity.Sample;
import com.study.automatic.rod.backend.repository.RecordRepository;
import com.study.automatic.rod.backend.repository.SampleRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RecordService {
    private static final Logger LOGGER = Logger.getLogger(RecordService.class.getName());
    private RecordRepository recordRepository;
    private SampleRepository sampleRepository;

    public RecordService(RecordRepository recordRepository, SampleRepository sampleRepository){
        this.recordRepository=recordRepository;
        this.sampleRepository=sampleRepository;
    }

    public List<Record> findAll(){
        return recordRepository.findAll();
    }

    public List<Record> findAll(Long longFilter) {
        if (longFilter == null || longFilter<0) {
            return recordRepository.findAll();
        } else {
            //return recordRepository.search(longFilter);
            return recordRepository.findAll();
        }
    }

    public long count(){
        return recordRepository.count();
    }

    public void delete(Record record){
        recordRepository.delete(record);
    }

    public void save(Record record) {
        if (record == null) {
            LOGGER.log(Level.SEVERE,
                    "Record is null. Are you sure you have connected your form to the application?");
            return;
        }
        recordRepository.save(record);
    }

    public Map<Date, Double> getChartData(Long sampleId){
        HashMap<Date, Double> data = new HashMap<>();
        findAll(sampleId).forEach(record -> data.put(record.getSaveDate(), record.getTemp()));
        return data;
    }
}
