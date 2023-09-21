package br.com.confchat.api.services;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class EndToEndCriptService {
    public EndToEndCriptService(){
        Security.addProvider(new BouncyCastleProvider());
    }
    public KeyPair generatePairKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(256);
        return keyPairGenerator.generateKeyPair();
    }
    public String decriptorMessage(byte[] message,PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA","BC");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(message);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes, 0, encryptedBytes.length);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    public byte[] encriptMessage (String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA","BC");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(messageBytes, 0, messageBytes.length);
    }
    public String publicKeyToPEM(PublicKey publicKey) throws Exception {
        PemObject pemObject = new PemObject("PUBLIC KEY", publicKey.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        return stringWriter.toString();
    }

    public String privateKeyToPEM(PrivateKey privateKey) throws Exception {
        PemObject pemObject = new PemObject("PRIVATE KEY", privateKey.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        return stringWriter.toString();
    }
    public PublicKey convertPEMToPublicKey(String publicKeyPEM) throws Exception {
        var publicKeyPEMClean = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----","")
                .replace("-----END PUBLIC KEY-----","")
                .replace("\n","");
        byte[] publicKeyBytes = Base64.decode(publicKeyPEMClean);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }
    public PrivateKey convertPEMToPrivateKey(String privateKeyPEM) throws Exception {
        var privateKeyPEMClean = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----","")
                .replace("-----END PRIVATE KEY-----","")
                .replace("\n","");
        byte[] privateKeyBytes = Base64.decode(privateKeyPEMClean);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public String generatePairKeyString() {
        try{
            var pair = generatePairKey();
            var publicKey = publicKeyToPEM(pair.getPublic());
            var privateKey = privateKeyToPEM(pair.getPrivate());
            return publicKey + "\n" + privateKey;
        } catch (Exception e) {
            return null;
        }
    }
}
