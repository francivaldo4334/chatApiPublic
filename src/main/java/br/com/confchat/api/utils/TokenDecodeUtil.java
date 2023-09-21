package br.com.confchat.api.utils;

import com.auth0.jwt.JWT;

import java.util.UUID;

public class TokenDecodeUtil {
    public static UUID getDeviceId(String token){
        var _toke = token;
        if(token.startsWith("Bearer "))
            _toke = token.substring(7);
        var claim = JWT.decode(_toke).getClaim("device").asString();
        var thisDeviceId = UUID.fromString(claim);
        return thisDeviceId;
    }
}
