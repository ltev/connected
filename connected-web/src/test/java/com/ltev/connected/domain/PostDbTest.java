package com.ltev.connected.domain;

import com.ltev.connected.repository.main.PostRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostDbTest {

    @Autowired
    PostRepository postRepository;

    @Test
    void insertTest() {
        Post post = new Post();
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> postRepository.save(post));
    }
}