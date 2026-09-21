package org.example.payments.mapper;

import org.example.model.BulkResult;
import org.example.model.Payment;
import org.example.payments.model.PaymentEntity;
import org.example.payments.resource.BulkUploadResponse;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

     Payment toDomain(PaymentRequest request);
     PaymentResponse toResponse(Payment payment);

     PaymentEntity toEntity(Payment payment);
     Payment toDomain(PaymentEntity entity);

     BulkUploadResponse toResponse(BulkResult bulkResult);

}
