package org.example.payments.config;


import com.progressoft.training.fileparser.parser.FileParserFactory;
import com.progressoft.training.fileparser.repository.*;
import com.progressoft.training.fileparser.usecase.*;
import org.example.model.Payment;
import org.example.payments.mapper.TemplateMapper;
import org.example.payments.repository.TemplateReaderImpl;
import org.example.payments.repository.TemplateRepositoryImpl;
import org.example.repository.PaymentRepository;
import org.example.useCases.CreatePaymentUseCase;
import org.example.useCases.UploadBulkUseCse;
import org.example.validation.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(
            @Qualifier("paymentValidatorChain") ValidatorChain<Payment> validatorChain
            , PaymentRepository repository) {

        return new CreatePaymentUseCase(validatorChain, repository);
    }

    @Bean
    public JsonTemplateRepository jsonTemplateRepository() throws IOException {
        Path file = Files.createTempFile("default-template", ".json");

        try (InputStream resourceAsStream = getClass().getClassLoader()
                .getResourceAsStream("template/default-template.json")) {

            Files.copy(resourceAsStream, file, StandardCopyOption.REPLACE_EXISTING);
        }
        return new JsonTemplateRepository(file);
    }

    @Bean
    FileParserFactory fileParserFactory() throws IOException {
        return new FileParserFactory();
    }

    @Bean
    public ParseFileUseCase<Payment> parseFileUseCase(TemplateReaderImpl templateReader, FileParserFactory fileParser) {
        return new ParseFileUseCase<>(templateReader, fileParser, parsedRow -> {
            Payment payment = new Payment();
            payment.setAmount(new BigDecimal(parsedRow.get("amount")));
            payment.setCurrency(parsedRow.get("currency"));
            payment.setNotes(parsedRow.get("notes"));
            payment.setSenderAccount(parsedRow.get("senderAccount"));
            payment.setReceiverAccount(parsedRow.get("receiverAccount"));
            payment.setCreditorName(parsedRow.get("creditorName"));

            return payment;
        });
    }

    @Bean
    public UploadBulkUseCse uploadBulkUseCse(ParseFileUseCase<Payment> parseFileUseCase,
                                             @Qualifier("paymentValidatorChain")
                                             ValidatorChain<Payment> validatorChain,
                                             PaymentRepository repository,
                                             @Value("${payments.uploud-bulk.save:}") Boolean saveIfError
    ) {

        return new UploadBulkUseCse(parseFileUseCase, validatorChain, repository, saveIfError);
    }

    @Bean
    public CreateTemplateUseCase createTemplateUseCase(TemplateRepositoryImpl repository) {
        return new CreateTemplateUseCase(repository);
    }

    @Bean
    public GetTemplateUseCase getTemplateUseCase(TemplateRepositoryImpl repository) {
        return new GetTemplateUseCase(repository);
    }

    @Bean
    public DeleteTemplateUseCase deleteTemplateUseCase(TemplateRepositoryImpl repository) {
        return new DeleteTemplateUseCase(repository);
    }

    @Bean
    public UpdateTemplateUseCase updateTemplateUseCase(TemplateRepositoryImpl repository) {
        return new UpdateTemplateUseCase(repository);
    }

}
