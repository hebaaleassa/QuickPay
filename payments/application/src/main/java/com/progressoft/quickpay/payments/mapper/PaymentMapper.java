package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.resources.BulkUploadResultResponse;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentEntity toRequest(PaymentRequest request);

    PaymentEntity toEntity(Payment payment);

    Payment toDomain(PaymentEntity entity);

    PaymentResponse toResponse(Payment payment);

    BulkUploadResultResponse toResponse(BulkUploadResult result);
}
