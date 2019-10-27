package com.tech.plangenerator.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

import static com.tech.plangenerator.util.Constants.*;

@Service
public class CalculatePaymentsPlanServiceImpl implements CalculatePaymentsPlanService {

    /**
     * Calculates based on the formula
     * Interest = (Nominal-Rate * Days in Month * Initial OutstandingPrincipal) / days in year
     * @param initialOutstandingPrincipal
     * @param nominalRate
     * @return
     */
    @Override
    public BigDecimal calculateInterest(final BigDecimal initialOutstandingPrincipal, final BigDecimal nominalRate) {
        BigDecimal interest = nominalRate;
        interest = interest.multiply(DAYS_IN_A_MONTH);
        interest = interest.multiply(initialOutstandingPrincipal);
        interest = interest.divide(DAYS_IN_A_YEAR, NumberUtils.INTEGER_TWO, RoundingMode.HALF_DOWN);

        //convert from cents to dollars
        interest = interest.divide(ONE_HUNDRED, NumberUtils.INTEGER_TWO, RoundingMode.HALF_DOWN);
        return interest;
    }

    /**
     * To calculate principal subtract the interest from annuity (final value, not rate)
     * @param annuity
     * @param interest
     * @return
     */
    @Override
    public BigDecimal calculatePrincipal(final BigDecimal annuity, final BigDecimal interest) {
        return annuity.subtract(interest).round(MathContext.DECIMAL32);
    }

    /**
     * To calculate the Remaining Outstanding Principal subtract the principal from initial outstanding principal
     * @param initialOutstandingPrincipal
     * @param principal
     * @return
     */
    @Override
    public BigDecimal calculateRemainingOutstandingPrincipal(final BigDecimal initialOutstandingPrincipal, final BigDecimal principal) {
        return initialOutstandingPrincipal.subtract(principal).round(MathContext.DECIMAL32).setScale(NumberUtils.INTEGER_TWO);
    }

    /**
     * The formula for this calculation can be defined as:
     *  PV * rate% / 1 - [1 + rate%] ^ period_in_months
     * I based the precision of this calculation on the test
     * description and in the calculator below.
     * https://edfinancial.com/TOOLS/Loan-Repayment-Calculator
     * @param pv - present value
     * @param rate - rate of interest per cent
     * @param period - number of instalments (in months in this case)
     * @return BigDecimal that represents the annuity value
     * @throws InvalidParameterException
     */
    @Override
    public BigDecimal calculateAnnuity(final BigDecimal pv, final BigDecimal rate, final Integer period) throws InvalidParameterException{

        validateAnnuityArguments(pv, rate, period);

        //find monthly rate in decimal
        BigDecimal monthlyInterestRate = rate.divide(TWELVE, INT_SIX, RoundingMode.HALF_EVEN)
                .divide(ONE_HUNDRED, INT_SIX, RoundingMode.HALF_EVEN);

        BigDecimal pvTimesRate = pv.multiply(monthlyInterestRate);
        //first sum 1 with monthlyInterestRate = (1 + r)
        BigDecimal divisor = monthlyInterestRate.add(BigDecimal.ONE);
        //power of the period
        divisor = divisor.pow(period, MathContext.DECIMAL64);
        //to take care of the negative exponentiation with divide 1 per divisor ( 1/ divisor)
        divisor = BigDecimal.ONE.divide(divisor, INT_SIX, RoundingMode.HALF_EVEN);
        //now subtract divisor from 1 (1 - divisor)
        divisor  = BigDecimal.ONE.subtract(divisor);

        return pvTimesRate.divide(divisor, NumberUtils.INTEGER_TWO, RoundingMode.CEILING);
    }

    /**
     * If a argument is invalid a InvalidParameterException is thrown
     * @param pv
     * @param rate
     * @param period
     */
    private void validateAnnuityArguments(final BigDecimal pv, final BigDecimal rate, final Integer period){
        if(pv.compareTo(BigDecimal.ZERO) <= NumberUtils.INTEGER_ZERO){
            throw new InvalidParameterException();
        }
        if(rate.compareTo(BigDecimal.ZERO) <= NumberUtils.INTEGER_ZERO){
            throw new InvalidParameterException();
        }
        if(period < NumberUtils.INTEGER_ONE){
            throw new InvalidParameterException();
        }
    }
}
