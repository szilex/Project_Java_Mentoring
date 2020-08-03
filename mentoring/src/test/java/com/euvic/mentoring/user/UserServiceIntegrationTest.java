package com.euvic.mentoring.user;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.UserRepository;
import com.euvic.mentoring.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceIntegrationTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test()
    public void givenNoMentor_whenGetMentor_thenThrowUserNotFoundException() {

        User mentorToReturn = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );

        Mockito.when(userRepository.findFirstByAuthorityOrderByIdAsc(mentorToReturn.getAuthority()))
                .thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, userService::getMentor);
    }

    @Test
    public void givenOneMentor_whenGetMentor_thenReturnMentor() {

        User mentorToReturn = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );

        Mockito.when(userRepository.findFirstByAuthorityOrderByIdAsc(mentorToReturn.getAuthority()))
                .thenReturn(java.util.Optional.of(mentorToReturn));
        User mentor = userService.getMentor();

        assertThat(mentor.getMail()).isEqualTo(mentorToReturn.getMail());
    }

    @Test
    public void givenIncorrectId_whenGetStudent_thenThrowUserNotFoundException() {

        int studentId = 3;
        String authority = "ROLE_STUDENT";

        Mockito.when(userRepository.findByIdAndAuthority(studentId, authority)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getStudent(studentId));
    }

    @Test
    public void givenCorrectId_whenGetStudent_thenReturnStudent() {

        User studentToReturn = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        int studentId = 3;
        String authority = "ROLE_STUDENT";

        Mockito.when(userRepository.findByIdAndAuthority(studentId, authority)).thenReturn(Optional.of(studentToReturn));
        User student = userService.getStudent(studentId);

        assertThat(student.getId()).isEqualTo(studentId);
    }

    @Test
    public void givenNoStudents_whenGetStudents_thenReturnEmptyList() {

        String authority = "ROLE_STUDENT";

        Mockito.when(userRepository.findAllByAuthority(authority)).thenReturn(Collections.emptyList());
        List<User> students = userService.getStudents();

        assertThat(students.isEmpty()).isTrue();
    }

    @Test
    public void givenOneStudent_whenGetStudents_thenReturnStudentList() {

        List<User> studentToReturn = List.of(new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" ));
        String authority = "ROLE_STUDENT";

        Mockito.when(userRepository.findAllByAuthority(authority)).thenReturn(studentToReturn);
        List<User> students = userService.getStudents();

        assertThat(students.size()).isEqualTo(1);
    }

    @Test
    public void givenNull_whenSaveStudent_thenReturnNull() {

        assertThat(userService.saveStudent(null)).isNull();
    }

    @Test
    public void givenNonExistingStudentWithoutAuthorityAndEnabled_whenSaveStudent_thenReturnStudent() {

        User studentToSave = new User("karenjohns@email.com", "pass123", "Karen", "Johns");

        Mockito.when(userRepository.save(studentToSave)).thenReturn(new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns"));
        User student = userService.saveStudent(studentToSave);

        assertThat(studentToSave.getMail()).isEqualTo(student.getMail());
        assertThat(studentToSave.getAuthority()).isEqualTo(student.getAuthority());
        assertThat(studentToSave.getEnabled()).isEqualTo(student.getEnabled());
    }

    @Test
    public void givenExistingStudent_whenSaveStudent_thenThrowException() {

        User studentToSave = new User("karenjohns@email.com", "pass123", "Karen", "Johns");

        Mockito.when(userRepository.save(studentToSave)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,() -> userService.saveStudent(studentToSave));
    }

    @Test
    public void givenNull_whenUpdateStudent_thenReturnNull() {
        assertThat(userService.updateStudent(null)).isNull();
    }

    @Test
    public void givenNonExistingStudent_whenUpdateStudent_thenThrowUserNotFoundException() {

        User studentToUpdate = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");

        Mockito.when(userRepository.findByIdAndAuthority(studentToUpdate.getId(), studentToUpdate.getAuthority())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateStudent(studentToUpdate));
    }

    @Test
    public void givenExistingStudent_whenUpdateStudentByDifferentStudent_thenThrowIllegalArgumentException() {

        User studentToUpdate = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        User loggedStudent = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels");

        Mockito.when(userRepository.findByIdAndAuthority(studentToUpdate.getId(), studentToUpdate.getAuthority())).thenReturn(Optional.of(studentToUpdate));
        Mockito.lenient().when(userRepository.findByMailAndAuthority(loggedStudent.getMail(), loggedStudent.getAuthority())).thenReturn(Optional.of(loggedStudent));
        Mockito.lenient().when(userRepository.save(studentToUpdate)).thenReturn(studentToUpdate);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of((GrantedAuthority) () -> "ROLE_STUDENT");
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return "monicadaniels@email.com";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        });

        assertThrows(IllegalArgumentException.class, () -> userService.updateStudent(studentToUpdate));
    }

    @Test
    public void givenExistingStudent_whenUpdateStudentBySameStudent_thenUpdateStudent() {

        User studentToUpdate = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");

        Mockito.when(userRepository.findByIdAndAuthority(studentToUpdate.getId(), studentToUpdate.getAuthority())).thenReturn(Optional.of(studentToUpdate));
        Mockito.lenient().when(userRepository.findByMailAndAuthority(studentToUpdate.getMail(), studentToUpdate.getAuthority())).thenReturn(Optional.of(studentToUpdate));
        Mockito.lenient().when(userRepository.save(studentToUpdate)).thenReturn(studentToUpdate);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of((GrantedAuthority) () -> "ROLE_STUDENT");
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return "karenjohns@email.com";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        });

        User student = userService.updateStudent(studentToUpdate);

        assertNotNull(student);
        assertThat(studentToUpdate.getMail()).isEqualTo(student.getMail());
    }

    @Test
    public void givenIncorrectId_whenDeleteStudent_thenThrowUserNotFoundException() {

        int studentId = 3;
        String authority = "ROLE_STUDENT";

        Mockito.when(userRepository.findByIdAndAuthority(studentId, authority)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteStudent(studentId));
    }

    @Test
    public void givenCorrectId_whenDeleteStudentByDifferentStudent_thenThrowIllegalArgumentException() {

        User studentToDelete = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        User loggedStudent = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels");

        Mockito.when(userRepository.findByIdAndAuthority(studentToDelete.getId(), studentToDelete.getAuthority())).thenReturn(Optional.of(studentToDelete));
        Mockito.lenient().when(userRepository.findByMailAndAuthority(loggedStudent.getMail(), loggedStudent.getAuthority())).thenReturn(Optional.of(loggedStudent));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of((GrantedAuthority) () -> "ROLE_STUDENT");
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return "monicadaniels@email.com";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        });

        assertThrows(IllegalArgumentException.class, () -> userService.deleteStudent(studentToDelete.getId()));
    }

    @Test
    public void givenCorrectId_whenDeleteStudentBySameStudent_thenDeleteStudent() {

        User studentToDelete = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");

        Mockito.when(userRepository.findByIdAndAuthority(studentToDelete.getId(), studentToDelete.getAuthority())).thenReturn(Optional.of(studentToDelete));
        Mockito.lenient().when(userRepository.findByMailAndAuthority(studentToDelete.getMail(), studentToDelete.getAuthority())).thenReturn(Optional.of(studentToDelete));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of((GrantedAuthority) () -> "ROLE_STUDENT");
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return "karenjohns@email.com";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        });

        Assertions.assertDoesNotThrow(() -> userService.deleteStudent(studentToDelete.getId()));
    }

}
