package com.fastcampus.userservice.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.fastcampus.userservice.config.JWTProperties
import com.fastcampus.userservice.domain.entity.User
import com.fastcampus.userservice.model.SignUpRequest
import com.fastcampus.userservice.domain.repository.UserRepository
import com.fastcampus.userservice.exception.InvalidJwtTokenException
import com.fastcampus.userservice.exception.PasswordNotMatchedException
import com.fastcampus.userservice.exception.UserExistsException
import com.fastcampus.userservice.exception.UserNotFoundException
import com.fastcampus.userservice.model.SignInRequest
import com.fastcampus.userservice.model.SignInResponse
import com.fastcampus.userservice.utils.BCryptUtils
import com.fastcampus.userservice.utils.JWTClaim
import com.fastcampus.userservice.utils.JWTUtils
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtProperties: JWTProperties,
    private val cacheManager: CoroutineCasheManager<User>,
)
{

    private val logger = KotlinLogging.logger{}

    companion object {
        private val CACHE_TTL = Duration.ofMinutes(1)
    }

    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest){
            userRepository.findByEmail(email)?.let{
                throw UserExistsException()
            }
            logger.debug { "JY Debug 1" }
            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username = username,
            )
            logger.debug { "JY Debug 2" }
            userRepository.save(user)
        }
    }

    suspend fun signIn(signInRequest: SignInRequest) : SignInResponse {
        // SignInResponse를 리턴
        // userRepository에서 email을 기준으로 고객을 조회하고 없을경우 UserNotFoundException 발생
        return with(userRepository.findByEmail(signInRequest.email) ?: throw UserNotFoundException()){
            // 고객이 있을 경우 BCrptUtils를 통해 비밀번호를 검증
            val verified = BCryptUtils.verify(signInRequest.password, password)
            // 검증에 실패할 경우 PasswordNotMatchedException 발생
            if(!verified){
                throw PasswordNotMatchedException()
            }
            // 검증까지 성공한 경우 JWTClaim 객체 생성
            val jwtClaim = JWTClaim(
                userId = id!!,
                email = email,
                profileUrl = profileUrl,
                username = username
            )

            // 생성한 JWTClaim과 resource에 있는 jwtProperties를 가지고 token 생성
            val token = JWTUtils.createToken(jwtClaim, jwtProperties)

            cacheManager.awaitPut(key = token, value = this, ttl = CACHE_TTL)

            // 리턴할 SignInResponse에 email, username, token 정보를 할당
            SignInResponse(
                email = email,
                username = username,
                token = token
            )
        }
    }

    suspend fun logout(token: String){
        cacheManager.awaitEvict(token)
    }

    suspend fun getByToken(token: String): User {
        val cachedUser = cacheManager.awaitGetOrPut(key = token, ttl = CACHE_TTL)
        // awaitGetOrPut function 동작
        // 인자값 token으로 cache가 존재하는 경우는 아래 람다식이 동작하지 않고 기존 cache 반환
        // cache가 존재하지 않을 경우 아래 람다식 동작하여 cache 생성
        {
            // 캐시가 유효하지 않은 경우 동작
            val decodedJWT: DecodedJWT = JWTUtils.decode(token, jwtProperties.secret, jwtProperties.issuer)

            val userId: Long = decodedJWT.claims["userId"]?.asLong() ?: throw InvalidJwtTokenException()
            get(userId)
        }
        return cachedUser
    }

    suspend fun get(userId: Long): User {
        return userRepository.findById(userId) ?: throw UserNotFoundException()
    }
}