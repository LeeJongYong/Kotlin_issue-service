package com.fastcampus.userservice.service

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class CoroutineCasheManager<T> {

    private val localCache = ConcurrentHashMap<String, CacheWrapper<T>>()

    suspend fun awaitPut(key: String, value: T, ttl: Duration){
        localCache[key] = CacheWrapper(value, Instant.now().plusMillis(ttl.toMillis()))
    }

    suspend fun awaitEvict(key: String) {
        localCache.remove(key)
    }

    suspend fun awaitGetOrPut(
        key: String,
        ttl: Duration?= Duration.ofMinutes(5),
        supplier: suspend () -> T,
    ) : T {
        // key로 cache를 조회해서 존재할 경우 cache 반환
        val now = Instant.now()
        val cacheWrapper = localCache[key]

        // 존재하지 않을 경우 인자로 넘어온 suspend 람다 함수를 통해 결과를 cache에 저장하고 cache 반환
        val cached = if (cacheWrapper == null) {
            CacheWrapper(cached = supplier(), ttl = now.plusMillis(ttl!!.toMillis())).also {
                localCache[key] = it
            }
        }
        else if (now.isAfter(cacheWrapper.ttl)) {
            // cache ttl이 지난 경우 cache를 재생성
            localCache.remove(key)
            CacheWrapper(cached = supplier(), ttl = now.plusMillis(ttl!!.toMillis())).also {
                localCache[key] = it
            }
        }
        else {
            cacheWrapper
        }

        checkNotNull(cached.cached)
        return cached.cached
    }

    data class CacheWrapper<T> (val cached: T, val ttl: Instant)
}