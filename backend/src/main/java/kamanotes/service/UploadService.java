package kamanotes.service;

import kamanotes.model.base.ApiResponse;
import kamanotes.model.vo.upload.ImageVO;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    ApiResponse<ImageVO> uploadImage(MultipartFile file);
}


