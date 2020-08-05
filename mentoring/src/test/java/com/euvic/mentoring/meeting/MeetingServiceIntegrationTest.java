package com.euvic.mentoring.meeting;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.MeetingDTO;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.MeetingRepository;
import com.euvic.mentoring.service.IMailService;
import com.euvic.mentoring.service.IUserService;
import com.euvic.mentoring.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class MeetingServiceIntegrationTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IMailService mailService;

    @Mock
    private ModelMapper modelMapper = new ModelMapper();

    private User mentor;
    private User student;
    private MeetingDTO inputMeetingDTO;
    private MeetingDTO outputMeetingDTO;
    private Meeting meeting;

    @BeforeEach
    public void setup() {
        mentor = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        student = new User(2, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        meeting = new Meeting(1, LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
    }

    @Test
    void givenNoMeetingWithSpecifiedId_whenGetMeeting_thenThrowMeetingNotFoundException() {

        Mockito.when(meetingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(MeetingNotFoundException.class, () -> meetingService.getMeeting(1));
    }

    @Test
    void givenMeetingWithSpecifiedId_whenGetMeeting_thenReturnMeetingDTO() {

        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);
        MeetingDTO meetingDTO = meetingService.getMeeting(meeting.getId());

        assertThat(meetingDTO.getId()).isEqualTo(outputMeetingDTO.getId());
        assertThat(meetingDTO).isEqualTo(outputMeetingDTO);
    }

    @Test
    void givenNoMeetings_whenGetMeetings_thenReturnEmptyList() {

        Mockito.when(meetingRepository.findAll()).thenReturn(Collections.emptyList());

        List<MeetingDTO> meetingDTOs = meetingService.getMeetings();

        assertThat(meetingDTOs.isEmpty()).isEqualTo(true);
    }

    @Test
    void givenMultipleMeetings_whenGetMeetings_thenReturnMeetingList() {

        Meeting meeting1 = new Meeting(2, LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
        outputMeetingDTO = new MeetingDTO(meeting);
        MeetingDTO outputMeetingDTO1 = new MeetingDTO(meeting1);

        Mockito.when(meetingRepository.findAll()).thenReturn(List.of(meeting, meeting1));
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);
        Mockito.lenient().when(modelMapper.map(meeting1, MeetingDTO.class)).thenReturn(outputMeetingDTO1);
        List<MeetingDTO> meetingDTOs = meetingService.getMeetings();

        assertThat(meetingDTOs.isEmpty()).isEqualTo(false);
        assertThat(meetingDTOs).contains(outputMeetingDTO, outputMeetingDTO1);
    }

    @Test
    void givenNoMeetingForSpecifiedStudent_whenGetStudentMeetings_thenReturnEmptyList() {

        Mockito.when(userService.getStudent(student.getId())).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> meetingService.getStudentMeetings(student.getId()));
    }

    @Test
    void givenMultipleMeetingsForSpecifiedStudent_whenGetStudentMeetings_thenReturnMeetingList() {

        Meeting meeting1 = new Meeting(2, LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
        meeting.setStudent(student);
        meeting1.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);
        MeetingDTO outputMeetingDTO1 = new MeetingDTO(meeting1);

        Mockito.when(userService.getStudent(student.getId())).thenReturn(student);
        Mockito.when(meetingRepository.findByStudent(student)).thenReturn(List.of(meeting, meeting1));
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);
        Mockito.lenient().when(modelMapper.map(meeting1, MeetingDTO.class)).thenReturn(outputMeetingDTO1);
        List<MeetingDTO> meetingDTOs = meetingService.getStudentMeetings(student.getId());

        assertThat(meetingDTOs.isEmpty()).isEqualTo(false);
        assertThat(meetingDTOs).contains(outputMeetingDTO, outputMeetingDTO1);
        assertThat(meetingDTOs.get(0).getStudentId()).isEqualTo(student.getId());
        assertThat(meetingDTOs.get(1).getStudentId()).isEqualTo(student.getId());
    }
}
