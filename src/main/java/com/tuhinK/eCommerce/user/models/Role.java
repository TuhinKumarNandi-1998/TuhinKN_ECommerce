package com.tuhinK.eCommerce.user.models;

import com.tuhinK.eCommerce.commons.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Role extends BaseModel {

    private String name;

    public Role(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();
}
