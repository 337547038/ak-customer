package customer.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import customer.config.PassToken;
import customer.config.PermissionCheck;
import customer.utils.Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "图片验证码")
@RestController
@RequestMapping("/test")
public class TestController {

    @PassToken
    @PostMapping("/get")
    @PermissionCheck(value = {"permission1", "permission2"}, logical = PermissionCheck.Logical.AND)
    public ResponseEntity<String> getTest() {

        return ResponseEntity.ok("map");
    }


}