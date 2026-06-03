package com.ndd.digitallibrary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity{
    @Column(nullable = false, unique = false)
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "tags")
    private List<Document> documents = new ArrayList<>();
}
