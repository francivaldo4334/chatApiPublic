package br.com.confchat.api.services;

import br.com.confchat.api.dtos.AuthenticationTwoFactorDTO;
import br.com.confchat.api.dtos.MessageDto;
import br.com.confchat.api.dtos.RegisterDTO;
import br.com.confchat.api.dtos.ResetPasswordDto;
import br.com.confchat.api.enums.EmailTemplet;
import br.com.confchat.api.enums.RS;
import br.com.confchat.api.enums.UserRole;
import br.com.confchat.api.models.*;
import br.com.confchat.api.repositories.*;
import br.com.confchat.api.utils.MyValidUtil;
import br.com.confchat.api.viewmodel.ResponseDefaultViewModel;
import com.auth0.jwt.JWT;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyValidUtil myValidUtil;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private EndToEndCriptService endToEndCriptService;
    @Autowired
    private MessageRepository messageRepository;
    @Value("${api.security.token.secret}")
    private String secret;
    @Value("${api.private.pem}")
    private String privateKeyString;

    public ResponseDefaultViewModel validDataRegister(RegisterDTO data) {
        if (userRepository.findByEmail(data.email()).isPresent())
            return new ResponseDefaultViewModel(400, "E-mail já registrado.");
        if (this.userRepository.findByLogin(data.login()).isPresent())
            return new ResponseDefaultViewModel(400, "usuário existente.");
        if (!myValidUtil.validLogin(data.login()))
            return new ResponseDefaultViewModel(400, "O login ou e-mail deve conter apenas números e letras e ter pelo menos 8 caracteres.");
        if (!myValidUtil.validPassword(data.password()))
            return new ResponseDefaultViewModel(400, "A senha deve ter pelo menos 15 caracteres, um número e um caractere especial.");
        if (!myValidUtil.validEmail(data.email()))
            return new ResponseDefaultViewModel(400, "E-mail deve ter um formato válido.");
        if (!myValidUtil.validBirthDay(data.birthDay()))
            return new ResponseDefaultViewModel(400, "O usuário deve ter pelo menos 18 anos de idade.");
        if (!myValidUtil.validName(data.name()))
            return new ResponseDefaultViewModel(400, "O nome deve conter primeiro e último nome.");
        return new ResponseDefaultViewModel(200, "sucesso");
    }

    public UserModel createUser(RegisterDTO data) throws MessagingException {
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserModel newUser = new UserModel(
                data.login(),
                data.name(),
                data.email(),
                encryptedPassword,
                UserRole.USER,
                data.birthDay());
        sendVerificationCode(newUser);
        return newUser;
    }

    public void sendVerificationCode(UserModel user) throws MessagingException {
        var code = verificationCodeService.generateVerificationCode(user.getLogin());
        emailService.sendEmail(
                user.getEmail(),
                "new account verification",
                emailService.getTemplat(EmailTemplet.AuthCode).replace("{code}", code)
        );
    }
    public void sendResetPasswordCode(UserModel user) throws MessagingException {
        var code = verificationCodeService.generateVerificationCode(user.getEmail());
        emailService.sendEmail(
                user.getEmail(),
                "new account verification",
                emailService.getTemplat(EmailTemplet.AuthCode).replace("{code}", code)
        );
    }

    public void sendRequestPassword(UserModel user) throws MessagingException {
        var code = verificationCodeService.generateVerificationCode(user.getEmail() + secret);
        emailService.sendEmail(
                user.getEmail(),
                "new account verification",
                emailService.getTemplat(EmailTemplet.ResetPassword).replace("{code}", code)
        );
    }

    public boolean setNewPassword(Optional<UserModel> checkUser, ResetPasswordDto data) {
        if (checkUser.isEmpty())
            return false;
        if (verificationCodeService.validVerificationCode(checkUser.get().getEmail() + secret, data.code())) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(data.newPassword());
            checkUser.get().setPassword(encryptedPassword);
            userRepository.save(checkUser.get());
            return true;
        }
        return false;
    }

    public Optional<UserModel> findByLoginOrEmai(String loginOrEmail) {
        if (myValidUtil.validEmail(loginOrEmail)) {
            return userRepository.findByEmail(loginOrEmail);
        }
        return userRepository.findByLogin(loginOrEmail);
    }

    public void checkSuspiciusActivity(Optional<UserModel> checkUser) {
        var accessLimint = new Timestamp(System.currentTimeMillis());
        var lastAccess = checkUser.get().getLastAccess();
        if (lastAccess == null) {
            lastAccess = new Timestamp(System.currentTimeMillis());
        }
        var dateLimint = accessLimint.toLocalDateTime().minusMinutes(30);
        accessLimint = Timestamp.valueOf(dateLimint);
        if (checkUser.get().getNumberAccess() > 5 && lastAccess.compareTo(accessLimint) > 0) {
            try {
                emailService.sendEmail(
                        checkUser.get().getEmail(),
                        "Atificade suspeita",
                        emailService.getTemplat(EmailTemplet.SuspiciousActivity)
                );
            } catch (Exception ignored) {
            }
            checkUser.get().setNumberAccess(0);
        } else if (lastAccess.compareTo(accessLimint) > 0) {
            checkUser.get().setNumberAccess(checkUser.get().getNumberAccess() + 1);
        } else {
            checkUser.get().setNumberAccess(0);
        }
        checkUser.get().setLastAccess(new Timestamp(System.currentTimeMillis()));
        userRepository.save(checkUser.get());
    }
    public Optional<UUID> checkDevices(AuthenticationTwoFactorDTO data, UserModel userModel) {
        Optional<DeviceModel> checkDevice;
        try {
            var uuidDevice = UUID.fromString(data.deviceId());
            checkDevice = deviceRepository.findByIdAndUserId(uuidDevice, userModel.getId());
        }
        catch (Exception e){
            checkDevice = Optional.empty();
        }
        if (checkDevice.isEmpty()) {
            var countDevice = deviceRepository.findAllByUserId(userModel.getId()).size();
            if(countDevice > 10){
                return Optional.empty();
            }
            var uuidDevice = createNewDevice(data,userModel);
            return uuidDevice;
        }
        return Optional.empty();
    }
    private Optional<UUID> createNewDevice(AuthenticationTwoFactorDTO data, UserModel userModel){
        var newDevice = new DeviceModel();
        var nameDevice = data.deviceName();
        if(nameDevice.isEmpty())
            nameDevice = "Desconhecido";
        newDevice.setUser(userModel);
        newDevice.setName(data.deviceName());
        deviceRepository.save(newDevice);
        try {
            emailService.sendEmail(userModel.getEmail(),
                    "new device connected",
                    emailService.getTemplat(EmailTemplet.NewDeviceConected));
            return Optional.of(newDevice.getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Optional<UserModel> getAuthDataByUpdateToken(String updateToken) {
        var subject = tokenService.validateUpdateToken(updateToken);
        if(subject == null || subject.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByLogin(subject);
    }

    public void logout(String token) {
        var deviceId = UUID.fromString(JWT.decode(token).getClaim("device").asString());
        var deviceModel = deviceRepository.findById(deviceId);
        deviceModel.ifPresent(model -> deviceRepository.delete(model));
    }

    public Optional<ChatModel> checkOrCreateContact(UUID id, UUID fromUserId,MessageDto data) {
        ChatModel chat;
        var checkContact = contactRepository.findByUserIdAndUserContactId(id,fromUserId);
        if(checkContact.isEmpty()){
            var contactUser = userRepository.findById(fromUserId);
            var thisUser = userRepository.findById(id);
            if(contactUser.isPresent() && thisUser.isPresent()){
                return createContact(id,fromUserId,data,contactUser.get(),thisUser.get());
            }
            else {
                return Optional.empty();
            }
        }
        else {
            chat = checkContact.get().getFkChat();
            return Optional.of(chat);
        }
    }
    private Optional<ChatModel> createContact(UUID id, UUID fromUserId,MessageDto data,UserModel contactUser,UserModel thisUser){
        ChatModel chat;
        var checkContactInOtherUser = contactRepository.findByUserIdAndUserContactId(fromUserId,id);
        if(checkContactInOtherUser.isPresent()){
            chat = checkContactInOtherUser.get().getFkChat();
        }
        else {
            var newChat = new ChatModel();
            newChat.setPrivateKey(data.getPrivateKey().getBytes(StandardCharsets.UTF_8));
            newChat.setPublicKey(data.getPublicKey().getBytes(StandardCharsets.UTF_8));
            chat = chatRepository.save(newChat);
        }
        var newContact1 = new ContactModel();
        newContact1.setUserContact(contactUser);
        newContact1.setUser(thisUser);
        newContact1.setName(contactUser.getName());
        newContact1.setFkChat(chat);
        contactRepository.save(newContact1);

        var newContact2 = new ContactModel();
        newContact2.setUserContact(thisUser);
        newContact2.setUser(contactUser);
        newContact2.setName(thisUser.getName());
        newContact2.setFkChat(chat);
        contactRepository.save(newContact2);
        return Optional.of(chat);
    }

    public void createMessage(ChatModel chat, MessageDto data,UserModel userId) {
        var newMessage = new MessageModel();
        newMessage.setUser(userId);
        newMessage.setChatModel(chat);
        newMessage.setContent(data.getMessage());
        messageRepository.save(newMessage);
    }

    public String updatePairKey(Optional<ChatModel> chat) {
        byte[] privateKey = new String().getBytes(StandardCharsets.UTF_8);
        var checkContacts = contactRepository.findByIdChatId(chat.get().getId());
        for (ContactModel e : checkContacts) {
            var id = e.getUser().getId();
            var checkMessage = messageRepository.findByUserId(id);
            if(checkMessage.isEmpty()){
                privateKey = chat.get().getPrivateKey();
                try {
                    var privateKeyPEM = endToEndCriptService.convertPEMToPrivateKey(privateKeyString);
                    return endToEndCriptService.decriptorMessage(privateKey,privateKeyPEM);
                }catch (Exception ex){
                    return null;
                }
            }
        };
        chat.get().setPrivateKey(null);
        chatRepository.save(chat.get());
        return null;
    }
}
