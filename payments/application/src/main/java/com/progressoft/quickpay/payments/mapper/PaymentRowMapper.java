package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.training.fileparser.domain.ParsedRow;
import com.progressoft.training.fileparser.mapper.RowMapper;

import java.math.BigDecimal;

public class PaymentRowMapper implements RowMapper<Payment> {

    @Override
    public Payment map(ParsedRow row) {
        Payment payment = new Payment();
        payment.setSenderAccount(row.get("senderAccount"));
        payment.setReceiverAccount(row.get("receiverAccount"));
        payment.setAmount(new BigDecimal(row.get("amount")));
        payment.setCurrency(row.get("currency"));
        payment.setNotes(row.get("notes"));
        payment.setCreditorName(row.get("creditorName"));
        return payment;
    }
}