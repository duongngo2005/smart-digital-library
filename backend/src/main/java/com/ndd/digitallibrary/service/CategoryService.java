package com.ndd.digitallibrary.service;

import com.github.slugify.Slugify;
import com.ndd.digitallibrary.dto.request.CreateCategoryRequest;
import com.ndd.digitallibrary.dto.request.UpdateCategoryRequest;
import com.ndd.digitallibrary.dto.response.CategoryResponse;
import com.ndd.digitallibrary.entity.Category;
import com.ndd.digitallibrary.exception.AppException;
import com.ndd.digitallibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Slugify slugify = Slugify.builder()
            .customReplacement("đ", "d")
            .customReplacement("Đ", "D")
            .build();

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request){

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(slugify.slugify(request.getName()))
                .build();

        if (request.getParent() != null){
            category.setParent(categoryRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục cha")));
        }

        category = categoryRepository.save(category);

        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UpdateCategoryRequest request, Long id){

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không có danh mục này"));

        if(request.getName() != null){
            category.setName(request.getName());
            category.setSlug(slugify.slugify(request.getName()));
        }

        if (request.getParentId() != null){

            if(request.getParentId().equals(id)){
                throw new AppException(HttpStatus.BAD_REQUEST, "Một danh mục không thể tự nhận mình làm cha");
            }

            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục cha")));
        }

        if (request.getDescription() != null){
            category.setDescription(request.getDescription());
        }

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public void deleteCategory(Long id){

        Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"));

        if (category.getChildren() != null && !category.getChildren().isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST, "Không thể xóa danh mục này vì vẫn đang còn danh mục con");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findByParentIsNull().stream().map(CategoryResponse::fromEntity).toList();
    }

    @Transactional
    public CategoryResponse getCategoryById(Long id){

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục này"));

        return CategoryResponse.fromEntity(category);
    }
}
