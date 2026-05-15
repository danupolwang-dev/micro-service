package com.example.user_service.dto;

public class MenuDTO {
    private Long id;
    private String name;
    private String path;
    private String icon;
    private Integer displayOrder;

    // Constructors
    public MenuDTO() {
    }

    public MenuDTO(Long id, String name, String path, String icon, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.icon = icon;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
