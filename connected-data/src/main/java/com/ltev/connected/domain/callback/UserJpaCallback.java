package com.ltev.connected.domain.callback;

import com.ltev.connected.converter.PasswordConverter;
import com.ltev.connected.domain.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

@ToString
public class UserJpaCallback {

    @Autowired
    private PasswordConverter passwordConverter;

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate(User user) {
        user.setPassword(passwordConverter.convertToDatabaseColumn(user.getPassword()));
    }
}