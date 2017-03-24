package com.gmail.dleemcewen.tandemfieri.menubuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jfly1_000 on 2/13/2017.
 */
public class ItemOption implements Serializable{
    private String optionName;
    private ArrayList<OptionSelection> selections;
    private boolean required;
    private boolean andRelationship; //determines if the relations ship of the items is mutually exclusive or not ie: you cannot have both ranch and ceaser for your dressing

    public ItemOption(){}

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public ArrayList<OptionSelection> getSelections() {
        if(selections!=null)return selections;
        selections = new ArrayList<>();
        return selections;
    }

    public void setSelections(ArrayList<OptionSelection> selections) {
        this.selections = selections;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ItemOption(String name, boolean required, boolean andRelationship){
        this.optionName = name;
        this.required = required;
        this.andRelationship = andRelationship;
        this.selections = new ArrayList<>();
    }

    public boolean isAndRelationship() {
        return andRelationship;
    }

    public void setAndRelationship(boolean andRelationship) {
        this.andRelationship = andRelationship;
    }
}
