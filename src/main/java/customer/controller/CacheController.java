package customer.controller;

import customer.config.PassToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @PassToken
    @PostMapping("/clearAll")
    public ResponseEntity<Boolean> clearCache() {
        // 获取所有缓存名称
        cacheManager.getCacheNames().forEach(cacheName -> {
            // 清空每个缓存
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            //// 直接清空指定缓存
            //  cacheManager.getCache("a").clear();
        });
        return ResponseEntity.ok(true);
    }
}
