package com.tech.plangenerator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

@Service
public class CalculatePaymentsPlanServiceImpl implements CalculatePaymentsPlanService {

    private static final BigDecimal DAYS_IN_A_MONTH = new BigDecimal(30);
    private static final BigDecimal DAYS_IN_A_YEAR = new BigDecimal(360);
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @Override
    public BigDecimal calculateInterest(final BigDecimal initialOutstandingPrincipal, final BigDecimal nominalRate) {
        BigDecimal interest = nominalRate;
        interest = interest.multiply(DAYS_IN_A_MONTH);
        interest = interest.multiply(initialOutstandingPrincipal);
        interest = interest.divide(DAYS_IN_A_YEAR, 2, RoundingMode.HALF_DOWN);

        //convert from cents to dollars
        interest = interest.divide(ONE_HUNDRED, 2, RoundingMode.HALF_DOWN);
        return interest;
    }

    @Override
    public BigDecimal calculatePrincipal(final BigDecimal annuity, final BigDecimal interest) {
        return annuity.subtract(interest).round(MathContext.DECIMAL32);
    }

    @Override
    public BigDecimal calculateRemainingOutstandingPrincipal(final BigDecimal initialOutstandingPrincipal, final BigDecimal principal) {
        return initialOutstandingPrincipal.subtract(principal).round(MathContext.DECIMAL32).setScale(2);
    }

    @Override
    public BigDecimal calculateAnnuity(final BigDecimal pv, final BigDecimal rate, final Integer period) throws InvalidParameterException{

        validateAnnuityArguments(pv, rate, period);

        //find monthly rate in decimal
        BigDecimal monthlyInterestRate = rate.divide(new BigDecimal(12), 6, RoundingMode.HALF_EVEN)
                .divide(new BigDecimal(100), 6, RoundingMode.HALF_EVEN);

        BigDecimal pvTimesRate = pv.multiply(monthlyInterestRate);
        //first sum 1 with monthlyInterestRate = (1 + r)
        BigDecimal divisor = monthlyInterestRate.add(new BigDecimal(1));
        //power of the period
        divisor = divisor.pow(period, MathContext.DECIMAL64);
        //to take care of the negative exponentiation with divide 1 per divisor ( 1/ divisor)
        divisor = new BigDecimal(1).divide(divisor, 6, RoundingMode.HALF_EVEN);
        //now subtract divisor from 1 (1 - divisor)
        divisor  = new BigDecimal(1).subtract(divisor);

        return pvTimesRate.divide(divisor, 2, RoundingMode.CEILING);
    }

    private void validateAnnuityArguments(final BigDecimal pv, final BigDecimal rate, final Integer period){
        if(pv.compareTo(new BigDecimal(0)) <= 0){
            throw new InvalidParameterException();
        }
        if(rate.compareTo(new BigDecimal(0)) <= 0){
            throw new InvalidParameterException();
        }
        if(period < 1){
            throw new InvalidParameterException();
        }
    }
}
