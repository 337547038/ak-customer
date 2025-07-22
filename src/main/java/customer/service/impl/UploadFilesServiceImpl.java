package customer.service.impl;

import customer.config.CustomException;
import customer.service.UploadFilesService;
import customer.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service("uploadFilesService")
public class UploadFilesServiceImpl implements UploadFilesService {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    private final String uploadDir = "upload";

    private File uploadDir() {
        String dir = System.getProperty("user.dir");
        return new File(dir + File.separator + uploadDir);
    }

    private static final String[] ALLOW_FILE_TYPES = {"jpg", "jpeg", "png"};

    // 生成保存的文件名
    private String getFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return Utils.getCurrentUserId() + "-" + UUID.randomUUID().toString() + fileExtension;
    }

    @Override
    public String uploadSingleFile(MultipartFile file) {
        try {
            //上传文件格式校验
            String contentType = Objects.requireNonNull(file.getContentType()).toLowerCase();
            boolean allowType = false;
            for (String allowFileType : ALLOW_FILE_TYPES) {
                if (contentType.contains(allowFileType)) {
                    allowType = true;
                    break;
                }
            }

            if (!allowType) {
                throw new CustomException(0, "请上传正确的文件格式");
            }
            // 生成唯一文件名
            String newFilename = getFileName(file);


            File uploadPath = uploadDir();
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File dest = new File(uploadPath, newFilename);
            file.transferTo(dest);

            //return  dest.getAbsolutePath();
            return contextPath + "/" + uploadDir + "/" + newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(0, "文件上传失败: " + e.getMessage());
            //return "文件上传失败: " + e.getMessage();
        }
    }

    public Boolean delUploadFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                return false;
            }
            String file = filePath.replace(contextPath + "/" + uploadDir + "/", "");
            File delPathDir = uploadDir();
            File delPath = new File(delPathDir, file);
            Path path = Paths.get(delPath.getPath());
            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            } else {
                System.out.println("文件不存在");
                return false; // 文件不存在
            }
        } catch (SecurityException e) {
            System.out.println("没有删除权限");
            return false;// 没有删除权限
        } catch (Exception e) {
            System.out.println("删除过程中发生错误");
            return false; // 删除过程中发生错误
        }
    }
}
