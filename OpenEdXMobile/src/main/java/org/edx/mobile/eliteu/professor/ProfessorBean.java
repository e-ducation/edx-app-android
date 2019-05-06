package org.edx.mobile.eliteu.professor;

import java.io.Serializable;

public class ProfessorBean implements Serializable {

    private int id;
    private int user_id;
    private String name;
    private String description;
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ProfessorBean{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
