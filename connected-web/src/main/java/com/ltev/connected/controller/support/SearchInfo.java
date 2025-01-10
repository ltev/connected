package com.ltev.connected.controller.support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

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