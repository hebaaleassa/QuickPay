package org.example.validation;

import org.example.model.Violation;

import java.util.List;

public interface Validator<T>{
    public List<Violation> validate(T t);
}
