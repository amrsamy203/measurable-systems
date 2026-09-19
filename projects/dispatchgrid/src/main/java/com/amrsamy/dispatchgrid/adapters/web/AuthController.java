package com.amrsamy.dispatchgrid.adapters.web;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.UserEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.UserRepository;
import com.amrsamy.dispatchgrid.adapters.web.dto.ApiDtos;
import com.amrsamy.dispatchgrid.adapters.web.security.DispatchGridUserPrincipal;
import com.amrsamy.dispatchgrid.adapters.web.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDtos.LoginResponse> login(@Valid @RequestBody ApiDtos.LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        DispatchGridUserPrincipal principal = (DispatchGridUserPrincipal) auth.getPrincipal();
        UserEntity user = userRepository.findByEmailIgnoreCase(principal.getUsername()).orElseThrow();
        String token = jwtService.generateToken(
                principal.getId(),
                principal.getUsername(),
                principal.getRole(),
                principal.getTenantId());
        return ResponseEntity.ok(new ApiDtos.LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getDisplayName(),
                user.getTenantId()));
    }
}
