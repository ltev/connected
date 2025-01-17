package com.ltev.connected.service.validator;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    @Test
    void validateUsername_valid() {
        int MIN = 3, MAX = 50;
        String fieldName = "username";
        String[] validUsernames = {"abc", "fsafdsafdsa", "01234567890123456789012345678901234567890123456789"};

        for (String validUsername : validUsernames) {
            User user = new User();
            user.setUsername(validUsername);

            Validator<User> validator = new Validator<>(user);
            validator.validate();

            assertThat(validator.hasErrors()).isFalse();
            assertThat(validator.hasErrors(fieldName)).isFalse();
            assertThat(validator.numErrors()).isZero();
            assertThat(validator.numErrors(fieldName)).isZero();
        }
    }

    @Test
    void validateUsername_invalid() {
        int MIN = 3, MAX = 50;
        String fieldName = "username";

        String[] invalidUsernames = {null, "a", "bb", strOfLength(51)};
        int[] expectedErrorsCount = {2, 1, 1, 1};

        for (int i = 0; i < invalidUsernames.length; i++) {
            String invalidUsername = invalidUsernames[i];
            int expectedErrors = expectedErrorsCount[i];

            User user = new User();
            user.setUsername(invalidUsername);

            Validator<User> validator = new Validator<>(user);
            validator.validate();

            assertThat(validator.hasErrors()).isTrue();
            assertThat(validator.hasErrors(fieldName)).isTrue();
            assertThat(validator.numErrors()).isEqualTo(expectedErrors);
            assertThat(validator.numErrors(fieldName)).isEqualTo(expectedErrors);
            assertThat(validator.getFieldErrors(fieldName).get(0).getRejectedValue()).isEqualTo(invalidUsername);
        }
    }

    @Test
    void validatePostText_valid() {
        int MIN = 4, MAX = 1000;
        String fieldName = "text";
        Post post = new Post();
        post.setVisibility(Post.Visibility.PRIVATE);
        String[] validTexts = {"abcd", "fsafdsafdsa", strOfLength(1000)};

        for (String validText : validTexts) {
            post.setText(validText);

            Validator<Post> validator = new Validator<>(post);
            validator.validate();

            assertThat(validator.hasErrors()).isFalse();
            assertThat(validator.hasErrors(fieldName)).isFalse();
            assertThat(validator.numErrors()).isZero();
            assertThat(validator.numErrors(fieldName)).isZero();
        }
    }

    @Test
    void validatePostText_invalid() {
        int MIN = 4, MAX = 1000;
        String fieldName = "text";
        Post post = new Post();
        String[] invalidTexts = {null, "", "adb", strOfLength(1001)};
        int[] expectedErrorsCount = {3, 2, 1, 1};

        for (int i = 0; i < invalidTexts.length; i++) {
            String invalidText = invalidTexts[i];
            int expectedErrors = expectedErrorsCount[i];

            if (invalidText != null) {
                post.setText(invalidText);
            }

            Validator<Post> validator = new Validator<>(post);
            validator.validate();

            assertThat(validator.hasErrors()).isTrue();
            assertThat(validator.hasErrors(fieldName)).isTrue();
            assertThat(validator.numErrors(fieldName)).isEqualTo(expectedErrors);
        }
    }

    private String strOfLength(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
}