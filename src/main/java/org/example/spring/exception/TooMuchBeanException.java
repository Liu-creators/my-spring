package org.example.spring.exception;

/**
 * -03/29-0:32
 * -
 */
public class TooMuchBeanException extends RuntimeException {

    public TooMuchBeanException() {
        this("Too much bean found.");
    }

    public TooMuchBeanException(String message) {
        super(message);
    }

}