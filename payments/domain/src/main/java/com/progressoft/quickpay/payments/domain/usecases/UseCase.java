package com.progressoft.quickpay.payments.domain.usecases;

public interface UseCase<T> {
    void execute(T t);
}
