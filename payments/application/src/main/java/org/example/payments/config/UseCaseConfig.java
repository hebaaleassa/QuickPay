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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(
            @Qualifier("paymentValidatorChain") ValidatorChain<Payment> validatorChain
            , PaymentRepository repository) {

        return new CreatePaymentUseCase(validatorChain, repository);
    }

    @Bean
    public JsonTemplateRepository jsonTemplateRepository() throws IOException, URISyntaxException {
        ClassPathResource classPathResource = new ClassPathResource("template/default-template.json");
            URL url = classPathResource.getURL();
            return new JsonTemplateRepository(Path.of(url.toURI()));

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
                                             @Qualifier("paymentValidatorChain") ValidatorChain<Payment> validatorChain,
                                             PaymentRepository repository
    ) {

        return new UploadBulkUseCse(parseFileUseCase, validatorChain, repository);
    }

    @Bean
    public CreateTemplateUseCase createTemplateUseCase(@Qualifier("databaseTemplateRepository")
                                                       TemplateRepositoryImpl repository) {
        return new CreateTemplateUseCase(repository);
    }

    @Bean
    public GetTemplateUseCase getTemplateUseCase(
            @Qualifier("databaseTemplateRepository") TemplateRepositoryImpl repository) {
        return new GetTemplateUseCase(repository);
    }

    @Bean
    public ListTemplatesUseCase listTemplatesUseCase(
            @Qualifier("databaseTemplateRepository") TemplateRepositoryImpl repository) {
        return new ListTemplatesUseCase(repository);
    }

    @Bean
    public DeleteTemplateUseCase deleteTemplateUseCase(
            @Qualifier("databaseTemplateRepository") TemplateRepositoryImpl repository) {
        return new DeleteTemplateUseCase(repository);
    }

    @Bean
    public UpdateTemplateUseCase updateTemplateUseCase(TemplateRepositoryImpl repository) {
        return new UpdateTemplateUseCase(repository);
    }

}
