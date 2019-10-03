package com.ufpi.leevfrequency.Model;

public class User {

    private String id;
    private String name;
    private String email;
    private String projects;
    private Boolean isVisible;
    private int userType;
    private Boolean isFinalizedRegister;
    private String idTeacher;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        this.isVisible = visible;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Boolean getFinalizedRegister() {
        return isFinalizedRegister;
    }

    public void setFinalizedRegister(Boolean finalizedRegister) {
        this.isFinalizedRegister = finalizedRegister;
    }

    public String getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(String idTeacher) {
        this.idTeacher = idTeacher;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
