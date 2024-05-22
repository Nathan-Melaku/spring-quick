package et.nate.backend.data.model;

public enum SocialLoginProvider {
    GITHUB("github"),
    GITLAB("gitlab"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    MICROSOFT("microsoft"),
    APPLE("apple");

    private final String providerName;

    SocialLoginProvider(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String toString() {
        return providerName;
    }
}
