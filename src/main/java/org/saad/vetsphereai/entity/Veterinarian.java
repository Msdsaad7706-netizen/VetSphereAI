package org.saad.vetsphereai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "veterinarian")
public class Veterinarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String specialization;
    private  Integer experiance;
    private  String qualification;
    private  String phone;
    private String email;

    public Veterinarian() {
    }

    public Veterinarian(Long id, String fullName, String specialization, Integer experiance, String qualification, String phone, String email) {
        this.id = id;
        this.fullName = fullName;
        this.specialization = specialization;
        this.experiance = experiance;
        this.qualification = qualification;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getExperiance() {
        return experiance;
    }

    public void setExperiance(Integer experiance) {
        this.experiance = experiance;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
