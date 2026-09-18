package org.example.useCases;

import org.example.exception.SystemViolationException;
import org.example.model.Payment;
import org.example.model.Violation;
import org.example.repository.PaymentRepository;
import org.example.validation.ValidationResult;
import org.example.validation.ValidatorChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {

    @Mock
    private ValidatorChain<Payment> validatorChain;

    @Mock
    private PaymentRepository repository;

    @Mock
    private Payment inputPayment;

    @InjectMocks
    private CreatePaymentUseCase createPaymentUseCase;

    @Test
    void givenInvalidPayment_whenExecute_throwsExceptionAndNeverSaves() {
        ValidationResult failureResult = new ValidationResult
                (Set.of(new Violation("amount", "Amount is invalid")));

        when(validatorChain.validate(inputPayment)).thenReturn(failureResult);
        assertThrows(SystemViolationException.class, () -> createPaymentUseCase.execute(inputPayment));
}

    @Test
    void givenValidPayment_whenExecute_setsStatusAndDateAndSaves() {
        ValidationResult successResult = new ValidationResult(Set.of());
        when(validatorChain.validate(inputPayment)).thenReturn(successResult);
        when(repository.save(inputPayment)).thenReturn(inputPayment);

        Payment result = createPaymentUseCase.execute(inputPayment);
        assertNotNull(result);
    }

}