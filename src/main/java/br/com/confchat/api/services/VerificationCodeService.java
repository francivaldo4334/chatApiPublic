package br.com.confchat.api.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import de.taimos.totp.TOTP;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Objects;

@Service
public class VerificationCodeService {
    @Autowired
    private QRCodeWriter qrCodeWriter;
    private final long _15Minute = 15 * 60 * 1000L;
    public String generateTotpUri(String email, String secret){
        var appName = "ConfChat";
        String uri = "otpauth://totp/"+appName+":"+email+"?secret=" + secret + "&issuer="+appName;
        return uri;

    }
    public BufferedImage generateTotpQrCode(String uri) throws WriterException{
        var matrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public boolean validateTOTP(String token, String secret) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secret);
        String hexKey = Hex.encodeHexString(bytes);
        var n = TOTP.getOTP(hexKey);
        return Objects.equals(n, token);
    }
    private String getOTP(String Key,long time){
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        try {
            return codeGenerator.generate(Key,time);
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateVerificationCode(String secret) {
        return getOTP(secret,_15Minute);
    }
    public boolean validVerificationCode(String secret,String code) {
        var checkCode = getOTP(secret,_15Minute);
        var resp = checkCode.equals(code);
        return  resp;
    }
}
