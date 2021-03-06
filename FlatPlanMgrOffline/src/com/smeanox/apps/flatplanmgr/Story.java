package com.smeanox.apps.flatplanmgr;

/**
 * A single page in the flat plan
 */
public class Story {
    private int start;
    private int length;
    private String title;
    private Author author;
    private Category category;
    private String fileFormat;
    private StoryStatus status;

    public Story() {
        start = 0;
        length = 1;
        title = "";
        fileFormat = "";
        status = StoryStatus.Missing;
    }

    public Story(int start, int length, String title, Author author, Category category, String fileFormat, StoryStatus status) {
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

    public StoryStatus getStatus() {
        return status;
    }

    public void setStatus(StoryStatus status) {
        this.status = status;
    }

    public String getFileName(){
        return String.format("%02d_%s__%s.%s", start, title.replace(" ", "_"), author.getFullName().replace(" ", "_"), fileFormat);
    }
}
