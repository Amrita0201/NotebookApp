package com.tarento.notebook.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateTagInNotesException extends DuplicateKeyException {

    public DuplicateTagInNotesException(String msg) {
        super(msg);
    }

    public DuplicateTagInNotesException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
