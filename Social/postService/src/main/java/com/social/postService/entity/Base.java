package com.social.postService.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
// bring attribute from superclass to baseclass
@MappedSuperclass // suitable when extends from many other, unrelated genres.
// EX: post, like, cmt extend base
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // suitable when extends from one category.
// EX: user1, user2 extend user
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class   Base {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "created_at")
    Timestamp createdAt;
    @Column(name = "update_at")
    Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}
