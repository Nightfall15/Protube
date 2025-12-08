package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.models.UserDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private IUserRepository userRepository;
    private UserService service;

    @BeforeEach
    void setup() {
        userRepository = mock(IUserRepository.class);
        service = new UserService(userRepository);
    }

    @Test
    void testCreateUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("hknight67");
        dto.setEmail("hollow@knight.com");
        dto.setName("Hollow");
        dto.setSurname("Knight");
        dto.setNumber("123");

        User saved = new User();
        saved.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDTO result = service.createUser(dto);

        assertEquals(1L, result.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        service.deleteUser(5L);
        verify(userRepository).deleteById(5L);
    }

    @Test
    void testEntityToDTO() {
        User u = new User();
        u.setId(5L);
        u.setUsername("hornetbug");
        u.setEmail("hornet@mail.com");
        u.setName("Hornet");
        u.setSurname("Wyrm");
        u.setNumber("123");

        VideoFile vf = new VideoFile();
        vf.setId(10L);
        u.setLikedVideos(List.of(vf));

        UserDTO dto = service.entityToDTO(u);

        assertEquals(5L, dto.getId());
        assertEquals("hornetbug", dto.getUsername());
        assertEquals(List.of(10L), dto.getLikedVideoIds());
    }

    @Test
    void testDtoToEntity() {
        UserDTO dto = new UserDTO();
        dto.setId(4L);
        dto.setUsername("garmondzaza");
        dto.setEmail("garmong@gmail.com");
        dto.setName("Sr");
        dto.setSurname("Garmond");
        dto.setNumber("948");

        User u = service.dtoToEntity(dto);

        assertEquals(4L, u.getId());
        assertEquals("garmondzaza", u.getUsername());
        assertEquals("948", u.getNumber());
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("grimm");

        User u2 = new User();
        u2.setId(2L);
        u2.setUsername("corniferius");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserDTO> list = service.getAllUsers();

        assertEquals(2, list.size());

        assertEquals(1L, list.get(0).getId());
        assertEquals("grimm", list.get(0).getUsername());

        assertEquals(2L, list.get(1).getId());
        assertEquals("corniferius", list.get(1).getUsername());
    }

    @Test
    void testGetUserById_found() {
        User u = new User();
        u.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<UserDTO> result = service.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = service.getUserById(100L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateUser_found() {
        User existing = new User();
        existing.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO update = new UserDTO();
        update.setUsername("coolquirrel");
        update.setEmail("guirri@dreamer.com");
        update.setName("Quirrel");
        update.setSurname("MrSeCoolGuy");
        update.setNumber("6767676767");

        Optional<UserDTO> result = service.updateUser(1L, update);

        assertTrue(result.isPresent());
        assertEquals("coolquirrel", result.get().getUsername());
    }

    @Test
    void testUpdateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = service.updateUser(1L, new UserDTO());

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserByUsername() {
        User u = new User();
        u.setId(12L);

        when(userRepository.findByUsername("elderbug")).thenReturn(Optional.of(u));

        Optional<UserDTO> result = service.getUserByUsername("elderbug");

        assertTrue(result.isPresent());
        assertEquals(12L, result.get().getId());
    }
}
