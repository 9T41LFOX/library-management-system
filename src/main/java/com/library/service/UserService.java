package com.library.service;

import com.library.dto.MemberForm;
import com.library.entity.User;

public interface UserService {
    User findByUsername(String username);

    boolean usernameExists(String username);

    User registerMember(MemberForm form, String username, String rawPassword);
}
