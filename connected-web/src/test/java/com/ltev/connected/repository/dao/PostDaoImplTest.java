package com.ltev.connected.repository.dao;

import com.ltev.connected.dao.impl.PostDaoImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostDaoImpl.class)
class PostDaoImplTest {

    @Autowired
    PostDaoImpl postDaoImpl;

    @Test
    void findFriends() {
        System.out.println(postDaoImpl.findFriendsPosts(1L));
    }
}