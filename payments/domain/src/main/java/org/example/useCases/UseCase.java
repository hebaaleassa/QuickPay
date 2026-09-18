package org.example.useCases;

public interface UseCase< O, I> {
    O execute(I input);
}
