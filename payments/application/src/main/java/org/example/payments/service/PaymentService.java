package org.example.payments.service;

import com.progressoft.training.fileparser.usecase.ParseFileUseCase;
import org.example.repository.PaymentRepository;
import org.example.useCases.CreatePaymentUseCase;
import org.example.useCases.UploadBulkCommand;
import org.example.useCases.UploadBulkUseCse;
import org.springframework.stereotype.Service;
import org.example.model.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final PaymentRepository repository;
    private final UploadBulkUseCse uploadBulkUseCase;


    public PaymentService(CreatePaymentUseCase createPaymentUseCase, PaymentRepository repository, UploadBulkUseCse uploadBulkUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.repository = repository;
        this.uploadBulkUseCase = uploadBulkUseCase;
    }

    public Payment save(Payment payment) {
        return createPaymentUseCase.execute(payment);
    }

    public Optional<Payment> findBy(Long id) {
        return Optional.ofNullable(repository.findBy(id));
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public BulkResult uploadBulk(String fileName, Path tempFile) {
//        UploadBulkCommand command = new UploadBulkCommand(tempFile, fileName);
        ParseFileUseCase.ParseFileCommand command = new ParseFileUseCase.ParseFileCommand(fileName, tempFile);
        return uploadBulkUseCase.execute(command);

    }
}
