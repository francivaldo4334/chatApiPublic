package br.com.confchat.api.controllers;

import br.com.confchat.api.dtos.MessageDto;
import br.com.confchat.api.enums.RS;
import br.com.confchat.api.models.MessageModel;
import br.com.confchat.api.models.UserModel;
import br.com.confchat.api.repositories.ContactRepository;
import br.com.confchat.api.repositories.MessageRepository;
import br.com.confchat.api.services.EndToEndCriptService;

import br.com.confchat.api.services.UserService;
import br.com.confchat.api.utils.MapperUtil;
import br.com.confchat.api.viewmodel.ContactViewModel;
import br.com.confchat.api.viewmodel.MessageViewModel;
import br.com.confchat.api.viewmodel.ResponseDefaultViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Value("${api.public.pem}")
    private String publicKeyString;
    @Value("${api.private.pem}")
    private String privateKeyString;
    @Autowired
    private EndToEndCriptService endToEndCriptService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private MessageRepository messageRepository;
    @PostMapping(value = "/send")
    public ResponseEntity<ResponseDefaultViewModel> sendMessage(Authentication authentication, @RequestBody MessageDto data) {
        var checkUser = (UserModel)authentication.getPrincipal();
        var chat = userService.checkOrCreateContact(checkUser.getId(),data.getFromUserId(),data);
        var privateKey = userService.updatePairKey(chat);
        if(chat.isPresent())
            userService.createMessage(chat.get(),data,checkUser);
        else {
            return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(500,"Erro ao registrar"));
        }
        return ResponseEntity.ok(new ResponseDefaultViewModel(200,privateKey));
    }
    @GetMapping("/list-contact")
    public ResponseEntity<List<ContactViewModel>> listContact(Authentication authentication,
                                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size){
        var pageable = PageRequest.of(page,size);
        var checkUser = (UserModel)authentication.getPrincipal();
        var lsContact = contactRepository.findByUserId(checkUser.getId(),pageable);
        var response = lsContact.stream().map(MapperUtil::mapToViewModel).toList();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/message-by-chat")
    public ResponseEntity<List<MessageViewModel>> listMessage(@RequestParam int chatId,
                                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size){
        var pageable = PageRequest.of(page,size);
        var lsMessage = messageRepository.findByChatId(chatId,pageable);
        var response = lsMessage.stream().map(MapperUtil::mapToViewModel).toList();
        for (MessageModel e: lsMessage) {
            e.setRead(true);
            messageRepository.save(e);
        };
        return ResponseEntity.ok(response);
    }
//    @PutMapping(value = "/encript-message")
//    public ResponseEntity<String> encriptMessage(@RequestBody String message) throws Exception {
//        var publicKey = endToEndCriptService.convertPEMToPublicKey(publicKeyString);
//        var messageEncoder = endToEndCriptService.encriptMessage(message,publicKey);
//        return ResponseEntity.ok().body(Base64.getEncoder().encodeToString(messageEncoder));
//    }
}
