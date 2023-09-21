package br.com.confchat.api.controllers;

import br.com.confchat.api.services.EndToEndCriptService;
import jakarta.mail.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
@RequestMapping("adm")
public class AdmController {
    @Autowired
    private EndToEndCriptService endToEndCriptService;
    @GetMapping("pair-key")
    public ResponseEntity<ByteArrayResource> generatePairKey() throws Exception {
        var pair = endToEndCriptService.generatePairKey();
        var publicKey = endToEndCriptService.publicKeyToPEM(pair.getPublic());
        var privateKey = endToEndCriptService.privateKeyToPEM(pair.getPrivate());
        var pairFile = publicKey + "\n" + privateKey;
        var pairFileBytes = pairFile.getBytes();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment","file.pem");
        var byteArrayResource = new ByteArrayResource(pairFileBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pairFileBytes.length)
                .body(byteArrayResource);
    }
}
