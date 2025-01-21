package com.ltev.connected.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Converter
@Component
@ToString
public class PasswordConverter implements AttributeConverter<String, String> {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String convertToDatabaseColumn(String s) {
        if (!s.startsWith("{bcrypt}")) {
            s = "{bcrypt}" + encoder.encode(s);
        }
        return s;
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s;
    }
}