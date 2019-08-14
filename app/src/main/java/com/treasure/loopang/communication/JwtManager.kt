package com.treasure.loopang.communication

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

class JwtManager {
    private val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private var jwt: JwtBuilder? = null
    private var decodedJwt: Jws<Claims>? = null

    init { jwt = Jwts.builder() }

    fun makeJwtNull() { jwt = null }
    fun makeDecodedJwtNull() { decodedJwt = null }

    fun makeHeader(key: String, value: String): JwtManager {
        jwt?.setHeaderParam(key, value)
        return this@JwtManager
    }

    fun makeRegisterdPayload(key: String, value: Any): JwtManager {
        if(value is String) {
            when(key) {
                "iss" -> { jwt?.setIssuer(value) }       //  토큰 발급자 [issuer]
                "sub" -> { jwt?.setSubject(value) }      //  토큰 제목 [subject]
                "aud" -> { jwt?.setAudience(value) }     //  토큰 대상자 [audience]
                "jti" -> { jwt?.setId(value) }           //  JWT 고유 식별자
                else -> { }
            }
        }
        else if(value is Date) {
            when(key) {
                "exp" -> { jwt?.setExpiration(value) }   //  토큰의 만료시간 [expiration]
                "nbf" -> { jwt?.setNotBefore(value) }    //  토큰의 활성날짜 [Not Before]
                "iat" -> { jwt?.setIssuedAt(value) }     //  토큰이 발급된 시간 [Age]
                else -> { }
            }
        }
        return this@JwtManager
    }

    fun makeCustomPayload(key: String, value: String): JwtManager {
        jwt?.claim(key, value)
        return this@JwtManager
    }

    fun encodeJws() = jwt?.signWith(key)?.compact()

    fun decodeJws(jws: String) { decodedJwt = Jwts.parser().setSigningKey(key).parseClaimsJws(jws) }
}