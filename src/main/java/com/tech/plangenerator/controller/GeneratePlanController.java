package com.tech.plangenerator.controller;

import com.tech.plangenerator.model.LoanDetails;
import com.tech.plangenerator.model.Payment;
import com.tech.plangenerator.service.CalculatePaymentsPlanService;
import com.tech.plangenerator.util.Constants;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GeneratePlanController {

    @Autowired
    private
    CalculatePaymentsPlanService calculatePaymentService;

    /**
     * Endpoint which receives the arguments to generate the payment plan
     * @param loanDetails
     * @return JSON of each installment
     */
    @PostMapping("/generate-plan")
    public List<Payment> generatePlan(@Valid @RequestBody final LoanDetails loanDetails){

        final List<Payment> payments = new ArrayList<>();
        BigDecimal initialOutstandingPrincipal = loanDetails.getLoanAmount();
        final BigDecimal nominalRate = loanDetails.getNominalRate();
        final Integer duration = loanDetails.getDuration();

        BigDecimal annuity = calculatePaymentService.calculateAnnuity(initialOutstandingPrincipal, nominalRate, duration);

        for(long i=0; i < loanDetails.getDuration(); i++){

            Payment newPayment = new Payment();
            newPayment.setBorrowerPaymentAmount(annuity.toString());
            newPayment.setDate(loanDetails.getStartDate().plusMonths(i).toString());
            newPayment.setInitialOutstandingPrincipal(initialOutstandingPrincipal.toString());

            BigDecimal interest;
            interest = calculatePaymentService.calculateInterest(initialOutstandingPrincipal, nominalRate);
            newPayment.setInterest(interest.toString());

            BigDecimal principal = calculatePaymentService.calculatePrincipal(annuity, interest);
            newPayment.setPrincipal(principal.toString());
            BigDecimal remainingOutstandingPrincipal;
            remainingOutstandingPrincipal = calculatePaymentService.calculateRemainingOutstandingPrincipal(initialOutstandingPrincipal, principal);

            //if last iteration and remainingOutstandingPrincipal is less than 0
            if(i == loanDetails.getDuration()-1 && remainingOutstandingPrincipal.compareTo(Constants.ZERO) < NumberUtils.INTEGER_ZERO){
                annuity = initialOutstandingPrincipal.add(interest).round(MathContext.DECIMAL32).setScale(NumberUtils.INTEGER_TWO);
                principal = calculatePaymentService.calculatePrincipal(annuity, interest);
                remainingOutstandingPrincipal = calculatePaymentService.calculateRemainingOutstandingPrincipal(initialOutstandingPrincipal, principal);
                newPayment.setPrincipal(principal.toString());
                newPayment.setBorrowerPaymentAmount(annuity.toString());
            }

            newPayment.setRemainingOutstandingPrincipal(remainingOutstandingPrincipal.toString());
            payments.add(newPayment);
            initialOutstandingPrincipal = remainingOutstandingPrincipal;
        }
        return payments;
    }
}
