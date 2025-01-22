package com.ltev.connected.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SearchInfo {

    private String firstName;
    private String lastName;
    private Byte minAge;
    private Byte maxAge;

    public String searchBy() {
        StringBuilder sb = new StringBuilder();
        if (hasFirstName()) {
            sb.append("-firstName");
        }
        if (hasLastName()) {
            sb.append("-lastName");
        }
        if (hasAge()) {
            sb.append("-age");
        }
        return sb.toString();
    }

    public String[] getSearchByProperties() {
        List<String> properties = new ArrayList<>(3);
        if (hasFirstName()) {
            properties.add("first_name");
        }
        if (hasLastName()) {
            properties.add("last_name");
        }
        if (hasAge()) {
            properties.add("age");
        }
        return properties.toArray(String[]::new);
    }

    public Object[] getSearchByValues() {
        List<Object> properties = new ArrayList<>(3);
        if (hasFirstName()) {
            properties.add(firstName);
        }
        if (hasLastName()) {
            properties.add(lastName);
        }
        if (hasAge()) {
            properties.add(fromDate());
            properties.add(toDate());
        }
        return properties.toArray(Object[]::new);
    }

    public boolean hasAge() {
        return (minAge != null || maxAge != null)
                && ((minAge != null && minAge != 0) || (maxAge != null && maxAge != 120));
    }

    /**
     * Returns the day that was 'maxAge' years and 355 days before today
     */
    public LocalDate fromDate() {
        return LocalDate.now().minusYears(maxAge + 1).plusDays(1);
    }

    /**
     * Returns the day that was 'minAge' years before today
     */
    public LocalDate toDate() {
        return LocalDate.now().minusYears(minAge);
    }

    public boolean hasFirstName() {
        return firstName != null && !firstName.isEmpty();
    }

    public boolean hasLastName() {
        return lastName != null && !lastName.isEmpty();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }
}