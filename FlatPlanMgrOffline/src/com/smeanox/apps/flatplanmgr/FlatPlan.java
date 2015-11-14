package com.smeanox.apps.flatplanmgr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * A flat plan
 */
public class FlatPlan {
    private ArrayList<Page> pages;
    private ArrayList<Author> authors;
    private ArrayList<Category> categories;
    private File file;

    public FlatPlan() {
        pages = new ArrayList<>();
        authors = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Create a new FlatPlan from a file
     * @param file the file
     */
    public FlatPlan(File file){
        this();

        createFromFile(file);
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public File getFile() {
        return file;
    }

    /**
     * Returns the author with the given name or null if it doesn't exist
     * @param name the name of the author
     * @return the author
     */
    public Author getAuthorByName(String name){
        for(Author author : authors){
            if(author.getFullName().toLowerCase().equals(name.toLowerCase())){
                return author;
            }
        }
        return null;
    }

    /**
     * Returns the category with the given name or null if it doesn't exist
     * @param name the name of the category
     * @return the category
     */
    public Category getCategoryByName(String name){
        for(Category category : categories){
            if(category.getName().toLowerCase().equals(name.toLowerCase())){
                return category;
            }
        }
        return null;
    }

    /**
     * Create the file of the corresponding authors list
     * @param file the flat plan file
     * @return the file with the authors
     */
    private File createAuthorsFile(File file){
        String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        return new File(file.getParentFile(), name + ".authors" + ext);
    }

    /**
     * Create a new FlatPlan from a file
     * @param file the file
     */
    private void createFromFile(File file){
        this.file = file;

        try {
            File authorFile = createAuthorsFile(file);

            if(authorFile.exists()) {
                Files.lines(authorFile.toPath()).forEach(s -> {
                    String[] parts = s.split(";");
                    if (getAuthorByName(parts[0] + " " + parts[1]) == null) {
                        authors.add(new Author(parts[0], parts[1], parts[2], parts[3]));
                    }
                });
            }

            Files.lines(file.toPath()).forEach(s -> {
                String[] parts = s.split(";");
                Author author = getAuthorByName(parts[3]);
                if(author == null){
                    author = new Author(parts[3].substring(0, Math.max(0, parts[3].lastIndexOf(' '))), parts[3].substring(parts[3].lastIndexOf(' ') + 1), "", "");
                    authors.add(author);
                }
                Category category = getCategoryByName(parts[4]);
                if(category == null){
                    category = new Category(parts[4]);
                    categories.add(category);
                }
                pages.add(new Page(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2], author, category, parts[5], PageStatus.values()[Integer.parseInt(parts[6])-1]));
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    /**
     * Save the flat plan to the given file
     * @param file the file
     */
    public void save(File file){
        this.file = file;
    }
}
