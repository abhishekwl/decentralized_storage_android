package com.netlify.abhishekwl.dcloud;

public class Post {

    private String authorId;
    private String authorName;
    private String authorImage;
    private String name;
    private String description;
    private String hash;
    private long size;
    private boolean isPrivate;

    public Post(String authorId, String authorName, String authorImage, String name, String description, String hash, long size) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.name = name;
        this.description = description;
        this.hash = hash;
        this.size = size;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
