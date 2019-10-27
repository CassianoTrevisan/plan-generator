package com.tech.plangenerator.service;

import java.math.BigDecimal;

public interface CalculatePaymentsPlanService {
    BigDecimal calculateInterest(BigDecimal initialOutstandingPrincipal, BigDecimal nominalRate);
    BigDecimal calculatePrincipal(BigDecimal annuity, BigDecimal interest);
    BigDecimal calculateRemainingOutstandingPrincipal(BigDecimal initialOutstandingPrincipal, BigDecimal principal);
    BigDecimal calculateAnnuity(BigDecimal pv, BigDecimal rate, Integer period);
}
