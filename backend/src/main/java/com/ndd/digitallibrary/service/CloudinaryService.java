package com.ndd.digitallibrary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ndd.digitallibrary.dto.response.CloudinaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryResponse uploadCover(MultipartFile file){
        try{
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "digital-library/covers",
                            "resource_type", "image",
                            "allowed_formats", "jpg, jpeg, png, webp"
                    )
            );

            return new CloudinaryResponse(
                    result.get("secure_url").toString(),
                    result.get("public_id").toString()
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh bìa thất bại " + e.getMessage());
        }
    }

    public CloudinaryResponse uploadDocument(MultipartFile file){
        try{
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "digital-library/documents",
                            "resource_type", "auto",
                            "allowed_formats", "pdf"
                    )
            );

            return new CloudinaryResponse(
                    result.get("secure_url").toString(),
                    result.get("public_id").toString()
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload tài liệu thất bại " + e.getMessage());
        }
    }

    public void deleteFile(String publicId, String resourceType){
        try{
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", resourceType
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Xóa file thất bại " + e.getMessage());
        }
    }

}
