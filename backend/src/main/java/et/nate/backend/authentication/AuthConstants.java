package et.nate.backend.authentication;

public interface AuthConstants {
    String ERROR = "error";
    String TOKEN_VALIDATION_ERROR = "Failed to validate JWT token";
    String INVALID_TOKEN_ERROR = "Invalid token";
    String REFRESH_TOKEN_REUSED_ERROR = "Refresh token reused";
    String USER_NOT_FOUND_ERROR = "User not found";
    String USER_ALREADY_EXISTS_ERROR = "User already exists";
    String TOKEN_NOT_FOUND = "Verification token not found";
    String TOKEN_EXPIRED = "Expired token";
    String UNKNOWN_PROBLEM = "Unknown Problem Happened";

    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    String ACCESS_DENIED = "Access Denied";
    String AUTHORIZATION_HEADER = "Authorization";
    String SCOPE = "scope";
    String BEARER = "Bearer ";
    String ISSUER = "self";


    String GITHUB_EMAIL = "email";
    String GITHUB_NAME = "name";
    String GITHUB_AVATAR_IMG = "avatar_url";
    String UNSUPPORTED_AUTH_PROVIDER = "Unsupported authentication provider";
    String CSRF_ATTRIBUTE_NAME = "_csrf";
    String REGISTRATION_COMPLETED = "registration completed";
    String VERIFICATION_COMPLETED = "verification completed";
    String RESEND_COMPLETED = "resend completed";

    String DEFAULT_ROLE = "ROLE_UNVERIFIED";
    String ADMIN_ROLE = "ROLE_ADMIN";
    String USER_ROLE = "ROLE_USER";
    String ADMIN = "ADMIN";
    String USER = "USER";
    String BAD_CREDENTIALS = "Bad credentials";
    String USER_CONTEXT_COOKIE = "__Secure-UserContext";
    String USER_CONTEXT_REFRESH_COOKIE = "__Secure-UserContext-Refresh";
}
