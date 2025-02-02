package com.ltev.connected.controller;

import com.ltev.connected.domain.Message;
import com.ltev.connected.dto.ConversationInfo;
import com.ltev.connected.dto.MessagesInfo;
import com.ltev.connected.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/messages")
public class MessagesController {

    private MessageService messageService;

    @GetMapping
    public String showMessagesMainPage(Model model) {
        MessagesInfo messagesInfo = messageService.getMessagesInfo();
        model.addAttribute("messagesInfo", messagesInfo);
        return "messages/show-main";
    }

    @GetMapping("/{username}")
    public String showConversationAndMessageForm(@PathVariable String username, Model model) {
        ConversationInfo messagesInfo = messageService.getConversationInfo(username);
        if (! messagesInfo.profileExist()) {
            return "redirect:/messages";
        }

        model.addAttribute("conversationInfo", messagesInfo);
        model.addAttribute("message", new Message());
        return "messages/message-form";
    }

    @PostMapping(path = "/{username}", params = "action=new-message")
    public String createNewMessage(@PathVariable String username,
                                                 @RequestParam Long profileId,
                                                 @Valid Message message,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "messages/message-form";
        }
        messageService.saveMessage(message, profileId);
        return "redirect:/messages/" + username;
    }
}