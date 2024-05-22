package et.nate.backend.authentication;

public interface AuthConstants {
    public String ERROR = "error";
    public String TOKEN_VALIDATION_ERROR = "Failed to validate JWT token";
    public String INVALID_TOKEN_ERROR = "Invalid token";
    public String REFRESH_TOKEN_REUSED_ERROR = "Refresh token reused";

    public String ACCESS_TOKEN = "access_token";
    public String REFRESH_TOKEN = "refresh_token";

    public String ACCESS_DENIED = "Access Denied";
    public String AUTHORIZATION_HEADER = "Authorization";
    public String SCOPE = "scope";
    public String BEARER = "Bearer ";
    public String ISSUER = "self";

    public String USER_ROLE = "USER";

    public String GITHUB_EMAIL = "email";
    public String GITHUB_NAME = "name";
    public String GITHUB_AVATAR_IMG = "avatar_url";
    public String UNSUPPORTED_AUTH_PROVIDER = "Unsupported authentication provider";
    public String CSRF_ATTRIBUTE_NAME = "_csrf";
}
