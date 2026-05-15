package com.example.security_service.service;

import com.example.security_service.config.JwtUtil;
import com.example.security_service.dto.AuthRequest;
import com.example.security_service.dto.AuthResponse;
import com.example.security_service.dto.RefreshTokenRequest;
import com.example.security_service.dto.UserDetailDTO;
import com.example.security_service.model.UserSession;
import com.example.security_service.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserSessionRepository sessionRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    @Transactional
    public UserDetailDTO signUp(AuthRequest authRequest) {
        if (!StringUtils.hasText(authRequest.getPassword())) {
            throw new RuntimeException("Password is required");
        }

        // Prepare request object (using UserDetailDTO or a specific SignupRequestDTO if you have one)
        // Here we reuse AuthRequest fields to map to what user-service expects
        Map<String, String> signupRequest = new HashMap<>();
        signupRequest.put("userName", authRequest.getUserName());
        signupRequest.put("password", authRequest.getPassword());
        signupRequest.put("email", authRequest.getEmail());
        signupRequest.put("mobileNo", authRequest.getMobileNo());

        // Call user-service to register
        return restTemplate.postForObject(userServiceUrl + "/api/users/signup", signupRequest, UserDetailDTO.class);
    }

    @Transactional
    public AuthResponse signIn(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUserName());
        
        // Fetch user details again to get ID (or you could cast userDetails if you customized it)
        UserDetailDTO user = restTemplate.getForObject(userServiceUrl + "/api/users/details/" + authRequest.getUserName(), UserDetailDTO.class);
        
        if (user == null) {
             throw new RuntimeException("User not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        final String accessToken = jwtUtil.generateToken(userDetails, claims);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        UserSession userSession = sessionRepo.findByUserName(authRequest.getUserName())
                .orElse(new UserSession());
        
        userSession.setUserName(authRequest.getUserName());
        userSession.setAccessToken(accessToken);
        userSession.setRefreshToken(refreshToken);
        
        sessionRepo.save(userSession);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        String username = jwtUtil.extractUsername(requestRefreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(requestRefreshToken, userDetails)) {
            UserSession userSession = sessionRepo.findByRefreshToken(requestRefreshToken)
                    .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));

            // Fetch user details to get ID
            UserDetailDTO user = restTemplate.getForObject(userServiceUrl + "/api/users/details/" + username, UserDetailDTO.class);
             if (user == null) {
                 throw new RuntimeException("User not found");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());

            String newAccessToken = jwtUtil.generateToken(userDetails, claims);
            userSession.setAccessToken(newAccessToken);
            sessionRepo.save(userSession);

            return new AuthResponse(newAccessToken, requestRefreshToken);
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }

    public Boolean validateToken(String token) {
        return jwtUtil.validateTokenAndCheckDb(token);
    }
}
