package com.example.imhungry3.Class;

public class RecipeClass {
    private String id;
    private String nameRecipe;
    private String like;
    private String description;
    private String urlImage;
    private String watch;

    public RecipeClass() {
    }

    public RecipeClass(String nameRecipe, String like, String description, String urlImage, String watch,String id) {
        this.nameRecipe = nameRecipe;
        this.like = like;
        this.description = description;
        this.urlImage = urlImage;
        this.watch = watch;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getWatch() {
        return watch;
    }

    public void setWatch(String watch) {
        this.watch = watch;
    }

    public String getNameRecipe() {
        return nameRecipe;
    }

    public void setNameRecipe(String nameRecipe) {
        this.nameRecipe = nameRecipe;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
