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
                    Stream.of("Copper; 0.00017; 770.95; 20.0",
                            "Brass; 0.00019; 772.45; 20.0",
                            "Aluminium;0.000231;0;20.0",
                            "Magnesium;0.00026;0;20.0",
                            "Gold;0.00014;0;20.0",
                            "Iron;0.000118;0;20.0",
                            "Diamond;0.00001;0;20.0",
                            "Lead;0.00020;0;20.0",
                            "Platinum;0.00009;0;20.0",
                            "Steel; 0.00013; 771.00; 20.0")
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
