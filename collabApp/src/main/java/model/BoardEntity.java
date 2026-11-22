package model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "board")
public class BoardEntity {
    @Id
    private Integer id;

    @Column(name = "projectname")
    private String projectName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "deadline")
    private Timestamp deadline;

    @Transient
    private String role;

    public Integer getId() { return id; }
    public String getProjectName() { return projectName; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getDeadline() { return deadline; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
