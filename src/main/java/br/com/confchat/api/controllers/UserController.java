package br.com.confchat.api.controllers;

import br.com.confchat.api.models.UserModel;
import br.com.confchat.api.repositories.ContactRepository;
import br.com.confchat.api.repositories.DeviceRepository;
import br.com.confchat.api.repositories.UserRepository;
import br.com.confchat.api.services.VerificationCodeService;
import br.com.confchat.api.utils.EncryptionUtil;
import br.com.confchat.api.utils.MapperUtil;
import br.com.confchat.api.utils.TokenDecodeUtil;
import br.com.confchat.api.viewmodel.ContactViewModel;
import br.com.confchat.api.viewmodel.DeviceViewModel;
import br.com.confchat.api.viewmodel.ResponseDefaultViewModel;
import com.auth0.jwt.JWT;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private EncryptionUtil encryptionUtil;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ContactRepository contactRepository;
    @GetMapping("/me")
    public ResponseEntity<ResponseDefaultViewModel> getMe(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();
        return ResponseEntity.ok(new ResponseDefaultViewModel(200,""));
    }
    @GetMapping("/two-factor-authentication-config")
    public ResponseEntity<String> twoFactorAuthConfig(@RequestParam boolean enableTotp) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = (UserModel) authentication.getPrincipal();
        String imgBuffer = null;
        if (enableTotp) {
            String secret = "";
            if (user.getEncriptTotpSecret() != null) {
                secret = encryptionUtil.decrypt(user.getEncriptTotpSecret());
            } else {
                secret = encryptionUtil.generateSecretKey();
                user.setEncriptTotpSecret(encryptionUtil.encrypt(secret));
                userRepository.save(user);
            }
            imgBuffer = verificationCodeService.generateTotpUri(user.getEmail(), secret);
        } else {
            user.setEncriptTotpSecret(null);
        }
        user.setFlTwoFactorAuth(enableTotp);
        userRepository.save(user);
        return ResponseEntity.ok(imgBuffer);
    }

    @GetMapping("/generate-qr-code-totp")
    public ResponseEntity<BufferedImage> getQrCodeTotp(@RequestParam String uri) throws WriterException {
        var imgBuffer = verificationCodeService.generateTotpQrCode(uri);
        return ResponseEntity.ok(imgBuffer);
    }
    @GetMapping("/loged-devices")
    public ResponseEntity<List<DeviceViewModel>> getListDevices(Authentication authentication,@RequestHeader(name = "Authorization") String authorizationHeader){
        var checkUser = (UserModel)authentication.getPrincipal();
        var devices = deviceRepository.findAllByUserId(checkUser.getId());
        var thisDeviceId = TokenDecodeUtil.getDeviceId(authorizationHeader);
        var devicesViewModels = devices.stream().map(e ->MapperUtil.mapToViewModel(e,thisDeviceId));
        return ResponseEntity.ok(devicesViewModels.toList());
    }
    @DeleteMapping("/delete-device")
    public ResponseEntity<Boolean> deleteDevice(@RequestParam UUID deviceId){
        var checkDevice = deviceRepository.findById(deviceId);
        checkDevice.ifPresent(e -> deviceRepository.delete(e));
        return ResponseEntity.ok(true);
    }
}
