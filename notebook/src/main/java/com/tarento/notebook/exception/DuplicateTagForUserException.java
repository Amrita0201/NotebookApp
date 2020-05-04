package com.tarento.notebook.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateTagForUserException extends DuplicateKeyException {
    public DuplicateTagForUserException(String msg) {
        super(msg);
    }

    public DuplicateTagForUserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
