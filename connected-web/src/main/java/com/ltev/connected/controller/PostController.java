package com.ltev.connected.controller;

import com.ltev.connected.domain.Post;
import com.ltev.connected.service.impl.PostServiceImpl;
import com.ltev.connected.service.support.PostInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping(params = "action=post-form")
    public String showPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/post-form";
    }

    @PostMapping(params = "action=new-post")
    public String createNewPost(Post post) {
        postService.save(post);
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
                return "post/show-post";
            }
        }
        return "redirect:/";
    }
}