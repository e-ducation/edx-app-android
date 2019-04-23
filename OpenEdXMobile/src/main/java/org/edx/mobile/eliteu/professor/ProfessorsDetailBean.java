package org.edx.mobile.eliteu.professor;

import java.io.Serializable;

public class ProfessorsDetailBean implements Serializable {

    private ProfessorBean professorBean;
    private String professor_info;

    public ProfessorBean getProfessorBean() {
        return professorBean;
    }

    public void setProfessorBean(ProfessorBean professorBean) {
        this.professorBean = professorBean;
    }

    public String getProfessor_info() {
        return professor_info;
    }

    public void setProfessor_info(String professor_info) {
        this.professor_info = professor_info;
    }
}
