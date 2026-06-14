package com.assemble.crm.role.repository;

import com.assemble.crm.role.entity.Role;
import com.assemble.crm.role.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
