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
    Timestamp created_at;
    Timestamp updated_at;

    @PrePersist
    public void prePersist() {
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }
}
