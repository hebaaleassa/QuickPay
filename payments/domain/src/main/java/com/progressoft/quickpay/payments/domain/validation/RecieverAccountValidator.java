package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.List;

public class RecieverAccountValidator implements Validator<Payment> {
    @Override
    public List<Violation> validate(Payment payment) {
        String receiverAccount = payment.getReceiverAccount();
        String senderAccount = payment.getSenderAccount();
        if (receiverAccount == null || receiverAccount.isBlank()) {
            return List.of(new Violation("Reciever account must not be null or blank",
                    "RecieverAccountValidator"));
        }

        if (receiverAccount.trim().length() > 34) {
            return List.of(new Violation(
                    "reciever Account must not be more than 34 chars",
                    "RecieverAcoountValidator"
            ));
        }
        if ((senderAccount != null && !senderAccount.isBlank()) &&
                receiverAccount.trim().equalsIgnoreCase(senderAccount.trim())) {
            return List.of(new Violation("reciever acount must differ from sender account"
                    , "RecieverAccountValidator"));
        }
        return List.of();
    }
}
