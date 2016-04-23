package com.example.jim.twowayrecipes;

public class Recipe {

    private String title;
    private String publisher;
    private String publisher_url;
    private String image_url;
    private String source_url;
    private String f2f_url;
    private long social_rank;
    private String ingredients;

    public Recipe() {
    }

    public Recipe(String title, String publisher, String publisher_url,
                  String image_url, String source_url, String f2f_url,
                  long social_rank, String ingredients) {
        this.title = title;
        this.publisher = publisher;
        this.publisher_url = publisher_url;
        this.image_url = image_url;
        this.source_url = source_url;
        this.f2f_url = f2f_url;
        this.social_rank = social_rank;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher_url() {
        return publisher_url;
    }

    public void setPublisher_url(String publisher_url) {
        this.publisher_url = publisher_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getF2f_url() {
        return f2f_url;
    }

    public void setF2f_url(String f2f_url) {
        this.f2f_url = f2f_url;
    }

    public long getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(long social_rank) {
        this.social_rank = social_rank;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
