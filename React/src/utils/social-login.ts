export enum SocialLogin {
    GOOGLE = "google",
    GITHUB = "github",
}

function getProviderRedirect(loginProvider: SocialLogin): string {
    console.log(loginProvider)
    switch (loginProvider) {
        case SocialLogin.GITHUB:
            return "http://localhost:8080/oauth2/authorization/github"
        case SocialLogin.GOOGLE:
        default:
            return "";
    }
}

export function onSocialLogin(loginProvider: SocialLogin) {
    window.location.href = getProviderRedirect(loginProvider);
}

export function handle(jwt: string | null) {
    /*const val = jwt?.split('.').map(part => {
           return atob(part);
        }
    )

    console.log(val)*/
}
