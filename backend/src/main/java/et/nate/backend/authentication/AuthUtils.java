package et.nate.backend.authentication;

import et.nate.backend.authentication.jwt.TokenResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class AuthUtils {

    public static void setCookies(HttpServletResponse response, TokenResult tokens) {
        var userContextCookie = new Cookie(AuthConstants.USER_CONTEXT_COOKIE, tokens.userContextCookie());
        userContextCookie.setHttpOnly(true);
        userContextCookie.setSecure(true);
        userContextCookie.setAttribute("SameSite", "Strict");
        userContextCookie.setMaxAge(tokens.accessExpiresAt());

        var refreshContext = new Cookie(AuthConstants.USER_CONTEXT_REFRESH_COOKIE, tokens.userContextCookie());
        refreshContext.setHttpOnly(true);
        refreshContext.setSecure(true);
        userContextCookie.setAttribute("SameSite", "Strict");
        refreshContext.setMaxAge(tokens.refreshExpiresAt());

        response.addCookie(userContextCookie);
        response.addCookie(refreshContext);
    }

    public static boolean dontMatchContextHash(String hash, String plain){
        try {
            var hexFormat = HexFormat.of();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedByte = digest.digest(plain.getBytes(StandardCharsets.UTF_8));
            String hashed = hexFormat.formatHex(hashedByte);
            return !hashed.equals(hash);
        } catch (NoSuchAlgorithmException e) {
            return true;
        }
    }
}
