package com.ltev.connected.repository.dao;

import com.ltev.connected.domain.Post;
import com.ltev.connected.repository.PostRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@AllArgsConstructor
public class PostDao {

    private final PostRepository postRepository;
    private final EntityManagerFactory emf;


    public void save(Post post, String username) {
//        postRepositoryJpa.save(post);
//        try (var em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            int affectedRows = em.createNativeQuery("insert into users_posts values (" +
//                            "(select id from users where username = ?1), ?2)")
//                    .setParameter(1, username)
//                    .setParameter(2, post.getId())
//                    .executeUpdate();
//            if (affectedRows != 1) {
//                throw new RuntimeException("Should be 1 but was: " + affectedRows);
//            }
//            em.getTransaction().commit();
//        }
    }
}
