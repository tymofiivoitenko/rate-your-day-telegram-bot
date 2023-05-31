package com.tymofiivoitenko.rateyourdaybot.model.rate;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "rate")
@Data
public class Rate {

    @Id
    @SequenceGenerator(name = "rate_id_seq", sequenceName = "rate_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_id_seq")
    private Integer id;

    @Column(name = "person_id")
    @NotNull
    private Integer personId;

    @Column(name = "date")
    @NotNull
    private LocalDate date;

    @Column(name = "score")
    private Integer score;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
