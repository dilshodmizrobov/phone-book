package com.mizrobov.phonebook.exception;

import java.util.Arrays;

public class ApiError {
    private String codes;
    private String message;

    public ApiError(String codes, String message) {
        this.codes = codes;
        this.message = message;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}