package org.edx.mobile.eliteu.professor;

import java.io.Serializable;

public class ProfessorsDetailBean implements Serializable {

    private int id;
    private int user_id;
    private String name;
    private String description;
    private String avatar;
    private String professor_info;

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

    public String getProfessor_info() {
        return professor_info;
    }

    public void setProfessor_info(String professor_info) {
        this.professor_info = professor_info;
    }
}
