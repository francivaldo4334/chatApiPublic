package br.com.confchat.api.services;

import br.com.confchat.api.dtos.AuthenticationTwoFactorDTO;
import br.com.confchat.api.dtos.UpdateLoginDto;
import br.com.confchat.api.models.UserModel;
import br.com.confchat.api.repositories.DeviceRepository;
import br.com.confchat.api.utils.EncryptionUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    @Autowired
    private EncryptionUtil encryptionUtil;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DeviceRepository deviceRepository;

    public String generateToken(UserModel checkUser, AuthenticationTwoFactorDTO data, Optional<UUID> deviceId){
        if(deviceId.isEmpty())
            return "";
        if (checkUser.isFlTwoFactorAuth() && checkUser.getEncriptTotpSecret() != null) {
            String secret = encryptionUtil.decrypt(checkUser.getEncriptTotpSecret());
            boolean isValidTotp = verificationCodeService.validateTOTP(data.totpCode(), secret);
            if (isValidTotp) {
                return tokenService.checkPassword(checkUser, data.password(),deviceId.get());
            }
        }
        return tokenService.checkPassword(checkUser,data.password(),deviceId.get());
    }
    public String genereateTokenByUpdate(UserModel checkUser, UpdateLoginDto data){
        var deviceIdString = JWT.decode(data.updateToken()).getClaim("device");
        var deviceId = UUID.fromString(deviceIdString.asString());
        return tokenService.createToken(checkUser,deviceId);
    }
    public String checkPassword(UserModel user, String password, UUID deviceId){
        if(!new BCryptPasswordEncoder().matches(password,user.getPassword()))
            return "";
        try{
            var resp = createToken(user,deviceId);
            return resp;
        }catch (JWTCreationException e){
            throw new RuntimeException("Error while generating token",e);
        }
    }
    private String createToken(UserModel user, UUID deviceId){
        Date now = new Date();
        long EXPIRATION_TIME = 8 * 60 * 60 * 1000;
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("confchat-api")
                .withKeyId(user.getId().toString())
                .withSubject(user.getLogin())
                .withExpiresAt(expiration)
                .withClaim("device",deviceId.toString())
                .sign(algorithm);
    }
    public String createUpdateToken(UserModel user,String password,Optional<UUID> deviceId){
        if(deviceId.isEmpty())
            return "";
        if(!new BCryptPasswordEncoder().matches(password,user.getPassword()))
            return "";
        try{
            Date now = new Date();
            long EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;
            Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("confchat-api-update")
                    .withKeyId(user.getId().toString())
                    .withSubject(user.getLogin())
                    .withExpiresAt(expiration)
                    .withClaim("device",deviceId.get().toString())
                    .sign(algorithm);
        }catch (JWTCreationException e){
            throw new RuntimeException("Error while generating token",e);
        }
    }
    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Claim deviceId;
            String userId;
            try {
                deviceId = JWT.decode(token).getClaim("device");
                userId = JWT.decode(token).getKeyId();
                UUID uuidDevice = UUID.fromString(deviceId.asString());
                UUID uuidUser = UUID.fromString(userId);
                var checkDevice = deviceRepository.findByIdAndUserId(uuidDevice,uuidUser);
                if(checkDevice.isEmpty())
                    return null;

            } catch (Exception e) {
                return null;
            }
            return JWT.require(algorithm)
                    .withIssuer("confchat-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return null;
        }
    }
    public String validateUpdateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Claim deviceId;
            String userId;
            try {
                deviceId = JWT.decode(token).getClaim("device");
                userId = JWT.decode(token).getKeyId();
                UUID uuidDevice = UUID.fromString(deviceId.asString());
                UUID uuidUser = UUID.fromString(userId);
                var checkDevice = deviceRepository.findByIdAndUserId(uuidDevice,uuidUser);
                if(checkDevice.isEmpty())
                    return null;

            } catch (Exception e) {
                return null;
            }
            return JWT.require(algorithm)
                    .withIssuer("confchat-api-update")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return null;
        }
    }
}
