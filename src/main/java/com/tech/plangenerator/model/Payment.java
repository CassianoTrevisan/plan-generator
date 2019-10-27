package com.tech.plangenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
    private String borrowerPaymentAmount;
    private String date;
    private String initialOutstandingPrincipal;
    private String interest;
    private String principal;
    private String remainingOutstandingPrincipal;
}
