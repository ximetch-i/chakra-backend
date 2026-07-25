package com.agrialert.entity;

import com.agrialert.enums.RiskLevel;
import com.agrialert.enums.WeatherSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String crop;

    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_source", nullable = false, length = 20)
    private WeatherSource weatherSource;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double precipitation;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}