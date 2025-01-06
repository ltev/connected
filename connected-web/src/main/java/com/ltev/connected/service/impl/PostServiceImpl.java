package com.ltev.connected.service.impl;

import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.CommentRepository;
import com.ltev.connected.service.FriendRequestService;
import com.ltev.connected.service.PostService;
import com.ltev.connected.service.support.PostInfo;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private PostDao postDao;
    private UserDao userDao;
    private FriendRequestService friendRequestService;

    private CommentRepository commentRepository;

    @Override
    public void savePost(Post post) {
        // check if post has a user
        if (post.getUser() == null) {
            // check authentication
            Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();
            // get user
            User user = userDao.findByUsername(authentication.getName())
                    .orElseThrow(() -> new NoSuchElementException("No user with username: " + authentication.getName())
            );
            post.setUser(user);
        }
        postDao.save(post);
    }

    @Override
    public List<Post> findFriendsPosts(String username) {
        Long userId = userDao.findByUsername(username).get().getId();
        return postDao.findFriendsPosts(userId);
    }

    @Override
    public List<Post> findPosts(Long userId, Post.Visibility visibility) {
        return postDao.findPosts(userId, visibility);
    }

    @Override
    public PostInfo getPostInfo(Long postId) {
        PostInfo postInfo = new PostInfo();
        Optional<Post> post = postDao.findById(postId);

        if (post.isPresent()) {
            postInfo.setPost(post.get());

            if (AuthenticationUtils.isAuthenticated()) {
                /*
                When post.user -> fetch.LAZY, loggedUser must be fetched before post, otherwise if loggedUser will have
                the same id as post.user, then loggedUser class will be of the same type as post.user -> HibernateProxy,
                when the order is reverse both will be of class User
                 */
                User loggedUser = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName()).get();
                postInfo.setLoggedUser(loggedUser);

                if (! loggedUser.equals(post.get().getUser())) {
                    Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequest(loggedUser, post.get().getUser());

                    if (friendRequest.isPresent()
                            && friendRequest.get().getStatus(loggedUser.getUsername()) == FriendRequest.Status.ACCEPTED) {
                        postInfo.setFriends(true);
                    }
                }
            }
        }
        return postInfo;
    }

    @Override
    public void saveComment(Long postId, String commentText, Long loggedUserId) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        Comment comment = new Comment();
        comment.setUser(new User(loggedUserId));
        comment.setPost(new Post(postId));
        comment.setText(commentText);

        commentRepository.save(comment);
    }
}