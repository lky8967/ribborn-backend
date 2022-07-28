package com.spring.ribborn.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    // secretKey 와 같은 민감정보는 숨기는 것이 좋다.
    //정석대로 하려면 byte수에 맞게 디코딩해서 시크릿키를 구성해야하는데


//    @Value("K7kjHSF345h345S86F3A2erGB98iWIad")
////    private String secretKey;
//
//    public static String secretKey;
//
//    @Value("${jwt.secret-key}")
//    private void setKey(String secret) {
//        JwtTokenProvider.secretKey = secret;
//    }

    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 5분 설정 (1000L = 1초, 1000L * 60 = 1분)
    private static final long TOKEN_VALID_TIME = 1000L * 60 * 1440;

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey 를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String userPk , String nickname , Long userid) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위

        Map<String , Object> claimsMap = new HashMap<String , Object>();
            claimsMap.put("username" ,userPk);
            claimsMap.put("nickname" , nickname);
            claimsMap.put("userid" , userid);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claimsMap)
//                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
//    public String getUserPk(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getNickName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("nickname", String.class);
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public static String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    public final String HEADER_PREFIX = "Bearer ";

//    public String extract(String header, HttpServletRequest request) throws IOException {
//
//        if (header == null || header.equals("") || header.length() < HEADER_PREFIX.length()) {
//            System.out.println("error request : " + request.getRequestURI());
//            throw new NoSuchElementException("올바른 JWT 정보가 아닙니다.");
//        }
//
//        return header.substring(
//                HEADER_PREFIX.length(),
//                header.length()
//        );
//    }

    public  String extract(String header) {
        if (header == null || header.equals("") || header.length() < HEADER_PREFIX.length()) {
            throw new NoSuchElementException("올바른 JWT 정보가 아닙니다.");
        }

        return header.substring(
                HEADER_PREFIX.length(),
                header.length()
        );
    }


//    public boolean validateToken(String authToken) throws JwtException {
//
//        try {
//            System.out.println("Secret Key : " + JwtTokenUtils.JWT_SECRET);
//            System.out.println("JwtTokenUtils.JWT_SECRET.getBytes : " + Arrays.toString(JwtTokenUtils.JWT_SECRET.getBytes()));
//            Jwts.parserBuilder()
//                    .setSigningKey(JwtTokenUtils.JWT_SECRET.getBytes())
//                    .build()
//                    .parseClaimsJws(authToken);
//            return true;
//        } catch (JwtException e) {
//            System.out.println("토큰 검증 실패!! 토큰(" + authToken + ")");
//            e.printStackTrace();
//        }
//        return false;
//    }

}