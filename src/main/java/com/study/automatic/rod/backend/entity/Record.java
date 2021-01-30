package com.study.automatic.rod.backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Record extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "sample_id")
    private Sample sample;

    double temp;

    private Record() {
    }

    public Record(Sample sample) {
        this.sample = sample;
    }

    public Record(Sample sample, double temp) {
        this.sample = sample;
        this.temp = temp;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
