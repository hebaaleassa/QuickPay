package org.example.payments.mapper;

import org.example.payments.model.PaymentEntity;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

     PaymentEntity toEntity(PaymentRequest request);

     PaymentResponse toResponse(PaymentEntity entity);
}
