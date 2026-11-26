package com.example.hrm_be.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "leave_balances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_leave_balance_employee_year", columnNames = {"employee_id", "year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer year;

    @Builder.Default
    @Column(name = "total_days", nullable = false)
    private Double totalDays = 12.0;

    @Builder.Default
    @Column(name = "used_days", nullable = false)
    private Double usedDays = 0.0;

    @Version
    private Long version;
}
