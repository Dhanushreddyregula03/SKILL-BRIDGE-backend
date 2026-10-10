package com.skillbridge.service;

import com.skillbridge.config.JwtTokenProvider;
import com.skillbridge.dto.AuthRequest;
import com.skillbridge.dto.AuthResponse;
import com.skillbridge.dto.RegisterRequest;
import com.skillbridge.model.RecruiterProfile;
import com.skillbridge.model.Role;
import com.skillbridge.model.StudentProfile;
import com.skillbridge.model.User;
import com.skillbridge.repository.RecruiterProfileRepository;
import com.skillbridge.repository.StudentProfileRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       RecruiterProfileRepository recruiterProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = tokenProvider.generateToken(user);
            return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT);
        user.setPhone(request.getPhone());

        user = userRepository.save(user);

        if (user.getRole() == Role.ROLE_STUDENT) {
            StudentProfile profile = new StudentProfile(user);
            profile.setUniversity(request.getUniversity() != null ? request.getUniversity() : "Tech University");
            profile.setDegree(request.getDegree() != null ? request.getDegree() : "B.Tech Computer Science");
            profile.setHeadline("Aspiring Software & AI Engineer | Ready for Impact");
            profile.setBio("Passionate learner with experience building web apps and eager to apply skills in a high-impact internship.");
            studentProfileRepository.save(profile);
        } else if (user.getRole() == Role.ROLE_EMPLOYER) {
            RecruiterProfile profile = new RecruiterProfile(user, 
                    request.getCompanyName() != null ? request.getCompanyName() : "InnovateTech Inc.");
            profile.setIndustry("Technology & Software");
            profile.setLocation("Remote / Hybrid");
            profile.setCompanyDescription("Building next-generation digital products and hiring top student talent.");
            recruiterProfileRepository.save(profile);
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }
}
