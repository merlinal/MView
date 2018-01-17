package com.merlin.view.model;

import java.util.List;

/**
 * @author merlin
 */

public class SelectModel {

    private long id;
    private String label;
    private boolean selected;

    private List<SelectModel> children;

    @Override
    public String toString() {
        return label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<SelectModel> getChildren() {
        return children;
    }

    public void setChildren(List<SelectModel> children) {
        this.children = children;
    }

}
