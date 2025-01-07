package com.ltev.connected.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostTest {

    @Test
    void ofAtLeast() {
        List<Post.Visibility> expectedList = List.of(Post.Visibility.MYSELF, Post.Visibility.FRIENDS,
                Post.Visibility.LOGGED_USERS, Post.Visibility.EVERYONE);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.MYSELF));

        expectedList = List.of(Post.Visibility.FRIENDS, Post.Visibility.LOGGED_USERS, Post.Visibility.EVERYONE);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.FRIENDS));

        expectedList = List.of(Post.Visibility.LOGGED_USERS, Post.Visibility.EVERYONE);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.LOGGED_USERS));

        expectedList = List.of(Post.Visibility.EVERYONE);
        assertEquals(expectedList, Post.Visibility.ofAtLeast(Post.Visibility.EVERYONE));
    }
}