package et.nate.backend.authentication.registration;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.model.Role;
import et.nate.backend.data.model.VerificationToken;
import et.nate.backend.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationVerificationService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void verifyToken(String token, Authentication authentication) throws RegistrationVerificationException {
        var user = userRepository.findById(Long.parseLong(authentication.getName()));
        if (user.isEmpty()) {
            throw new RegistrationVerificationException(AuthConstants.UNKNOWN_PROBLEM);
        }

        var verificationToken = user.get().getVerificationToken();

        // token doesn't exists
        if (verificationToken == null || !verificationToken.getToken().equals(token)) {
            throw new RegistrationVerificationException(AuthConstants.TOKEN_NOT_FOUND);
        }
        // token hasn't expired
        var expireDate = verificationToken.getExpiryDate();
        if (expireDate.isBefore(Instant.now())) {
            throw new RegistrationVerificationException(AuthConstants.TOKEN_EXPIRED);
        }
        // update user role
        var defaultRoles = new HashSet<Role>();

        defaultRoles.add(
                Role.builder(AuthConstants.USER_ROLE)
                        .build());

        var userDb = user.get();
        userDb.setRoles(defaultRoles);
        userDb.setVerificationToken(null);
        userRepository.save(user.get());

    }

    public void resendToken(Authentication authentication) throws RegistrationVerificationException {
        var verificationToken = new VerificationToken(
                0,
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS));

        var user = userRepository.findById(Long.parseLong(authentication.getName()));


        if (user.isEmpty()) {
            throw new RegistrationVerificationException(AuthConstants.UNKNOWN_PROBLEM);
        }

        user.get().setVerificationToken(verificationToken);
        userRepository.save(user.get());

        eventPublisher.publishEvent(new ResendRegistrationEvent(this, verificationToken.getToken(), user.get().getEmail()));
    }
}
