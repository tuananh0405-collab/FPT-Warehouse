package com.hieuphung.SpringSecurityJWT.service;

import com.hieuphung.SpringSecurityJWT.dto.ReqRes;
import com.hieuphung.SpringSecurityJWT.entity.OurUsers;
import com.hieuphung.SpringSecurityJWT.repository.OurUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService  {

    @Autowired
    private OurUsersRepository ourUsersRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole(registrationRequest.getRole());
            OurUsers ourUsersResult = ourUsersRepository.save(ourUsers);
            if (ourUsersResult!=null && ourUsersResult.getId()>0) {
                reqRes.setOurUsers(ourUsersResult);
                reqRes.setMessage("User saved successfully");
                reqRes.setStatusCode(200);
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            var user = ourUsersRepository.findByEmail(signinRequest.getEmail()).orElseThrow();
            System.out.println("User is: " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("24h");
            reqRes.setMessage("Successfully signed in");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRegister) {
        ReqRes reqRes = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenRegister.getToken());
        OurUsers users = ourUsersRepository.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRegister.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshTokenRegister.getToken());
            reqRes.setExpirationTime("24h");
            reqRes.setMessage("Successfully refreshed in");
        }
        reqRes.setStatusCode(500);
        return reqRes;
    }

}
