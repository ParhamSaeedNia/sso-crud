package org.example.model;

public class Permission {
    private Long id;
    private String name;

    public Permission() {

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

    public Permission(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
