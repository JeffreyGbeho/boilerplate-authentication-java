package com.selfengineerjourney.auth.security.oauth;

import com.selfengineerjourney.auth.entity.Role;
import com.selfengineerjourney.auth.entity.RoleType;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.exception.UserAlreadyExistsException;
import com.selfengineerjourney.auth.repository.RoleRepository;
import com.selfengineerjourney.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        User user = userRepository.findByEmail(oidcUser.getEmail()).orElse(null);

        if (user != null && !user.getProvider().equals("google")) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if (user == null) {
            User newUser = new User();
            newUser.setUsername("user_" + UUID.randomUUID());
            newUser.setEmail(oidcUser.getEmail());
            newUser.setProvider(provider);
            newUser.setEnabled(true);

            Set<Role> roles = new HashSet<>();

            if (newUser.getEmail().equals("jeff.gbeho@gmail.com")) {
                roles.add(roleRepository.findByName(RoleType.ROLE_ADMIN).orElse(null));
            }

            roles.add(roleRepository.findByName(RoleType.ROLE_USER).orElse(null));

            newUser.setRoles(roles);

            userRepository.save(newUser);
        }

        return oidcUser;
    }
}

