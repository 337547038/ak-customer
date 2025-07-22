package customer.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFilesService {

    String uploadSingleFile(MultipartFile file);
    Boolean delUploadFile(String path);
}
