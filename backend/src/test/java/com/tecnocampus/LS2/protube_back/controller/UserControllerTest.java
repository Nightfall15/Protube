package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.configuration.JwtAuthenticationFilter;
import com.tecnocampus.LS2.protube_back.models.UserDTO;
import com.tecnocampus.LS2.protube_back.services.JwtService;
import com.tecnocampus.LS2.protube_back.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {WebSecurityConfigurer.class, JwtAuthenticationFilter.class}
                )
        }
)
@TestPropertySource(properties = {
        "protube.store.dir=${ENV_PROTUBE_STORE_DIR:src/test/resources/videos}"
})
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    void testCreateUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("mightyzote");

        Mockito.when(service.createUser(any(UserDTO.class))).thenReturn(dto);

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testListUsers() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);

        Mockito.when(service.getAllUsers()).thenReturn(List.of(dto));

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetUser_found() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(5L);

        Mockito.when(service.getUserById(5L)).thenReturn(Optional.of(dto));

        mvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void testGetUser_notFound() throws Exception {
        Mockito.when(service.getUserById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCurrentUser_found() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(3L);

        Mockito.when(service.getUserByUsername("hollowbretta")).thenReturn(Optional.of(dto));

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("hollowbretta");

        mvc.perform(get("/api/users/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void testGetCurrentUser_notFound() throws Exception {
        Mockito.when(service.getUserByUsername("paintersheo")).thenReturn(Optional.empty());

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("paintersheo");

        mvc.perform(get("/api/users/me").principal(auth))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_found() throws Exception {
        UserDTO updated = new UserDTO();
        updated.setId(1L);
        updated.setUsername("nailsmith");

        Mockito.when(service.updateUser(eq(1L), any(UserDTO.class))).thenReturn(Optional.of(updated));

        mvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nailsmith"));
    }

    @Test
    void testUpdateUser_notFound() throws Exception {
        Mockito.when(service.updateUser(eq(100L), any(UserDTO.class))).thenReturn(Optional.empty());

        mvc.perform(put("/api/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/api/users/8"))
                .andExpect(status().isNoContent());
        Mockito.verify(service).deleteUser(8L);
    }
}
