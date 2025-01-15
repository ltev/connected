package com.ltev.connected.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostTest {

    @Test
    void ofAtLeast() {
        List<Post.Visibility> expectedList = List.of(Post.Visibility.PRIVATE, Post.Visibility.FRIENDS,
                Post.Visibility.LOGGED_USERS, Post.Visibility.PUBLIC);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.PRIVATE));

        expectedList = List.of(Post.Visibility.FRIENDS, Post.Visibility.LOGGED_USERS, Post.Visibility.PUBLIC);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.FRIENDS));

        expectedList = List.of(Post.Visibility.LOGGED_USERS, Post.Visibility.PUBLIC);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.LOGGED_USERS));

        expectedList = List.of(Post.Visibility.PUBLIC);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.PUBLIC));
    }
}