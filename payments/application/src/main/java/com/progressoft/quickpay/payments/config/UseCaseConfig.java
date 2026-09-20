package com.progressoft.quickpay.payments.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Path;

@Configuration
public class UseCaseConfig {
    @Bean
    public CreatePaymentUseCase createPaymentUseCase(PaymentRepository paymentRepository, @Qualifier("createPaymentValidators") ValidatorChain<Payment> validatorChain) {
        return new CreatePaymentUseCase(paymentRepository, validatorChain);
    }

    @Bean
    public ParseFileUseCase<Payment> parseFileUseCase(FileParserFactory fileParserFactory,
                                                      TemplateReaderImpl templateReader) {
        return new ParseFileUseCase<>(templateReader, fileParserFactory, new PaymentRowMapper());
    }

    @Bean
    public UploadBulkPaymentUseCase uploadBulkPaymentUseCase(ParseFileUseCase<Payment> parseFileUseCase, CreatePaymentUseCase createPaymentUseCase) {
        return new UploadBulkPaymentUseCase(parseFileUseCase, createPaymentUseCase);
    }

    @Bean
    public JsonTemplateRepository jsonTemplateRepository() throws Exception {
        URL sourcefile = getClass().getClassLoader().getResource("templates/default-template.json");
        return new JsonTemplateRepository(Path.of(sourcefile.toURI()));
    }

    @Bean
    public FileParserFactory fileParserFactory() {
        return new FileParserFactory();
    }

    @Bean
    public GetTemplateUseCase getTemplateUseCase(TemplateRepositoryImpl templates) {
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
}
