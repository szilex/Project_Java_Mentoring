package com.euvic.mentoring.meeting;

import com.euvic.mentoring.MentoringApplication;
import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.entity.MeetingDTO;
import com.euvic.mentoring.service.IMeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
public class MeetingControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMeetingService meetingService;

    private MeetingDTO meetingDTOToReturn;

    @BeforeEach
    public void setup() {

        meetingDTOToReturn = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1);
        meetingDTOToReturn.setId(1);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndMeetingExists_whenGetMeeting_thenReturnMeetingDTO() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNonExistingMeeting_whenGetMeeting_thenReturn400BadRequest() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenThrow(new MeetingNotFoundException());

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndMeetingExists_whenGetMeeting_thenReturnMeetingDTO() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNonExistingMeeting_whenGetMeeting_thenReturn400BadRequest() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenThrow(new MeetingNotFoundException());

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndMeetingExists_whenGetMeeting_thenReturn401Unauthorized() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndNonExistingMeeting_whenGetMeeting_thenReturn401Unauthorized() throws Exception {

        Mockito.when(meetingService.getMeeting(any(Integer.class))).thenThrow(new MeetingNotFoundException());

        mockMvc.perform(get("/meeting/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNoMeeting_whenGetMeetings_thenReturnEmptyList() throws Exception {

        Mockito.when(meetingService.getMeetings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndMultipleMeetings_whenGetMeetings_thenReturnMeetingList() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getMeetings()).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}," +
                                "{\"id\":" + meetingDTO.getId() + "," +
                                "\"date\":\"" + meetingDTO.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNoMeeting_whenGetMeetings_thenReturnEmptyList() throws Exception {

        Mockito.when(meetingService.getMeetings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndMultipleMeetings_whenGetMeetings_thenReturnMeetingList() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getMeetings()).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}," +
                                "{\"id\":" + meetingDTO.getId() + "," +
                                "\"date\":\"" + meetingDTO.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                                "\"studentId\":" + "null" + "}]"));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndNoMeeting_whenGetMeetings_thenReturn401Unauthorized() throws Exception {

        Mockito.when(meetingService.getMeetings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndMultipleMeetings_whenGetMeetings_thenReturn401Unauthorized() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), 1);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getMeetings()).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNoMeeting_whenGetStudentMeetings_thenReturnEmptyList() throws Exception {

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndMultipleMeetings_whenGetStudentMeetings_thenReturnMeetingList() throws Exception {

        meetingDTOToReturn.setStudentId(2);
        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + 2 + "}," +
                                "{\"id\":" + meetingDTO.getId() + "," +
                                "\"date\":\"" + meetingDTO.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                                "\"studentId\":" + 2 + "}]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNoMeeting_whenGetStudentMeetings_thenReturnEmptyList() throws Exception {

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndMultipleMeetings_whenGetStudentMeetings_thenReturnMeetingList() throws Exception {

        meetingDTOToReturn.setStudentId(2);
        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + 2 + "}," +
                                "{\"id\":" + meetingDTO.getId() + "," +
                                "\"date\":\"" + meetingDTO.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                                "\"studentId\":" + 2 + "}]"));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndNoMeeting_whenGetStudentMeetings_thenReturn401Unauthorized() throws Exception {

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndMultipleMeetings_whenGetStudentMeetings_thenReturn401Unauthorized() throws Exception {

        meetingDTOToReturn.setStudentId(2);
        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), 1, 2);
        meetingDTO.setId(2);

        Mockito.when(meetingService.getStudentMeetings(any(Integer.class))).thenReturn(List.of(meetingDTOToReturn, meetingDTO));

        mockMvc.perform(get("/meeting/student/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndIncorrectMeetingDTO_whenPostMeeting_thenReturn400BadRequest() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndCorrectMeetingDTO_whenPostMeeting_thenReturnMeetingDTO() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + meetingDTOToReturn.getStudentId() + "}"));
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndIncorrectMeeting_whenPostMeeting_thenReturn403Forbidden() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndCorrectMeeting_whenPostMeeting_thenReturn403Forbidden() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndIncorrectMeeting_whenPostMeeting_thenReturn401Unauthorized() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndCorrectMeeting_whenPostMeeting_thenReturn401Unauthorized() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.saveMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(post("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"date\":\"" + meetingDTO.getDate() + "\"," +
                        "\"startTime\":\"" + meetingDTO.getStartTime().toString() + "\"," +
                        "\"endTime\":\"" + meetingDTO.getEndTime().toString() + "\"," +
                        "\"mentorId\":" + meetingDTO.getMentorId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndIncorrectMeetingDTO_whenPutMeeting_thenReturn403Forbidden() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(null, null, null, null, null);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndCorrectMeetingDTO_whenPutMeeting_thenReturn403Forbidden() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);
        meetingDTO.setId(1);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndIncorrectMeeting_whenPutMeeting_thenReturn400BadRequest() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndCorrectMeeting_whenPutMeeting_thenReturnMeetingDTO() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);
        meetingDTO.setId(1);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"id\":" + meetingDTOToReturn.getId() + "," +
                                "\"date\":\"" + meetingDTOToReturn.getDate() + "\"," +
                                "\"startTime\":\"" + meetingDTOToReturn.getStartTime().toString() + "\"," +
                                "\"endTime\":\"" + meetingDTOToReturn.getEndTime().toString() + "\"," +
                                "\"mentorId\":" + meetingDTOToReturn.getMentorId() + "," +
                                "\"studentId\":" + meetingDTOToReturn.getStudentId() + "}"));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndIncorrectMeeting_whenPutMeeting_thenReturn401Unauthorized() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), null);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndCorrectMeeting_whenPutMeeting_thenReturn401Unauthorized() throws Exception {

        MeetingDTO meetingDTO = new MeetingDTO(LocalDate.of(2020, 8, 6), LocalTime.of(15, 20, 30, 1), LocalTime.of(15, 35, 30, 1), 1, 2);
        meetingDTO.setId(1);

        Mockito.when(meetingService.updateMeeting(any(MeetingDTO.class))).thenReturn(meetingDTOToReturn);

        mockMvc.perform(put("/meeting")
                .contentType("application/json")
                .content("{\"id\":" + meetingDTO.getId() + "," +
                        "\"studentId\":" + meetingDTO.getStudentId() + "}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndMeetingExists_whenDeleteMeeting_thenDeleteMeeting() throws Exception {

        Mockito.doNothing().when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_MENTOR"})
    void givenUserLoggedAsMentorAndNonExistingMeeting_whenDeleteMeeting_thenReturn400BadRequest() throws Exception {

        Mockito.doThrow(new MeetingNotFoundException()).when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndMeetingExists_whenDeleteMeeting_thenReturn403Forbidden() throws Exception {

        Mockito.doNothing().when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@email.com", password = "pass123", authorities = {"ROLE_STUDENT"})
    void givenUserLoggedAsStudentAndNonExistingMeeting_whenDeleteMeeting_thenReturn403Forbidden() throws Exception {

        Mockito.doThrow(new MeetingNotFoundException()).when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndMeetingExists_whenDeleteMeeting_thenReturn401Unauthorized() throws Exception {

        Mockito.doNothing().when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUserAndNonExistingMeeting_whenDeleteMeeting_thenReturn401Unauthorized() throws Exception {

        Mockito.doThrow(new MeetingNotFoundException()).when(meetingService).deleteMeeting(any(Integer.class));

        mockMvc.perform(delete("/meeting/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
