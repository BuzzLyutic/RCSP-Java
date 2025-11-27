package com.example.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("missions")
public class Mission {

    @Id
    private Long id;

    private String name;

    private String destination; // Mars, Moon, Jupiter, Europa...

    @Column("launch_year")
    private Integer launchYear;

    private String status; // PLANNED, LAUNCHED, COMPLETED, FAILED

    @Column("crew_size")
    private Integer crewSize;
}
