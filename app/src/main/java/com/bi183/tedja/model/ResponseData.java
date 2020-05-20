package com.bi183.tedja.model;

import java.util.List;

public class ResponseData {
    private String value;
    private String message;
    private List<Lagu> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Lagu> getResult() {
        return result;
    }
}
