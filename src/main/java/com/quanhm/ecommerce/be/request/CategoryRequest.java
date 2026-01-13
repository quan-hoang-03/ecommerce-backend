package com.quanhm.ecommerce.be.request;

public class CategoryRequest {
    public String name;
    public String parentName;

    public CategoryRequest(String name, String parentName) {
        this.name = name;
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
