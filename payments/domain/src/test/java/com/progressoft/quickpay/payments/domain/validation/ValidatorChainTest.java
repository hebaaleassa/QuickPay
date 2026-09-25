package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

// Techniques: equivalence partitioning on the chain (empty / all pass / several fail / duplicate violations)
class ValidatorChainTest {

    @Test
    void givenNoValidators_whenValidate_thenNoViolations() {
        ValidatorChain<Payment> chain = new ValidatorChain<>(List.of());
        Assertions.assertTrue(chain.validate(PaymentTestData.validPayment()).getViolations().isEmpty());
    }

    @Test
    void givenValidPayment_whenValidate_thenNoViolations() {
        ValidatorChain<Payment> chain = new ValidatorChain<>(List.of(new SenderAccountValidator(), new AmountValidator()));
        Assertions.assertTrue(chain.validate(PaymentTestData.validPayment()).getViolations().isEmpty());
    }

    @Test
    void givenSeveralInvalidFields_whenValidate_thenViolationsFromEveryValidatorInOrder() {
        ValidatorChain<Payment> chain = new ValidatorChain<>(List.of(new SenderAccountValidator(), new AmountValidator()));
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount(null);
        payment.setAmount(null);

        List<String> violators = chain.validate(payment).getViolations().stream().map(Violation::violator).toList();

        Assertions.assertEquals(List.of("senderAccount", "amount"), violators);
    }

    @Test
    void givenDuplicateViolations_whenValidate_thenReportedOnce() {
        Validator<Payment> alwaysFails = payment -> List.of(new Violation("bad", "field"));
        ValidatorChain<Payment> chain = new ValidatorChain<>(List.of(alwaysFails, alwaysFails));

        Assertions.assertEquals(1, chain.validate(PaymentTestData.validPayment()).getViolations().size());
    }
}
