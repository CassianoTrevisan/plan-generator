package com.tech.plangenerator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanDetails {
    @NotNull(message = "Please provide a loan amount")
    @DecimalMin("1.00")
    private BigDecimal loanAmount;
    @NotNull(message = "Please provide a nominal rate")
    @DecimalMin("0.01")
    private BigDecimal nominalRate;
    @NotNull(message = "Please provide a duration")
    private Integer duration;
    @NotNull(message = "Please provide a start date")
    private LocalDateTime startDate;
}
