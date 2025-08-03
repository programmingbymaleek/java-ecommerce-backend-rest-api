package com.statless_api_setup.stateless_api.login;
import com.statless_api_setup.stateless_api.JWTSecurityConfiguration.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;

@RestController
public class LoginController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    public LoginController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }
    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest response){
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(response.getEmail(),response.getPassword()));
        String token = jwtService.issueAccessToken(
                auth.getName(), auth.getAuthorities(), Duration.ofMinutes(15)
        );
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", 15 * 60));
    }
}

record LoginResponse(String access_token, String token_type, long expires_in) {}
