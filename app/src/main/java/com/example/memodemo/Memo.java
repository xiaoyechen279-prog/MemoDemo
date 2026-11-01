package com.example.memodemo;

public class Memo {
    private long id;
    private String title;
    private String content;
    private boolean isImportant; // 新增：是否重要

    public Memo(long id, String title, String content, boolean isImportant) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
    }

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { isImportant = important; }
}
