package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "dailyOpsLoadConfig")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class DailyOpsLoadConfig {
    @Id
    private String id;
    private String customer;
    private Boolean isDPVACalculated;
    private Boolean isPerformanceMapCalculated = true;

    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
