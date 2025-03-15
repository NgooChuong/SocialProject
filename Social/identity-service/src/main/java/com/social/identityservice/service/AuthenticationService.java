package com.social.identityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.social.identityservice.configuration.CustomJwtDecoder;
import com.social.identityservice.dto.redis.RedisResponse;
import com.social.identityservice.dto.redis.StoreData;
import com.social.identityservice.dto.request.*;
import com.social.identityservice.dto.request.AuthenticationRequest;
import com.social.identityservice.dto.request.IntrospectRequest;
import com.social.identityservice.dto.request.message.GoogleMessage;
import com.social.identityservice.dto.response.AuthenticationResponse;
import com.social.identityservice.dto.response.IntrospectResponse;
import com.social.identityservice.entity.RefreshToken;
import com.social.identityservice.entity.User;
import com.social.identityservice.enums.StatusAccount;
import com.social.identityservice.exception.AppException;
import com.social.identityservice.exception.ErrorCode;
import com.social.identityservice.repository.RefreshTokenRepository;
import com.social.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.social.identityservice.repository.httpclient.UserUnauthenClient;
import com.social.identityservice.service.messageproducer.AuthenticationMessageProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    PasswordEncoder passwordEncoder;
    RedisService redisService;
    CustomJwtDecoder customJwtDecoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    private boolean isValidRefreshToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        Instant expiresAtInstant = Instant.ofEpochMilli(refreshTokenEntity.getExpiresAt());
        return !refreshTokenEntity.isRevoked()
                && expiresAtInstant.isAfter(Instant.now());
    }

    private boolean isValidToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return verified && expiryTime.after(new Date());
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("social.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim("role", user.getRole())
                .claim("userId", user.getUserId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(new Date());
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException, JsonProcessingException {
        log.info("RefreshToken: {}", request.getRefreshToken());
        var decodeToken = customJwtDecoder.decode(request.getToken());
        log.info("userId: {}", Optional.ofNullable(decodeToken.getClaim("userId")));
        var redisData = redisService.get(decodeToken.getClaim("userId"), StoreData.class);
        log.info("redisData: {}", redisData);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(request.getRefreshToken());

        Instant expiryInstant = Instant.ofEpochMilli(redisData.getExpiryTime());
        Instant currentInstant = Instant.ofEpochMilli(System.currentTimeMillis());
        var validRefreshToken =
                redisData.getRefreshToken().equals(request.getRefreshToken()) &&
                expiryInstant.isAfter(currentInstant) && !refreshTokenEntity.isRevoked();
        boolean isValid = validRefreshToken &&  isValidToken(request.getToken());

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // ứng voi TH la k có token (dang nhap) và
    public RedisResponse handleDataRedis(User user, String refreshTokenRequest) throws JsonProcessingException {
        var token = generateToken(user);
        log.info("refreshTokenRequest: {}", refreshTokenRequest);
        if (refreshTokenRequest != null) { // nhưng lan khac dang nhap
            var redisData = redisService.get(user.getUserId(), StoreData.class);
            if (isValidRefreshToken(refreshTokenRequest) && redisData != null
                    && redisData.getRefreshToken().equals(refreshTokenRequest)) { //
                log.info("redisData: {}", redisData);
                redisData.setToken(token);
                redisService.update(user.getUserId(), redisData); // cap nhat redis
                return RedisResponse.builder() // tra ve phia user
                        .token(token)
                        .refreshToken(redisData.getRefreshToken())
                        .build();
            }
        }
        // tao moi khi lan dau dang nhap
        var refreshToken = createRefreshToken(user.getUserId());
        StoreData newRedisData = StoreData.builder()
                .token(token)
                .refreshToken(refreshToken.getRefreshToken())
                .expiryTime(refreshToken.getExpiresAt()) // Thêm 7 ngày từ thời điểm hiện tại
                .build();
        redisService.save(user.getUserId(), newRedisData);
        return RedisResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getRefreshToken())
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws JsonProcessingException {
        User user = Optional.ofNullable(request.getGoogle_id())
                .map(googleId -> userRepository.findByGoogleId(googleId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)))
                .orElseGet(() -> userRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        log.info("Authenticated: {}", request);
        RedisResponse redisResponse = handleDataRedis(user, request.getRefresh_token());
        return AuthenticationResponse.builder()
                .token(redisResponse.getToken())
                .refreshToken(redisResponse.getRefreshToken())
                .authenticated(true)
                .build();
    }

//    public AuthenticationResponse authenticate(GoogleRequest request) {
//        // check email dưới db qlbdx
//        log.info("vo ham r");
//        log.info(request.getEmail());
//        String username = userUnauthenClent.GetUsername(request.getEmail());
//        log.info("username", username);
//        var user = userRepository.findByUsername(username).orElse(null);
//        log.info("Authenticated: {}", user);
//
//        if (user == null && username == null) {
//            var pass = passwordEncoder.encode("123456789");
//            user = User.builder().username(request.getEmail())
//                    .status(StatusAccount.ACTIVE.name())
//                    .role("USER")
//                    .password(pass)
//                    .build();
//            userRepository.save(user);
//
//            GoogleMessage googleMessage = GoogleMessage
//                    .builder()
//                    .email(request.getEmail())
//                    .password(pass)
//                    .avatar(request.getAvatar())
//                    .build();
//            authenticationMessageProducer.sendSynchronousGoogleMessage(googleMessage);
//        }
//        var token = generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(token)
//                .authenticated(true)
//                .build();
//    }

    public String RenewToken(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return generateToken(user);
        }
        return null;
    }
}
