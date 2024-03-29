package org.example.spring.exception;

/**
 * -03/29-0:32
 * -
 */
public class NoSuchBeanException extends RuntimeException {

    public NoSuchBeanException() {
        this("No such bean found.");
    }

    public NoSuchBeanException(String message) {
        super(message);
    }

}
