package com.carrefour.infra.h2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "available_time_sloy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableTimeSlot {

    @Id
    @org.springframework.data.annotation.Id
    private Long id;
    private String startTime;
    private String endTime;
    private Integer numberOfReservation;
    private Integer numberMaxOfReservation;

}
