package com.quanhm.ecommerce.be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max=50)
    private String name;

    // Tên hiển thị đẹp (VD: "Trang điểm" thay vì "trang_diem")
    @Column(name = "display_name")
    @Size(max=100)
    private String displayName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "parent_category_id")
    private Category parentCategory;

    private int level;

    public Category() {
    }

    public Category(Long id, String name, String displayName, Category parentCategory, int level) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.parentCategory = parentCategory;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        // Trả về displayName nếu có, nếu không thì trả về name
        return displayName != null && !displayName.isEmpty() ? displayName : name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
