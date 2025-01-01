package com.ltev.connected.service;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.PostRepository;
import com.ltev.connected.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;

    public void save(Post post) {
        // check if post has a user
        if (post.getUser() == null) {
            // check authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (! authentication.isAuthenticated()) {
                throw new RuntimeException("Not authenticated! " + authentication);
            }
            // get user
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new NoSuchElementException("No user with username: " + authentication.getName())
            );
            post.setUser(user);
        }
        postRepository.save(post);
    }
}
