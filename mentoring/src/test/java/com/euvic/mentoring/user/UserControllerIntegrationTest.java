package com.euvic.mentoring.user;

import com.euvic.mentoring.MentoringApplication;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.service.IUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {MentoringApplication.class}
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-usercontrollerintegrationtest.properties")
public class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    private static User mentorToReturn;
    private static List<User> studentsToReturn;

    @BeforeAll
    public static void setup() {
        mentorToReturn = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );

        User student1 = new User(2, "georgeadams@email.com", "pass123", "ROLE_STUDENT", 1, "George", "Adams" );
        User student2 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student3 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        studentsToReturn = List.of(student1, student2, student3);
    }

    @BeforeEach
    public void setupMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentor_whenGetMentor_thenReturnMentor() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1,\"mail\":\"johnsmith@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_MENTOR\",\"enabled\":1,\"firstName\":\"John\",\"lastName\":\"Smith\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudent_whenGetMentor_thenReturn403Forbidden() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsUnauthorized_whenGetMentor_thenReturn401Unauthorized() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNoStudentWithSpecifiedId_whenGetStudent_thenReturn400BadRequest() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndUserWithSpecifiedIdExists_whenGetStudent_thenReturnStudent() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNoStudentWithSpecifiedId_whenGetStudent_thenReturn400BadRequest() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndStudentWithSpecifiedIdExists_whenGetStudent_thenReturnStudent() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}"));
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndNoStudentWithSpecifiedId_whenGetStudent_thenReturn401Unauthorized() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndStudentWithSpecifiedIdExists_whenGetStudent_thenReturn401Unauthorized() throws Exception {

        when(userService.getStudent(any(Integer.class))).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNoStudents_whenGetStudents_thenReturnEmptyList() throws Exception {

        when(userService.getStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentor_whenGetStudents_thenReturnStudentList() throws Exception {

        when(userService.getStudents()).thenReturn(studentsToReturn);

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}," +
                                "{\"id\":3,\"mail\":\"karenjohns@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Karen\",\"lastName\":\"Johns\"}," +
                                "{\"id\":4,\"mail\":\"monicadaniels@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Monica\",\"lastName\":\"Daniels\"}]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNoStudents_whenGetStudents_thenReturnEmptyList() throws Exception {

        when(userService.getStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndMultipleStudents_whenGetStudents_thenReturnStudentList() throws Exception {

        when(userService.getStudents()).thenReturn(studentsToReturn);

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}," +
                                "{\"id\":3,\"mail\":\"karenjohns@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Karen\",\"lastName\":\"Johns\"}," +
                                "{\"id\":4,\"mail\":\"monicadaniels@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Monica\",\"lastName\":\"Daniels\"}]"));
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndNoStudents_whenGetStudents_thenReturn401Unauthorized() throws Exception {

        when(userService.getStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndMultipleStudents_whenGetStudents_thenReturn401Unauthorized() throws Exception {

        when(userService.getStudents()).thenReturn(studentsToReturn);

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndIncorrectStudent_whenPostStudent_thenReturn403Forbidden() throws Exception {

        when(userService.saveStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":null,\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndCorrectStudent_whenPostStudent_thenReturn403Forbidden() throws Exception {

        User studentToSave = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(studentToSave);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndIncorrectStudent_whenPostStudent_thenReturn403Forbidden() throws Exception {

        when(userService.saveStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":null,\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndCorrectStudent_whenPostStudent_thenReturn403Forbidden() throws Exception {

        User studentToSave = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(studentToSave);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndIncorrectStudent_whenPostStudent_thenReturn400BadRequest() throws Exception {

        when(userService.saveStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndCorrectStudent_whenPostStudent_thenSaveAndReturnStudent() throws Exception {

        User updatedStudent = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(updatedStudent);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndIncorrectStudent_whenUpdateStudent_thenReturn403Forbidden() throws Exception {

        when(userService.updateStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":null,\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndCorrectStudent_whenUpdateStudent_thenReturn403Forbidden() throws Exception {

        User updatedStudent = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndIncorrectStudent_whenUpdateStudent_thenReturn400BadRequest() throws Exception {

        when(userService.updateStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":null,\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndCorrectStudent_whenUpdateStudent_thenUpdateAndReturnStudent() throws Exception {

        User studentToUpdate = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(studentToUpdate);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"));
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndIncorrectStudent_whenUpdateStudent_thenReturn403Forbidden() throws Exception {

        when(userService.updateStudent(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":null,\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndCorrectStudent_whenUpdateStudent_thenReturn403Forbidden() throws Exception {

        User updatedStudent = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndIncorrectStudentId_whenDeleteStudent_thenReturn403Forbidden() throws Exception {

        doThrow(new IllegalArgumentException()).when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndCorrectStudentId_whenDeleteStudent_thenReturn403Forbidden() throws Exception {

        doNothing().when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndIncorrectStudentId_whenDeleteStudent_thenDeleteStudent() throws Exception {

        doThrow(new IllegalArgumentException()).when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndCorrectStudentId_whenDeleteStudent_thenDeleteStudent() throws Exception {

        doNothing().when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndIncorrectStudentId_whenDeleteStudent_thenReturn403Forbidden() throws Exception {

        doThrow(new IllegalArgumentException()).when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenUserIsAnonymousAndCorrectStudentId_whenDeleteStudent_thenReturn403Forbidden() throws Exception {

        doNothing().when(userService).deleteStudent(any(Integer.class));

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
