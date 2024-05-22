package et.nate.backend.authentication;

public interface AuthConstants {
    String ERROR = "error";
    String TOKEN_VALIDATION_ERROR = "Failed to validate JWT token";
    String INVALID_TOKEN_ERROR = "Invalid token";
    String REFRESH_TOKEN_REUSED_ERROR = "Refresh token reused";

    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    String ACCESS_DENIED = "Access Denied";
    String AUTHORIZATION_HEADER = "Authorization";
    String SCOPE = "scope";
    String BEARER = "Bearer ";
    String ISSUER = "self";

    String USER_ROLE = "USER";

    String GITHUB_EMAIL = "email";
    String GITHUB_NAME = "name";
    String GITHUB_AVATAR_IMG = "avatar_url";
    String UNSUPPORTED_AUTH_PROVIDER = "Unsupported authentication provider";
    String CSRF_ATTRIBUTE_NAME = "_csrf";
}
