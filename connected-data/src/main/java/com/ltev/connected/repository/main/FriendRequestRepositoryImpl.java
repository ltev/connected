package com.ltev.connected.repository.main;

import com.ltev.connected.domain.FriendRequest;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.Optional;

//@Repository
@AllArgsConstructor
public class FriendRequestRepositoryImpl  {

    private EntityManagerFactory emf;

    public Optional<FriendRequest> findFriendRequest(Long userId1, Long userId2) {
        try (var em = emf.createEntityManager()) {

            return Optional.ofNullable(
                    (FriendRequest) em.createNativeQuery("select * from friend_requests " +
                                    "where from_user_id in (?1, ?2) and to_user_id in (?1, ?2) and (from_user_id != to_user_id)")
                    .setParameter(1, userId1)
                    .setParameter(2, userId2)
                    .getSingleResult());
        }
    }
}