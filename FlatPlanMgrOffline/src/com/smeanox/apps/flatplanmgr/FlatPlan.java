package com.smeanox.apps.flatplanmgr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * A flat plan
 */
public class FlatPlan {
    private ObservableList<Story> stories;
    private ObservableList<Author> authors;
    private ObservableList<Category> categories;
    private File file;

    public FlatPlan() {
        stories = FXCollections.observableArrayList();
        authors = FXCollections.observableArrayList();
        categories = FXCollections.observableArrayList();
    }

    /**
     * Create a new FlatPlan from a file
     * @param file the file
     */
    public FlatPlan(File file) throws IOException {
        this();

        createFromFile(file);
    }

    public ObservableList<Story> getStories() {
        return stories;
    }

    public ObservableList<Author> getAuthors() {
        return authors;
    }

    public ObservableList<Category> getCategories() {
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
    public static File createAuthorsFile(File file){
        String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        return new File(file.getParentFile(), name + ".authors" + ext);
    }

    /**
     * Create a new FlatPlan from a file
     * @param file the file
     */
    private void createFromFile(File file) throws IOException {
        if(!file.exists()){
            throw new IOException("File doesn't exist: " + file.getAbsolutePath());
        }

        this.file = file;

        try {
            File authorFile = createAuthorsFile(file);

            if(authorFile.exists()) {
                Files.lines(authorFile.toPath(), StandardCharsets.UTF_8).forEach(s -> {
                    String[] partsA = s.split(";");
                    String[] parts = new String[] {"", "", "", ""};
                    System.arraycopy(partsA, 0, parts, 0, partsA.length);
                    if (getAuthorByName(parts[0] + " " + parts[1]) == null) {
                        authors.add(new Author(parts[0], parts[1], parts[2], parts[3]));
                    }
                });
            }

            Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(s -> {
                if(s.trim().isEmpty()){
                    return;
                }
                String[] parts = s.split(";");
                if(parts.length < 7){
                    return;
                }
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
                stories.add(new Story(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2], author, category, parts[5], StoryStatus.values()[Integer.parseInt(parts[6])-1]));
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private String joinValues(String sep, Object ... vals){
        if(vals.length == 0){
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(vals[0]);
        for(int i = 1; i < vals.length; i++){
            sb.append(sep);
            sb.append(vals[i]);
        }
        return sb.toString();
    }

    /**
     * Save the flat plan to the given file
     * @param file the file
     * @param isCopy whether this is a copy (and thus the file path shouldn't be updated)
     */
    public void save(File file, boolean isCopy){
        if(!isCopy) {
            this.file = file;
        }

        File authorFile = createAuthorsFile(file);

        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(authorFile), StandardCharsets.UTF_8));
            for(Author author : authors){
                writer.println(joinValues(";", author.getFirstName(), author.getLastName(), author.getRole(), author.getMailAddress()));
            }
            writer.close();
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            for(Story story : stories){
                writer.println(joinValues(";", story.getStart(), story.getLength(), story.getTitle(), story.getAuthor().getFullName(),
                        story.getCategory().getName(), story.getFileFormat(), story.getStatus().ordinal() + 1));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

	/**
	 * GO through the given folder and mark all found files
     * @param folder the folder to check
     * @return log of the status update
     */
    public String statusWithFolder(File folder){
        if(!folder.isDirectory()) {
            return "Directory " + folder + " not found";
        }
        StringBuilder log = new StringBuilder();
        for(Story story : stories){
            File file = new File(folder, story.getFileName());
            if(file.exists()){
                if(story.getStatus() == StoryStatus.Missing){
                    story.setStatus(StoryStatus.Received);
                    log.append(file);
                    log.append(" found\n");
                }
            } else {
                if(story.getStatus() != StoryStatus.Missing){
                    story.setStatus(StoryStatus.Missing);
                    log.append(file).append(" not found\n");
                }
            }
        }
        return log.toString();
    }

	/**
     * Go through the given folder try to match the stories with files
     * @param folder the folder to check
     */
    public String matchWithFolder(File folder, matchResponder responder){
        if(!folder.isDirectory()){
            return "Directory " + folder + " not found";
        }
        StringBuilder log = new StringBuilder();
        List<File> files = Arrays.asList(folder.listFiles());
        for(Story story : stories){
            File file = new File(folder, story.getFileName());
            Map<File, Integer> levenshteinCache = new HashMap<>();
            for(File afile : files){
                levenshteinCache.put(afile, levenshtein(splitFilename(file.getName())[0], splitFilename(afile.getName())[0]));
            }
            if(!file.exists()){
                File finalFile = file;
                files.sort((o1, o2) -> {
                    int diff = levenshteinCache.get(o1) - levenshteinCache.get(o2);
                    if (diff == 0){
                        return levenshtein(finalFile.getName(), o1.getName()) - levenshtein(finalFile.getName(), o2.getName());
                    }
                    return diff;
                });
                File choice = responder.match(story, files);
                if(choice == null){
                    log.append("No match for file ").append(file);
                } else {
                    String[] filesplit = splitFilename(file.getName());
                    String[] choicesplit = splitFilename(choice.getName());
                    story.setFileFormat(choicesplit[1]);
                    file = new File(file.getParentFile(), filesplit[0] + "." + choicesplit[1]);
                    boolean success = choice.renameTo(file);
                    log.append("Match file ").append(choice).append(" to " ).append(file);
                    if(!success){
                        log.append("could not rename file");
                    }
                }
                log.append("\n");
            }
        }
        return log.toString();
    }

    private String[] splitFilename(String filename){
        int lastPoint = filename.lastIndexOf('.');
        if(lastPoint < 0){
            return new String[]{filename, ""};
        }
        return new String[]{filename.substring(0, lastPoint), filename.substring(lastPoint + 1)};
    }

	/**
     * Calculate Levenshtein distance between two strings
     * @param a first string
     * @param b second string
     * @return distance
     */
    private int levenshtein(String a, String b){
        if(a.length() == 0){
            return b.length();
        }
        if(b.length() == 0){
            return a.length();
        }

        int m[][] = new int[a.length()+1][b.length()+1];
        for (int i = 0; i <= a.length(); i++) {
            m[i][0] = i;
        }
        for (int i = 0; i <= b.length(); i++) {
            m[0][i] = i;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                m[i][j] = a.charAt(i-1) == b.charAt(j-1)
                        ? m[i-1][j-1]
                        : Math.min(m[i-1][j-1] + 1, Math.min(m[i][j-1] + 1, m[i-1][j] + 1));
            }
        }
        return m[a.length()][b.length()];
    }

	/**
	 * Choose the best fitting file from a list sorted by relevance
     */
    public interface matchResponder{
        File match(Story story, List<File> files);
    }
}
