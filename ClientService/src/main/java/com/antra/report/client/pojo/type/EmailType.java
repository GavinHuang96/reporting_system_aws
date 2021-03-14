package com.antra.report.client.pojo.type;

public enum EmailType {
    SUCCESS("Hi, your %TYPE% report is generated.");

    public String content;

    EmailType(String content) {
        this.content = content;
    }
}
