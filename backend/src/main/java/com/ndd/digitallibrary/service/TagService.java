package com.ndd.digitallibrary.service;

import com.github.slugify.Slugify;
import com.ndd.digitallibrary.dto.request.TagRequest;
import com.ndd.digitallibrary.dto.response.TagResponse;
import com.ndd.digitallibrary.entity.Tag;
import com.ndd.digitallibrary.exception.AppException;
import com.ndd.digitallibrary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final Slugify slugify = Slugify.builder()
            .customReplacement("đ", "d")
            .customReplacement("Đ", "D")
            .build();

    @Transactional
    public TagResponse createTag(TagRequest request){

        String generatedSlug = slugify.slugify(request.getName());

        if (tagRepository.findBySlug(generatedSlug).isPresent()){
            throw new AppException(HttpStatus.CONFLICT, "Thẻ này đã tồn tại");
        }

        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(generatedSlug)
                .build();

        tag = tagRepository.save(tag);

        return TagResponse.fromEntity(tag);
    }

    @Transactional
    public List<Tag> findOrCreateTag(List<String> tagNames){

        List<Tag> tags = new ArrayList<>();

        for(String name : tagNames){
            String generatedSlug = slugify.slugify(name);
            Tag tag = tagRepository.findBySlug(generatedSlug)
                    .orElseGet(() -> {
                        Tag newTag = Tag.builder()
                                .name(name)
                                .slug(generatedSlug)
                                .build();
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }

    @Transactional
    public TagResponse updateTag(TagRequest request, Long id){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Thẻ này không tồn tại"));

        String generatedSlug = slugify.slugify(request.getName());

        if(!tag.getSlug().equals(generatedSlug) && tagRepository.findBySlug(generatedSlug).isPresent()){
            throw new AppException(HttpStatus.CONFLICT, "Tên thẻ bị trùng");
        }

        tag.setSlug(generatedSlug);
        tag.setName(request.getName());
        tag = tagRepository.save(tag);

        return TagResponse.fromEntity(tag);
    }

    @Transactional
    public void deleteTag(Long id){

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Thẻ này không tồn tại"));

        tagRepository.delete(tag);
    }

    public List<TagResponse> getAllTag(){
        return tagRepository.findAll().stream().map(TagResponse::fromEntity).toList();
    }

    public TagResponse getTagById(Long id){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Thẻ này không tồn tại"));

        return TagResponse.fromEntity(tag);
    }

}
