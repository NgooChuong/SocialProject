package com.social.identityservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.social.identityservice.dto.request.ApiResponse;
import com.social.identityservice.dto.request.AuthenticationRequest;
import com.social.identityservice.dto.request.IntrospectRequest;
import com.social.identityservice.dto.response.AuthenticationResponse;
import com.social.identityservice.dto.response.IntrospectResponse;
import com.social.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.social.identityservice.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws JsonProcessingException {
        log.info("Authentication pass: {}", request);
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

//    @PostMapping("/face/token")
//    ApiResponse<AuthenticationResponse> faceAuthenticate(){
//        var result = authenticationService.authenticate();
//        return ApiResponse.<AuthenticationResponse>builder()
//                .result(result)
//                .build();
//    }
//
//    @PostMapping("/google/token")
//    public ApiResponse<AuthenticationResponse>  GoogleAuthentication(@RequestBody GoogleRequest request) {
//        log.info("controller");
//        log.info(request.getEmail());
//        var result = authenticationService.authenticate(request);
//        return ApiResponse.<AuthenticationResponse>builder()
//                .result(result)
//                .build();
//    }


    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException, JsonProcessingException {
        log.info(request.toString());
        log.info(request.getToken());
        log.info(request.getRefreshToken());
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh") // trường hợp refresh token còn hạn nhưng token hết hạn
    ApiResponse<AuthenticationResponse> Refresh(@RequestBody AuthenticationRequest request) throws JsonProcessingException {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
