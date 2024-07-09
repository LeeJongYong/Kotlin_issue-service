package com.fastcampus.issueservice.config

import com.fastcampus.issueservice.exception.UnauthorizedException
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration
class WebConfig(
    private val authUserHandlerArgumentResolver: AuthUserHandlerArgumentResolver,
) : WebMvcConfigurationSupport() {


    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.apply {
            add(authUserHandlerArgumentResolver)
        }
    }

    // web 정적 리소스에 자동 접근하기 위한 경로 설정
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")      // 모든 경로 : /**
            .addResourceLocations(
                "classpath:/META-INF/resources/",
                "classpath:/resources/",
                "classpath:/static/",
                "classpath:/public/",
            )
    }
}

@Component
class AuthUserHandlerArgumentResolver(
    @Value("\${auth.url}") val authUrl: String,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        AuthUser::class.java.isAssignableFrom(parameter.parameterType)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {

        // 헤더의 Authorization가 없을경우 인증 실패로 간주하여 exception발생
        val authHeader = webRequest.getHeader("Authorization") ?: throw UnauthorizedException()

        return runBlocking {
            WebClient.create()
                .get()
                .uri(authUrl)
                .header("Authorization", authHeader)    // Bearer adslfknlk1nlkdsamlkdmlkdmlq..
                .retrieve()
                .awaitBody<AuthUser>()
        }
//        return AuthUser(
//            userId = 1,
//            username = "테스트",
//        )
    }


}

data class AuthUser(
    @JsonProperty("id")         // JsonProperty annotation을 사용하면 자동으로 매핑된다.
    val userId: Long,
    val username: String,
    val email: String,
    val profileUrl: String? = null,
)

