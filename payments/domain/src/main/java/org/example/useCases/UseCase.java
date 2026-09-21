package org.example.useCases;

public interface UseCase<I, O> {
    O execute(I input);
}
