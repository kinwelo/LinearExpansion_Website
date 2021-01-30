package com.study.automatic.rod.backend.entity;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Material extends AbstractEntity {
    @NotEmpty
    String name;

    @NotNull
    double alpha; //linear expansion coefficient

    @NotNull
    @Min(-273)
    @Max(500)
    double temp_0;

    @NotNull
    @Min(0)
    double length_0;

    public Material() {
    }

    public Material(@NotNull String name, @NotNull double alpha, @NotNull double temp_0, @NotNull double length_0) {
        this.name = name;
        this.alpha = alpha;
        this.temp_0 = temp_0;
        this.length_0 = length_0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getTemp_0() {
        return temp_0;
    }

    public void setTemp_0(double temp_0) {
        this.temp_0 = temp_0;
    }

    public double getLength_0() {
        return length_0;
    }

    public void setLength_0(double length_0) {
        this.length_0 = length_0;
    }
}
