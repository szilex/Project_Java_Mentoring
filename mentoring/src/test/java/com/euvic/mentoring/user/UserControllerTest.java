package com.euvic.mentoring.user;

import com.euvic.mentoring.MentoringApplication;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
@TestPropertySource(locations = "classpath:application-userintegrationtest.properties")
public class UserControllerTest {

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
    void given_UserLoggedAsMentor_when_GetMentor_then_ReturnMentor() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":1,\"mail\":\"johnsmith@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_MENTOR\",\"enabled\":1,\"firstName\":\"John\",\"lastName\":\"Smith\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void given_UserLoggedAsStudent_when_GetMentor_then_Return403Forbidden() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void given_UserIsUnauthorized_when_GetMentor_then_Return401Unauthorized() throws Exception {

        when(userService.getMentor()).thenReturn(mentorToReturn);

        mockMvc.perform(get("/user/mentor"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void given_UserLoggedAsMentor_when_GetStudent_then_ReturnStudent() throws Exception {

        when(userService.getStudent(2)).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void given_UserLoggedAsStudent_when_GetStudent_then_ReturnStudent() throws Exception {

        when(userService.getStudent(2)).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":2,\"mail\":\"georgeadams@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"George\",\"lastName\":\"Adams\"}"));
    }

    @Test
    @WithAnonymousUser
    void given_UserIsAnonymous_when_GetStudent_then_Return401Unauthorized() throws Exception {

        when(userService.getStudent(2)).thenReturn(studentsToReturn.get(0));

        mockMvc.perform(get("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void given_UserLoggedAsMentor_when_GetStudents_then_ReturnStudents() throws Exception {

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
    void given_UserLoggedAsStudent_when_GetStudents_then_ReturnStudents() throws Exception {

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
    void given_UserIsAnonymous_when_GetStudents_then_Return401Unauthorized() throws Exception {

        when(userService.getStudents()).thenReturn(studentsToReturn);

        mockMvc.perform(get("/user/student"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void given_UserLoggedAsMentor_when_PostStudent_then_Return403Forbidden() throws Exception {

        User studentToSave = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(studentToSave);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void given_UserLoggedAsStudent_when_PostStudent_then_Return403Forbidden() throws Exception {

        User studentToSave = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(studentToSave);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void given_UserIsAnonymous_when_PostStudent_then_CreateAndReturnStudent() throws Exception {

        User studentToSave = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.saveStudent(any(User.class))).thenReturn(studentToSave);

        mockMvc.perform(post("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void given_UserLoggedAsMentor_when_UpdateStudent_then_Return403Forbidden() throws Exception {

        User studentToUpdate = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(studentToUpdate);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void given_UserLoggedAsStudent_when_UpdateStudent_then_UpdateAndReturnStudent() throws Exception {

        User studentToUpdate = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(studentToUpdate);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(content().json(
                        "{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"));
    }

    @Test
    @WithAnonymousUser
    void given_UserIsAnonymous_when_UpdateStudent_then_Return403Forbidden() throws Exception {

        User studentToUpdate = new User(5, "laurenmoriz@email.com", "pass123", "ROLE_STUDENT", 1, "Lauren", "Moriz" );

        when(userService.updateStudent(any(User.class))).thenReturn(studentToUpdate);

        mockMvc.perform(put("/user/student")
                .contentType("application/json")
                .content("{\"id\":5,\"mail\":\"laurenmoriz@email.com\",\"password\":\"pass123\",\"authority\":\"ROLE_STUDENT\",\"enabled\":1,\"firstName\":\"Lauren\",\"lastName\":\"Moriz\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void given_UserLoggedAsMentor_when_DeleteStudent_then_Return403Forbidden() throws Exception {

        doNothing().when(userService).deleteStudent(2);

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void given_UserLoggedAsStudent_when_DeleteStudent_then_DeleteStudent() throws Exception {

        doNothing().when(userService).deleteStudent(2);

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void given_UserIsAnonymous_when_DeleteStudent_then_Return403Forbidden() throws Exception {

        doNothing().when(userService).deleteStudent(2);

        mockMvc.perform(delete("/user/student/2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
