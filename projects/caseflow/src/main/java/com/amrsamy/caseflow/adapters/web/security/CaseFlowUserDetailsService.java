package com.amrsamy.caseflow.adapters.web.security;

import com.amrsamy.caseflow.adapters.persistence.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CaseFlowUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CaseFlowUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .map(u -> new CaseFlowUserPrincipal(
                        u.getId(),
                        u.getTenantId(),
                        u.getEmail(),
                        u.getPasswordHash(),
                        u.getRole(),
                        u.isActive()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
