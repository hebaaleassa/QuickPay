package com.progressoft.quickpay.payments.config;

import com.progressoft.quickpay.payments.domain.model.payment.ParsedPayment;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import com.progressoft.quickpay.payments.domain.usecases.UploadBulkPaymentUseCase;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import com.progressoft.quickpay.payments.mapper.PaymentRowMapper;
import com.progressoft.quickpay.payments.repository.TemplateReaderImpl;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryImpl;
import com.progressoft.training.fileparser.parser.FileParserFactory;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import com.progressoft.training.fileparser.usecase.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Configuration
public class UseCaseConfig {
    @Bean
    public CreatePaymentUseCase createPaymentUseCase(PaymentRepository paymentRepository, @Qualifier("createPaymentValidators") ValidatorChain<Payment> validatorChain) {
        return new CreatePaymentUseCase(paymentRepository, validatorChain);
    }

    @Bean
    public ParseFileUseCase<ParsedPayment> parseFileUseCase(FileParserFactory fileParserFactory, TemplateReaderImpl templateReader) {
        return new ParseFileUseCase<>(templateReader, fileParserFactory, new PaymentRowMapper());
    }

    @Bean
    public UploadBulkPaymentUseCase uploadBulkPaymentUseCase(ParseFileUseCase<ParsedPayment> parseFileUseCase,
                                                             @Qualifier("createPaymentValidators")
                                                             ValidatorChain<Payment> validatorChain,
                                                             PaymentRepository paymentRepository,
                                                             @Value("${payments.persist-db-on-error:false}") boolean persistDBonError) {
        return new UploadBulkPaymentUseCase(parseFileUseCase, validatorChain, paymentRepository, persistDBonError);
    }

    @Bean
    public JsonTemplateRepository jsonTemplateRepository() throws Exception {
        Path file = Files.createTempFile("default-template", ".json");
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("templates/default-template.json")) {
            Files.copy(resourceAsStream, file, StandardCopyOption.REPLACE_EXISTING);
        }
        return new JsonTemplateRepository(file);
    }

    @Bean
    public FileParserFactory fileParserFactory() {
        return new FileParserFactory();
    }

    @Bean
    public GetTemplateUseCase getTemplateUseCase(TemplateReaderImpl templates) {
        return new GetTemplateUseCase(templates);
    }

    @Bean
    public ListTemplatesUseCase listTemplatesUseCase(TemplateRepositoryImpl templates) {
        return new ListTemplatesUseCase(templates);
    }

    @Bean
    public DeleteTemplateUseCase deleteTemplateUseCase(TemplateRepositoryImpl templates) {
        return new DeleteTemplateUseCase(templates);
    }

    @Bean
    public CreateTemplateUseCase createTemplateUseCase(TemplateRepositoryImpl templates) {
        return new CreateTemplateUseCase(templates);
    }

    @Bean
    public UpdateTemplateUseCase updateTemplateUseCase(TemplateRepositoryImpl templateRepository) {
        return new UpdateTemplateUseCase(templateRepository);
    }
}
