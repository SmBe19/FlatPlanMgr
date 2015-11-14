package com.smeanox.apps.flatplanmgr;

/**
 * A single page in the flat plan
 */
public class Page {
    private int start;
    private int length;
    private String title;
    private Author author;
    private Category category;
    private String fileFormat;
    private PageStatus status;

    public Page() {
    }

    public Page(int start, int length, String title, Author author, Category category, String fileFormat, PageStatus status) {
        this.start = start;
        this.length = length;
        this.title = title;
        this.author = author;
        this.category = category;
        this.fileFormat = fileFormat;
        this.status = status;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public PageStatus getStatus() {
        return status;
    }

    public void setStatus(PageStatus status) {
        this.status = status;
    }
}
