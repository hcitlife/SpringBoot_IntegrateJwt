package com.hc.utils;

import com.hc.common.CustomException;
import com.hc.common.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtTokenUtil {

    /**
     * 解析jwt
     * @param jsonWebToken
     * @param base64Security
     * @return
     */
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (ExpiredJwtException  eje) {
            log.error("===== Token过期 =====", eje);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_EXPIRED);
        } catch (Exception e){
            log.error("===== token解析异常 =====", e);
            throw new CustomException(ResultCode.PERMISSION_TOKEN_INVALID);
        }
    }

    /**
     * 构建jwt
     * @param userId
     * @param username
     * @param role
     * @return
     */
    public static String createJWT(String userId, String username, String role) {
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JwtConst.secret);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //userId是重要信息，进行加密下
            String encryUserId = Base64Util.encode(userId);

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    // 可以将基本不重要的对象信息放到claims
                    .claim("role", role)
                    .claim("userId", encryUserId)
                    .setSubject(username)           // 代表这个JWT的主体，即它的所有人
                    .setIssuer(JwtConst.clientId)              // 代表这个JWT的签发主体
                    .setIssuedAt(new Date())        // 是一个时间戳，代表这个JWT的签发时间
                    .setAudience(JwtConst.name)          // 代表这个JWT的接收对象
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            long TTLMillis = JwtConst.expires;
            if (TTLMillis >= 0) {
                long expMillis = nowMillis + TTLMillis;
                Date exp = new Date(expMillis);
                builder.setExpiration(exp)  // 是一个时间戳，代表这个JWT的过期时间；
                        .setNotBefore(now); // 是一个时间戳，代表这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
            }

            //生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new CustomException(ResultCode.PERMISSION_SIGNATURE_ERROR);
        }
    }

    /**
     * 从token中获取用户名
     * @param token
     * @return
     */
    public static String getUsername(String token){
        return parseJWT(token, JwtConst.secret).getSubject();
    }

    /**
     * 从token中获取用户ID
     * @param token
     * @return
     */
    public static String getUserId(String token){
        String encryUserId = parseJWT(token, JwtConst.secret).get("userId").toString();
        return Base64Util.decode(encryUserId);
    }

    /**
     * 是否已过期
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) {
        return parseJWT(token, JwtConst.secret).getExpiration().before(new Date());
    }

    public static void main(String[] args) {
        String token = createJWT("1001", "zhangsan", "admin");
        System.out.println(token);
        Claims claims = parseJWT(token, JwtConst.secret);
        System.out.println("claims "+claims);
        System.out.println(claims.get("role"));
        System.out.println(" ****   " +claims.get("userId"));
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt());
        System.out.println("userid "+getUserId(token));
        System.out.println(getUsername(token));
        System.out.println(isExpiration(token));
    }
}
