package org.astron.focify_backend.api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "tb_users",
        uniqueConstraints=@UniqueConstraint(columnNames = {"login"})
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String password;
}
