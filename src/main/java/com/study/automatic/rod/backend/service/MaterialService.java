package com.study.automatic.rod.backend.service;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//
@Service
public class MaterialService {
    private static final Logger LOGGER = Logger.getLogger(MaterialService.class.getName());
    private final MaterialRepository materialRepository;
//
    public MaterialService(MaterialRepository repository){
        this.materialRepository=repository;
    }

    public List<Material> findAll(){
        return materialRepository.findAll();
    }

    public List<Material> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return materialRepository.findAll();
        } else {
            return materialRepository.search(stringFilter);
        }
    }

    public long count(){
        return materialRepository.count();
    }

    public void delete(Material material){
        materialRepository.delete(material);
    }

    public void save(Material material){
        if (material == null) {
            LOGGER.log(Level.SEVERE,
                    "Material is null. Are you sure you have connected your form to the application?");
            return;
        }
        materialRepository.save(material);
    }

    @PostConstruct
    public void populateTestData(){
        if(materialRepository.count() == 0){
            materialRepository.saveAll(
                    Stream.of("Copper; 0.0000168; 770.95; 21.2",
                            "Brass; 0.0000193; 772.45; 21.1",
                            "Steel; 0.0000118; 771.00; 21.3")
                            .map(line->{
                                String[] split = line.split(";");
                                Material material = new Material();
                                material.setName(split[0].trim());
                                material.setAlpha(Double.parseDouble(split[1]) );
                                material.setLength_0(Double.parseDouble(split[2]) );
                                material.setTemp_0(Double.parseDouble(split[3]) );
                                return material;
                            }).collect(Collectors.toList())
            );//saveAll)
        }//if
    }
}
