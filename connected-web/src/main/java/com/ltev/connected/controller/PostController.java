package com.ltev.connected.controller;

import com.ltev.connected.domain.Comment;
import com.ltev.connected.domain.Like;
import com.ltev.connected.domain.Post;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.ltev.connected.controller.support.ControllerSupport.getLastUrl;
import static com.ltev.connected.controller.support.ControllerSupport.redirectToLastUrl;

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
    public String createNewPost(@Valid Post post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "post/post-form";
        }
        postService.savePost(post);
        return "redirect:/";
    }

    @GetMapping("post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Optional<PostInfo> postInfoOptional = postService.getPostInfoForPersonPost(id);

        if (postInfoOptional.isPresent()) {
            model.addAttribute("postInfo", postInfoOptional.get());

            if (postInfoOptional.get().getLoggedUser() != null) {
                model.addAttribute("loggedUserId", postInfoOptional.get().getLoggedUser().getId());
                Comment comment = new Comment();
                comment.setUser(postInfoOptional.get().getLoggedUser());
                comment.setPost(postInfoOptional.get().getPost());
                model.addAttribute("comment", comment);
            }
            return "post/show-post";
        }
        return "redirect:/";
    }

    @PostMapping(path = "post/{postId}", params = "action=new-comment")     // {id} - if 'id' then it's automatically bind as comment.id
    public String saveComment(@PathVariable("postId") Long postId, @Valid Comment comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/post/" + postId + "?ics";     // ics = invalid comment size
        }
        postService.saveComment(comment);
        return "redirect:/post/" + postId;
    }

    @PostMapping(params = "likeValue")
    public String saveOrRemoveLike(Long postId, Like.Value likeValue, boolean isFromSelfAccordion, HttpServletRequest request) {
        postService.saveOrRemoveLike(postId, likeValue);
        if (isFromSelfAccordion) {
            return "redirect:/?self";
        }
        if (getLastUrl(request).equals("http://localhost:8080/?self")) {
            return "redirect:/";
        }
        // coming from group with page parameters - keep the ?page=x
        return redirectToLastUrl(request);
    }
}