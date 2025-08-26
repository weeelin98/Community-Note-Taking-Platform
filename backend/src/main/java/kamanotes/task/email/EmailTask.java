package com.kama.notes.service.impl;

import lombok.Data;

@Data
public class NEmailTask {
    private String email;
    private String code;
    private long timestamp;
}
