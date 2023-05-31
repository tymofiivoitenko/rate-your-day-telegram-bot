package com.tymofiivoitenko.rateyourdaybot.model.rate;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;


@Entity
@Table(name = "rate_settings")
@Data
public class RateSettings {

    @Id
    @SequenceGenerator(name = "rate_settings_id_seq", sequenceName = "rate_settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_settings_id_seq")
    private Integer id;

    @Column(name = "person_id")
    @NotNull
    private Integer personId;

    @Column(name = "ask_time")
    @NotNull
    private LocalTime askTime;
}
