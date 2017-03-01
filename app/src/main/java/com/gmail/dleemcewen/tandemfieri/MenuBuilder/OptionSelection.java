package com.gmail.dleemcewen.tandemfieri.MenuBuilder;

/**
 * Created by jfly1_000 on 2/13/2017.
 */
public class OptionSelection {
    private String SelectionName;
    private  double addedPrice;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelectionName() {
        return SelectionName;
    }

    public void setSelectionName(String selectionName) {
        SelectionName = selectionName;
    }

    public double getAddedPrice() {
        return addedPrice;
    }

    public void setAddedPrice(double addedPrice) {
        this.addedPrice = addedPrice;
    }

    /**
     * Constructor with no arguments required by firebase
     */
    public OptionSelection() {}

}
