package customer.controller;

import customer.config.CustomException;
import customer.service.UploadFilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("system")
public class UploadFilesController {

    @Resource
    private UploadFilesService uploadFilesService;

    @Operation(summary ="文件上传")
    @Parameters({
            @Parameter(name = "file",description = "上传的文件")
    })
    @PostMapping("upload")
    public String uploadSingle(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException("请选择文件");
        }
        return  this.uploadFilesService.uploadSingleFile(file);
    }

    @Operation(summary ="从服务器删除上传文件")
    @Parameters({
            @Parameter(name = "path",description = "删除的文件路径")
    })
    @PostMapping("upload/delete")
    public Boolean delUploadFile(@RequestBody Map<String,String> params) {
        return  this.uploadFilesService.delUploadFile(params.get("path"));
    }
}
