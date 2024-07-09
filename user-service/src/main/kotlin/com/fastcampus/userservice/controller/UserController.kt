package com.fastcampus.userservice.controller

import com.fastcampus.userservice.model.*
import com.fastcampus.userservice.service.UserService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    // 회원가입
    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SignUpRequest) {       // 입력받은 json 데이터를 활용
        userService.signUp(request)
    }

    // 로그인
    @PostMapping("/signin")
    suspend fun signIn(@RequestBody signInRequest: SignInRequest) : SignInResponse {        // 입력받은 json 데이터를 활용
        return userService.signIn(signInRequest)
    }

    // 로그아웃
    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken token : String) {     // 생성 annotation을 활용하여 핸들러에서 캐치
        userService.logout(token)
    }

    // 내 정보 조회
    @GetMapping("/me")
    suspend fun get(
        @AuthToken token: String
    ): MeResponse {
        return MeResponse(userService.getByToken(token))
    }

    @GetMapping("/{userId}/username")
    suspend fun getUsername(@PathVariable userId: Long) : Map<String, String> {
        return mapOf("repoter" to userService.get(userId).username)
    }

    @PostMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun edit(
        @PathVariable id: Long,
        @ModelAttribute request: UserEditRequest,
        @AuthToken token: String,
        @RequestPart("profileUrl") filePart: FilePart,
    ){

        val orgFilename = filePart.filename()
        var filename: String? = null
        if(orgFilename.isNotEmpty()){
            // .jpg
            val ext = orgFilename.substring(orgFilename.lastIndexOf(".") + 1)
            filename = "${id}.${ext}"

            val file = File(ClassPathResource("/images/").file, filename)

            filePart.transferTo(file).awaitSingleOrNull()
        }

        userService.edit(token, request.username, filename)
    }

}