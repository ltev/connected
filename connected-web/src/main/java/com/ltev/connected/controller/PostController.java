package com.ltev.connected.controller;

import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.service.PostService;
import com.ltev.connected.service.support.PostInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(params = "action=post-form")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/post-form";
    }

    @PostMapping(params = "action=new-post")
    public String createNewPost(Post post) {
        postService.savePost(post);
        return "redirect:/";
    }

    @GetMapping("post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        PostInfo postInfo = postService.getPostInfo(id);
        Post post = postInfo.getPost();

        if (post != null) {
            if (post.getVisibility() == Post.Visibility.EVERYONE
                || (postInfo.isUserLogged()
                    && (postInfo.isSelfPost()
                        || (postInfo.isFriends() && Post.Visibility.atLeast(Post.Visibility.FRIENDS, post.getVisibility())) // friends
                        || Post.Visibility.atLeast(Post.Visibility.LOGGED_USERS, post.getVisibility())  // not friends
            ))) {
                model.addAttribute("post", post);
                if (postInfo.getLoggedUser() != null) {
                    model.addAttribute("loggedUserId", postInfo.getLoggedUser().getId());
                }
                return "post/show-post";
            }
        }
        return "redirect:/";
    }

    @PostMapping("post/{id}")
    public String saveComment(@PathVariable("id") Long postId,
                              @RequestParam("loggedUserId") Long loggedUserId,
                              @RequestParam("commentText") String comment) {
        postService.saveComment(postId, comment, loggedUserId);
        return "redirect:/post/" + postId;
    }

    @PostMapping(params = "likeValue")
    public String saveLike(Long postId, Like.Value likeValue, HttpServletRequest  request) {
        postService.saveLike(postId, likeValue);
        return "redirect:" + request.getHeader("Referer");
    }
}