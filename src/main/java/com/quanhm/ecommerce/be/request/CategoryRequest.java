package com.quanhm.ecommerce.be.request;

public class CategoryRequest {
    public String name;
    public String displayName;  // Tên hiển thị đẹp
    public String parentName;

    public CategoryRequest() {
    }

    public CategoryRequest(String name, String displayName, String parentName) {
        this.name = name;
        this.displayName = displayName;
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
