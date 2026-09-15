package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentEntity toRequest(PaymentRequest request);

    PaymentResponse toResponse(PaymentEntity response);
}
