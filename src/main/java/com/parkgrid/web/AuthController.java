package com.parkgrid.web;
import com.parkgrid.domain.User; import com.parkgrid.repo.UserRepository; import com.parkgrid.security.JwtService;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final JwtService jwt; private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
 public AuthController(UserRepository u,JwtService j){users=u;jwt=j;}
 public record Register(@Email @NotBlank String email,@Size(min=8) String password,String role){}
 public record Login(@Email @NotBlank String email,@NotBlank String password){}
 @PostMapping("/register") public Map<String,Object> register(@Valid @RequestBody Register r){if(users.findByEmail(r.email()).isPresent())throw new IllegalArgumentException("Email already registered"); User u=new User(r.email(),encoder.encode(r.password()),"ADMIN".equalsIgnoreCase(r.role())?User.Role.ADMIN:User.Role.USER);users.save(u);return Map.of("token",jwt.issue(u.getEmail(),u.getRole().name()),"role",u.getRole());}
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody Login r){User u=users.findByEmail(r.email()).orElseThrow(()->new IllegalArgumentException("Invalid credentials"));if(!encoder.matches(r.password(),u.getPasswordHash()))throw new IllegalArgumentException("Invalid credentials");return Map.of("token",jwt.issue(u.getEmail(),u.getRole().name()),"role",u.getRole());}
}
