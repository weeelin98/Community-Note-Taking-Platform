package kamanotes.service.impl;

import kamanotes.model.base.ApiResponse;
import kamanotes.model.vo.upload.ImageVO;
import kamanotes.service.FileService;
import kamanotes.service.UploadService;
import kamanotes.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    FileService fileService;

    @Override
    public ApiResponse<ImageVO> uploadImage(MultipartFile file) {
        String url = fileService.uploadImage(file);
        ImageVO imageVO = new ImageVO();
        imageVO.setUrl(url);
        return ApiResponseUtil.success("上传成功", imageVO);
    }
}


