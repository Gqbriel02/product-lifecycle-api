package com.example.productlifecycleapi.security;

import com.example.productlifecycleapi.model.AppUser;
import com.example.productlifecycleapi.repository.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public JpaUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert your AppUser to Spring Security's UserDetails
        // If you have roles, map them to GrantedAuthorities
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                // Or dynamically map roles from DB
        );
    }
}
