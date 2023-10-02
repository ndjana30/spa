package com.gestion.spa.rest.auth;


import com.gestion.spa.dto.AuthResponseDto;
import com.gestion.spa.dto.LoginDto;
import com.gestion.spa.dto.RegisterDto;
import com.gestion.spa.models.Role;
import com.gestion.spa.models.UserEntity;
import com.gestion.spa.repositories.RoleRepository;
import com.gestion.spa.repositories.UserRepository;
import com.gestion.spa.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;


    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator=jwtGenerator;
    }
    @PostMapping("employee/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken
                        (loginDto.getUsername(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Authentication usr = SecurityContextHolder.getContext().getAuthentication();

        String username=usr.getPrincipal().toString();
        String roles = usr.getAuthorities().toString();

        System.out.println("username is \t"+username+ "\t"+ "roles are \t"+roles);

        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token),HttpStatus.OK);
    }
    @PostMapping("employee/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto)
    {
        if(userRepository.existsByUsername(registerDto.getUsername()))
        {
            return new ResponseEntity<>("Username already taken", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role roles= roleRepository.findByName("EMPLOYEE").get();
        user.setRoles(Collections.singletonList(roles));
        userRepository.save(user);

        return new ResponseEntity<>("User registered success with Employee role",
                HttpStatus.OK);
    }
    @PostMapping("cashier/register")
    public ResponseEntity<String> cashierRegister(@RequestBody RegisterDto registerDto)
    {
        if(userRepository.existsByUsername(registerDto.getUsername()))
        {
            return new ResponseEntity<>("Username already taken", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role roles= roleRepository.findByName("CASHIER").get();
        user.setRoles(Collections.singletonList(roles));
        userRepository.save(user);

        return new ResponseEntity<>("User registered success with Cashier",
                HttpStatus.OK);
    }
    @GetMapping("connected/see")
    public String seeUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
