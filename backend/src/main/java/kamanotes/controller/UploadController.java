package kamanotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kamanotes.model.base.ApiResponse;
import kamanotes.model.vo.upload.ImageVO;
import kamanotes.service.UploadService;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload/image")
    public ApiResponse<ImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadImage(file);
    }
}


