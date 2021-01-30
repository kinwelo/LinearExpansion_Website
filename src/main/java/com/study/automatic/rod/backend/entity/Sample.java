package com.study.automatic.rod.backend.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Sample extends AbstractEntity {
    String description;

    @OneToMany(mappedBy = "sample", fetch = FetchType.EAGER)
    Set<Record> records = new HashSet<>();

    public Sample() {
    }

    public Sample(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
