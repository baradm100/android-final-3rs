package com.colman.bar.admoni.a3rs.providers;

import com.colman.bar.admoni.a3rs.models.Post;

public class PostIdPair {
    private final String id;
    private final Post post;

    public PostIdPair(String id, Post post) {
        this.id = id;
        this.post = post;
    }

    public String getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }
}
