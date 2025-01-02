package com.ltev.connected.service.impl;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.UserRepository;
import com.ltev.connected.repository.dao.PostDao;
import com.ltev.connected.service.PostService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostDao postDao;
    private UserRepository userRepository;

    @Override
    public void save(Post post) {
        // check if post has a user
        if (post.getUser() == null) {
            // check authentication
            Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();
            // get user
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new NoSuchElementException("No user with username: " + authentication.getName())
            );
            post.setUser(user);
        }
        postDao.save(post);
    }

    @Override
    public List<Post> findFriendsPosts(String username) {
        Long userId = userRepository.findByUsername(username).get().getId();
        return postDao.findFriendsPosts(userId);
    }
}
