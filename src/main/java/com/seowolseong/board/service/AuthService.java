package com.seowolseong.board.service; 

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.seowolseong.board.domain.Admin;
import com.seowolseong.board.dto.PostDto.AuthUser;
import com.seowolseong.board.error.ApiException;
import com.seowolseong.board.error.ErrorCode;
import com.seowolseong.board.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthUser authenticate(String username, String password) {
    	Admin u = userRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException(ErrorCode.AUTH_LOGIN_FAILED));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new ApiException(ErrorCode.AUTH_LOGIN_FAILED);
        }

        return new AuthUser(u.getId(), u.getUsername(), u.getRole());
    }
}
