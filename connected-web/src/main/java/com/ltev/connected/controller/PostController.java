package com.ltev.connected.controller;

import static com.ltev.connected.controller.support.ControllerSupport.redirectToLastUrl;
import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        Optional<PostInfo> postInfoOptional = postService.getPostInfo(id);

        if (postInfoOptional.isPresent()) {
            model.addAttribute("postInfo", postInfoOptional.get());
            if (postInfoOptional.get().getLoggedUser() != null) {
                model.addAttribute("loggedUserId", postInfoOptional.get().getLoggedUser().getId());
            }
            return "post/show-post";
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
    public String saveOrRemoveLike(Long postId, Like.Value likeValue, HttpServletRequest request) {
        postService.saveOrRemoveLike(postId, likeValue);
        return redirectToLastUrl(request);
    }
}