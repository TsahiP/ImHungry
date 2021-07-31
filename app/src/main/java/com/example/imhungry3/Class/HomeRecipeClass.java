package com.example.imhungry3.Class;

import java.util.ArrayList;

public class HomeRecipeClass {
    String name_recipe;
    String descraptions;
    String amount_of_shows;
    String amount_ppl_rate;
    float amount_avg_rate;

    public HomeRecipeClass(String name_recipe, String descraptions, String amount_of_shows, String amount_ppl_rate, float amount_avg_rate) {
        this.name_recipe = name_recipe;
        this.descraptions = descraptions;
        this.amount_of_shows = amount_of_shows;
        this.amount_ppl_rate = amount_ppl_rate;
        this.amount_avg_rate = amount_avg_rate;
    }

    public String getName_recipe() {
        return name_recipe;
    }

    public void setName_recipe(String name_recipe) {
        this.name_recipe = name_recipe;
    }

    public String getDescraptions() {
        return descraptions;
    }

    public void setDescraptions(String descraptions) {
        this.descraptions = descraptions;
    }

    public String getAmount_of_shows() {
        return amount_of_shows;
    }

    public void setAmount_of_shows(String amount_of_shows) {
        this.amount_of_shows = amount_of_shows;
    }

    public String getAmount_ppl_rate() {
        return amount_ppl_rate;
    }

    public void setAmount_ppl_rate(String amount_ppl_rate) {
        this.amount_ppl_rate = amount_ppl_rate;
    }

    public float getAmount_avg_rate() {
        return amount_avg_rate;
    }

    public void setAmount_avg_rate(int amount_avg_rate) {
        this.amount_avg_rate = amount_avg_rate;

    }


}
