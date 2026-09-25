package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.domain.model.payment.BulkError;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.validation.AmountValidator;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.resources.BulkErrorResponse;
import com.progressoft.quickpay.payments.resources.BulkUploadResultResponse;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "senderAccount", qualifiedByName = "trim")
    @Mapping(target = "receiverAccount", qualifiedByName = "trim")
    @Mapping(target = "currency", qualifiedByName = "trim")
    @Mapping(target = "notes", qualifiedByName = "trim")
    @Mapping(target = "creditorName", qualifiedByName = "trim")
    @Mapping(target = "amount", qualifiedByName = "amount")
    PaymentEntity toRequest(PaymentRequest request);

    PaymentEntity toEntity(Payment payment);

    Payment toDomain(PaymentEntity entity);

    PaymentResponse toResponse(Payment payment);

    BulkUploadResultResponse toResponse(BulkUploadResult result);

    BulkErrorResponse toResponse(BulkError error);

    @Named("trim")
    default String trim(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    @Named("amount")
    default BigDecimal amount(BigDecimal value) {
        if (value == null || value.stripTrailingZeros().scale() > AmountValidator.MAX_SCALE) {
            return value;
        }
        return value.setScale(AmountValidator.MAX_SCALE);
    }
}
