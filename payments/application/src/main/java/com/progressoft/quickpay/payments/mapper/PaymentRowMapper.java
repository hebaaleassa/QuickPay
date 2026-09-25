package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.domain.model.payment.ParsedPayment;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.training.fileparser.domain.ParsedRow;
import com.progressoft.training.fileparser.mapper.RowMapper;

import java.math.BigDecimal;

public class PaymentRowMapper implements RowMapper<ParsedPayment> {

    private final PaymentMapper paymentMapper;

    public PaymentRowMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Override
    public ParsedPayment map(ParsedRow row) {
        Payment payment = new Payment();
        payment.setSenderAccount(paymentMapper.trim(row.get("senderAccount")));
        payment.setReceiverAccount(paymentMapper.trim(row.get("receiverAccount")));
        payment.setAmount(paymentMapper.amount(new BigDecimal(row.get("amount"))));
        payment.setCurrency(paymentMapper.trim(row.get("currency")));
        payment.setNotes(paymentMapper.trim(row.get("notes")));
        payment.setCreditorName(paymentMapper.trim(row.get("creditorName")));
        return new ParsedPayment(row.rowNumber(), payment);
    }
}
