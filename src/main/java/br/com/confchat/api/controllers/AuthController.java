package br.com.confchat.api.controllers;

import br.com.confchat.api.dtos.*;
import br.com.confchat.api.enums.RS;
import br.com.confchat.api.models.UserModel;
import br.com.confchat.api.repositories.UserRepository;
import br.com.confchat.api.services.TokenService;
import br.com.confchat.api.services.UserService;
import br.com.confchat.api.services.VerificationCodeService;
import br.com.confchat.api.utils.MyValidUtil;

import br.com.confchat.api.viewmodel.ResponseDefaultViewModel;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private MyValidUtil myValidUtil;
    @Autowired
    private UserService userService;
    

    @PostMapping(value = "/register")
    public @ResponseBody ResponseEntity<ResponseDefaultViewModel> Register(@RequestBody RegisterDTO data) throws MessagingException {
        var validations = userService.validDataRegister(data);
        if (validations.status != HttpStatus.OK.value())
            return ResponseEntity.status(validations.status).body(validations);
        UserModel newUser = userService.createUser(data);
        this.userRepository.save(newUser);
        return ResponseEntity.ok(new ResponseDefaultViewModel(200, "Um e-mail foi enviado para:" + newUser.getEmail()));
    }

    @PostMapping(value = "/login")
    public @ResponseBody ResponseEntity<ResponseDefaultViewModel> Login(@RequestBody AuthenticationTwoFactorDTO data) {
        Optional<UserModel> checkUser = userService.findByLoginOrEmai(data.loginOrEmail());
        String token = "";
        String UpdateToken = "";
        if (checkUser.isPresent() && checkUser.get().isActive()) {
            var deviceId = userService.checkDevices(data,checkUser.get());
            token = tokenService.generateToken(checkUser.get(), data,deviceId);
            UpdateToken = tokenService.createUpdateToken(checkUser.get(),data.password(),deviceId);
            userService.checkSuspiciusActivity(checkUser);
            if(!token.isEmpty()) {
                String newHash = new BCryptPasswordEncoder().encode(data.password());
                checkUser.get().setPassword(newHash);
                userRepository.save(checkUser.get());
            }
        }
        if(!token.isEmpty())
            token += ";";
        return ResponseEntity.ok(new ResponseDefaultViewModel(200, token + UpdateToken));
    }
    @DeleteMapping(value = "/logout")
    public @ResponseBody ResponseEntity<ResponseDefaultViewModel> Logout(@RequestHeader(name = "Authorization") String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            userService.logout(token);
        }
        return ResponseEntity.ok(new ResponseDefaultViewModel(200,"deslogado com sucesso."));
    }
    @PostMapping(value = "/update-login")
    public @ResponseBody ResponseEntity<ResponseDefaultViewModel> UpdateLogin(@RequestBody UpdateLoginDto data) {
        var checkUser = userService.getAuthDataByUpdateToken(data.updateToken());
        if (checkUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(400,"Error"));
        }
        var token = tokenService.genereateTokenByUpdate(checkUser.get(),data);
        return ResponseEntity.ok(new ResponseDefaultViewModel(200, token));
    }

    @PutMapping("/check-verification-code")
    public @ResponseBody ResponseEntity<ResponseDefaultViewModel> CheckVerificationCode(@RequestBody VerificationCodeDto verificationCodeDto) {
        var checkUser = userService.findByLoginOrEmai(verificationCodeDto.email());
        if (checkUser.isEmpty() || checkUser.get().getActive()) {
            return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(400, "usuário ativo."));
        }
        var isCodeValid = verificationCodeService.validVerificationCode(checkUser.get().getLogin(), verificationCodeDto.code());
        if (!isCodeValid) {
            return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(400, "código de verificação não encontrado."));
        }
        checkUser.get().setActive(true);
        userRepository.save(checkUser.get());
            return ResponseEntity.ok(new ResponseDefaultViewModel(200, "usuário ativado."));
    }

    @PutMapping("/resend-verification-code")
    public ResponseEntity<ResponseDefaultViewModel> ResendVerificationCode(@RequestParam String email) {
        Optional<UserModel> checkUser = userRepository.findByEmail(email);
        if (checkUser.isPresent() && !checkUser.get().getActive()) {
            try {
                userService.sendResetPasswordCode(checkUser.get());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(500, e));
            }
        }
        return ResponseEntity.ok(new ResponseDefaultViewModel(200, "Um e-mail foi enviado para:" + email));
    }

    @PostMapping("/send-password-recovery-email")
    public ResponseEntity<ResponseDefaultViewModel> senPasswordRecoveryEmail(@RequestParam String email) {
        Optional<UserModel> checkUser = userRepository.findByEmail(email);
        if (checkUser.isPresent() && checkUser.get().getActive()) {
            try {
                userService.sendRequestPassword(checkUser.get());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(500, e));
            }
        }
        return ResponseEntity.ok(new ResponseDefaultViewModel(200, "Um e-mail foi enviado para:" + email));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseDefaultViewModel> resetPassword(@RequestBody ResetPasswordDto data) {
        if (!myValidUtil.validPassword(data.newPassword()))
            return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(400, "A senha deve ter pelo menos 15 caracteres, um número e um caractere especial."));
        var checkUser = userService.findByLoginOrEmai(data.email());
        var result = userService.setNewPassword(checkUser, data);
        if(result)
            return ResponseEntity.ok(new ResponseDefaultViewModel(200, ""));
        return ResponseEntity.badRequest().body(new ResponseDefaultViewModel(400, ""));
    }
}
