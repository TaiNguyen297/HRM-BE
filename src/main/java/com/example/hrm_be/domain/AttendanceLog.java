package com.example.hrm_be.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance_logs",
        indexes = {
                @Index(name = "idx_attendance_employee_check_time", columnList = "employee_id, check_time")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType type;

    private String note;
}
