package com.study.automatic.rod.backend.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
public class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date saveDate;

    public Long getId() {
        return id;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    private void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;
        AbstractEntity that = (AbstractEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @PrePersist
    public void beforePersist() { setSaveDate (new Date(System.currentTimeMillis()));}
}
