package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.models.UserDTO;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }


    public UserDTO createUser(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setNumber(dto.getNumber());
        User savedUser = userRepository.save(user);
        dto.setId(savedUser.getId());
        return dto;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO entityToDTO(User u){
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setName(u.getName());
        dto.setSurname(u.getSurname());
        dto.setNumber(u.getNumber());
        return dto;
    }

    public User dtoToEntity(UserDTO dto){
        User u = new User();
        u.setId(dto.getId());
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setName(dto.getName());
        u.setSurname(dto.getSurname());
        u.setNumber(dto.getNumber());
        return u;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::entityToDTO).toList();
    }

    public java.util.Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::entityToDTO);
    }

    public java.util.Optional<UserDTO> updateUser(Long id, UserDTO dto) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(dto.getUsername());
            existingUser.setEmail(dto.getEmail());
            existingUser.setName(dto.getName());
            existingUser.setSurname(dto.getSurname());
            existingUser.setNumber(dto.getNumber());
            User updatedUser = userRepository.save(existingUser);
            return entityToDTO(updatedUser);
        });
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::entityToDTO);
    }
}
