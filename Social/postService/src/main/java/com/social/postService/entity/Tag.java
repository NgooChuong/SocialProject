package com.social.postService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends Base{

    @Column(nullable = false, unique = true, length = 50)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(unique = true, length = 100)
    String slug;

    @Column(columnDefinition = "INT DEFAULT 0")
    int count;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    boolean isActive;

    @ToString.Exclude // ⚠️ Tránh vòng lặp vô hạn
    @ManyToMany(mappedBy = "tags")
    List<Post> posts;
}