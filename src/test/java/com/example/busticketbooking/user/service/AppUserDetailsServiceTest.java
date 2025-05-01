package com.example.busticketbooking.user.service;

import com.example.busticketbooking.shared.exception.NotFoundException;
import com.example.busticketbooking.user.entity.AppUser;
import com.example.busticketbooking.user.model.Role;
import com.example.busticketbooking.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setPassword("testpassword");
        appUser.setRole(Role.USER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));

        UserDetails userDetails = appUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("testpassword");
        assertThat(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()).containsExactlyInAnyOrder("ROLE_USER");
    }

    @Test
    void loadUserByUsername_nonExistingUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername("nonexistent"));
    }
}
