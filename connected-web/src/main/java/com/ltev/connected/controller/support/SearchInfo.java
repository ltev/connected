package com.ltev.connected.controller.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchInfo {

    private String firstName;
    private String lastName;

    public void check() {
        firstName = update(firstName);
        lastName = update(lastName);
    }

    public boolean hasFirstName() {
        return firstName != null;
    }

    public boolean hasLastName() {
        return lastName != null;
    }

    private String update(String str) {
        str = str.trim();
        return str.isEmpty() ? null : str;
    }
}