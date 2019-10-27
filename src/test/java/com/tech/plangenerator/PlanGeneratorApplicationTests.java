package com.tech.plangenerator;

import com.tech.plangenerator.service.CalculatePaymentsPlanService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PlanGeneratorApplicationTests {

	@Autowired
	private CalculatePaymentsPlanService calculatePaymentsPlanService;

	@Test
	void calculateInterestFirstInstallment(){
		BigDecimal interest = calculatePaymentsPlanService.calculateInterest(new BigDecimal(5000), new BigDecimal(5));
		BigDecimal expected = new BigDecimal(20.83).round(MathContext.DECIMAL32).setScale(2);
		assertEquals(expected, interest);
	}

	@Test
	void calculateInterestSecondInstallment(){
		BigDecimal interest = calculatePaymentsPlanService.calculateInterest(new BigDecimal(4801.47), new BigDecimal(5));
		BigDecimal expected = new BigDecimal(20.01).round(MathContext.DECIMAL32).setScale(2);
		assertEquals(expected, interest);
	}

	@Test
	void calculateInterestLastInstallment(){
		BigDecimal interest = calculatePaymentsPlanService.calculateInterest(new BigDecimal(218.37), new BigDecimal(5));
		BigDecimal expected = new BigDecimal(0.91).round(MathContext.DECIMAL32).setScale(2);
		assertEquals(expected, interest);
	}

	@Test
	void calculatePrincipalFirstInstallment(){
		BigDecimal principal = calculatePaymentsPlanService.calculatePrincipal(new BigDecimal(219.36), new BigDecimal(20.83));
		principal = principal.round(MathContext.DECIMAL32).setScale(2);
		BigDecimal expected = new BigDecimal(198.53).round(MathContext.DECIMAL32).setScale(2);
		assertEquals(expected, principal);
	}

	@Test
	void calculatePrincipalSecondInstallment(){
		BigDecimal principal = calculatePaymentsPlanService.calculatePrincipal(new BigDecimal(219.36), new BigDecimal(20.01));
		principal = principal.round(MathContext.DECIMAL32).setScale(2);
		BigDecimal expected = new BigDecimal(199.35).round(MathContext.DECIMAL32).setScale(2);
		assertEquals(expected, principal);
	}

	@Test
	void calculateAnnuityOne(){
		BigDecimal pv = new BigDecimal(5000);
		BigDecimal rate = new BigDecimal(5);
		Integer period = new Integer(24);

		BigDecimal expectedAnnuity = new BigDecimal(219.36).setScale( 2, RoundingMode.HALF_EVEN);

		BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);

		assertEquals(expectedAnnuity, annuity);
	}

	@Test
	void calculateAnnuityTwo(){
		BigDecimal pv = new BigDecimal(7500);
		BigDecimal rate = new BigDecimal(9);
		Integer period = new Integer(24);

		BigDecimal expectedAnnuity = new BigDecimal(342.64).setScale( 2, RoundingMode.HALF_EVEN);

		BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);

		assertEquals(expectedAnnuity, annuity);
	}

	@Test
	void calculateAnnuityWithDecimal(){
		BigDecimal pv = new BigDecimal(7500.60);
		BigDecimal rate = new BigDecimal(9.89);
		Integer period = new Integer(36);

		BigDecimal expectedAnnuity = new BigDecimal(241.64).setScale( 2, RoundingMode.HALF_EVEN);

		BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);

		assertEquals(expectedAnnuity, annuity);
	}

	@Test
	void calculateAnnuityWith(){
		BigDecimal pv = new BigDecimal(7500.60);
		BigDecimal rate = new BigDecimal(9.89);
		Integer period = new Integer(36);

		BigDecimal expectedAnnuity = new BigDecimal(241.64).setScale( 2, RoundingMode.HALF_EVEN);
		BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);
		assertEquals(expectedAnnuity, annuity);
	}

	@Test
	void calculateAnnuityWithPVZeroOrNegative(){
		final BigDecimal pv = new BigDecimal(0);
		final BigDecimal rate = new BigDecimal(2);
		final Integer period = new Integer(12);

		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);
		});

		final BigDecimal pvNegative = new BigDecimal(-4);
		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pvNegative, rate, period);
		});
	}

	@Test
	void calculateAnnuityWithRateZeroOrNegative(){
		final BigDecimal pv = new BigDecimal(1000);
		final BigDecimal rate = new BigDecimal(0);
		final Integer period = new Integer(12);

		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);
		});

		final BigDecimal rateNegative = new BigDecimal(-3);
		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rateNegative, period);
		});
	}

	@Test
	void calculateAnnuityWithPeriodZeroOrNegative(){
		final BigDecimal pv = new BigDecimal(1000);
		final BigDecimal rate = new BigDecimal(2);
		final Integer period = new Integer(0);

		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, period);
		});

		final Integer periodNegative = new Integer(-3);
		assertThrows(InvalidParameterException.class, () -> {
			BigDecimal annuity = calculatePaymentsPlanService.calculateAnnuity(pv, rate, periodNegative);
		});
	}
}
