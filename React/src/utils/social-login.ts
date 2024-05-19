export enum SocialLogin {
    GOOGLE = "google",
    GITHUB = "github",
}

export function getProviderRedirect(loginProvider: SocialLogin): string {
    console.log(loginProvider)
    switch (loginProvider) {
        case SocialLogin.GITHUB:
            return "http://localhost:8080/oauth2/authorization/github"
        case SocialLogin.GOOGLE:
        default:
            return "";
    }
}
