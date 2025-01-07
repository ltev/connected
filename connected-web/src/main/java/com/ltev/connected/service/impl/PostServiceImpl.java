package com.ltev.connected.service.impl;

import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.*;
import com.ltev.connected.repository.CommentRepository;
import com.ltev.connected.repository.LikeRepository;
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

    private LikeRepository likeRepository;

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
         return postDao.findFriendsPosts(username, Post.Visibility.ofAtLeast(Post.Visibility.FRIENDS));
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

                // check if friends
                if (! loggedUser.equals(post.get().getUser())) {
                    Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequest(loggedUser, post.get().getUser());

                    if (friendRequest.isPresent()
                            && friendRequest.get().getStatus(loggedUser.getUsername()) == FriendRequest.Status.ACCEPTED) {
                        postInfo.setFriends(true);
                    }
                }

                // check if like
                Optional<Like> like = likeRepository.findById(new Like.Id(post.get(), loggedUser));
                like.ifPresent(l -> postInfo.setLikeValue(l.getValue()));
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

        // update comments count
        postDao.increaseNumCommentsByOne(postId);
    }

    @Override
    public void saveOrRemoveLike(Long postId, Like.Value likeValue) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName())
                .orElseThrow(() -> new NoSuchElementException("No user"));
        Like.Id likeId = new Like.Id(new Post(postId), user);

        Optional<Like> dbLike = likeRepository.findById(likeId);

        if (dbLike.isPresent()) {
            Like like = dbLike.get();
            if (dbLike.get().getValue() == likeValue) {
                likeRepository.delete(like);
            } else {
                like.setValue(likeValue);
                likeRepository.save(like);
            }
        } else {
            Like like = new Like();
            like.setId(likeId);
            like.setValue(likeValue);
            likeRepository.save(like);
        }
    }
}