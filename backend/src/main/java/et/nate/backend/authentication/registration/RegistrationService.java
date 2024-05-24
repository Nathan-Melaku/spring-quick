package et.nate.backend.authentication.registration;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.registration.dto.RegistrationRequest;
import et.nate.backend.data.model.Role;
import et.nate.backend.data.model.User;
import et.nate.backend.data.model.VerificationToken;
import et.nate.backend.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

/**
 * Registration service, if the user doesn't exist generated a random verificationToken and saves the user
 * then triggers registration complete event.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(RegistrationRequest registrationRequest) throws UserAlreadyExistsException {

        if (userRepository.findByEmail(registrationRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        var verificationToken = new VerificationToken(
                0,
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS));

        var unverifiedRole = Role.builder(AuthConstants.DEFAULT_ROLE)
                .build();

        var user = User.builder(registrationRequest.email())
                .password(bCryptPasswordEncoder.encode(registrationRequest.password()))
                .firstName(registrationRequest.firstName())
                .lastName(registrationRequest.lastName())
                .verificationToken(verificationToken)
                .roles(Set.of(unverifiedRole));

        if (registrationRequest.address() != null) {
            user.address(registrationRequest.address().toUserAddress());
        }

        userRepository.save(user.build());
        eventPublisher.publishEvent(new RegistrationCompletedEvent(
                this,
                verificationToken.getToken(),
                registrationRequest.email())
        );
    }
}
