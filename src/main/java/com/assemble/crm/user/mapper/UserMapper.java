package com.assemble.crm.user.mapper;

import com.assemble.crm.user.dto.UserResponse;
import com.assemble.crm.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName().name(),
                user.isActive(),
                user.getCompany().getId(),
                user.getCreatedAt()
        );
    }
}
