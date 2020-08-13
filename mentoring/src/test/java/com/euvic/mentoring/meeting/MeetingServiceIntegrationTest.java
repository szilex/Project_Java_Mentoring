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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    private final ModelMapper modelMapper = new ModelMapper();

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

    @Test
    void givenMeetingDTOWithId_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setMentorId(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithMentorId_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithStudentId_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setStudentId(2);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithoutDate_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setDate(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithoutStartTime_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setStartTime(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithoutEndTime_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setEndTime(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithIncorrectDuration_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setEndTime(inputMeetingDTO.getStartTime().plusMinutes(20));
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithDateAndTimeCollidingWithAnotherMeeting_whenSaveMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        inputMeetingDTO.setEndTime(inputMeetingDTO.getStartTime().plusMinutes(20));
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(List.of(meeting));
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Mockito.verify(meetingRepository, Mockito.never()).save(meeting);
        assertThrows(IllegalArgumentException.class, () -> meetingService.saveMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithCorrectArguments_whenSaveMeeting_thenReturnMeetingDTO() {

        inputMeetingDTO = new MeetingDTO(meeting);
        inputMeetingDTO.setId(null);
        inputMeetingDTO.setMentorId(null);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getMentor()).thenReturn(mentor);
        Mockito.lenient().when(meetingRepository.findByDate(inputMeetingDTO.getDate())).thenReturn(Collections.emptyList());
        Mockito.lenient().when(meetingRepository.save(meeting)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);
        MeetingDTO meetingDTO = meetingService.saveMeeting(inputMeetingDTO);

        assertThat(meetingDTO).isEqualTo(outputMeetingDTO);
    }

    @Test
    void givenMeetingDTOWithDate_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(meeting.getDate(), null, null, null, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithStartTime_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, meeting.getStartTime(), null, null, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithEndTime_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, meeting.getEndTime(), null, student.getId());
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithMentorId_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, 1, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithoutMeetingId_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, student.getId());
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithoutStudentId_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, null);
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingDTOWithStudentIdDifferentThanCurrentlyLoggedStudentId_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);
        User loggedStudent = new User(3, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels");

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student, loggedStudent));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenNoMeetingWithSpecifiedId_whenUpdateMeeting_thenThrowMeetingNotFoundException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, student.getId());
        inputMeetingDTO.setId(1);
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.empty());
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(MeetingNotFoundException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingWithSpecifiedIdHasSpecifiedStudentId_whenUpdateMeeting_thenThrowIllegalArgumentException() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor(), student);
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        Mockito.verify(meetingRepository, Mockito.never()).save(any(Meeting.class));
        assertThrows(IllegalArgumentException.class, () -> meetingService.updateMeeting(inputMeetingDTO));
    }

    @Test
    void givenMeetingWithCorrectArguments_whenUpdateMeeting_thenReturnUpdatedMeeting() {

        inputMeetingDTO = new MeetingDTO(null, null, null, null, student.getId());
        inputMeetingDTO.setId(1);
        Meeting dbMeeting = new Meeting(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor());
        meeting.setStudent(student);
        outputMeetingDTO = new MeetingDTO(meeting);

        Mockito.lenient().when(userService.getStudents()).thenReturn(List.of(student));
        Mockito.lenient().when(meetingRepository.findById(inputMeetingDTO.getId())).thenReturn(Optional.of(dbMeeting));
        Mockito.lenient().when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(inputMeetingDTO, Meeting.class)).thenReturn(meeting);
        Mockito.lenient().when(modelMapper.map(meeting, MeetingDTO.class)).thenReturn(outputMeetingDTO);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.lenient().when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new UserDetails() {
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

        MeetingDTO meetingDTO = meetingService.updateMeeting(inputMeetingDTO);
        Mockito.verify(meetingRepository, Mockito.times(1)).save(any(Meeting.class));
        assertThat(meetingDTO).isEqualTo(outputMeetingDTO);
    }

    @Test
    void givenNoMeetingWithSpecifiedId_whenDeleteMeeting_thenThrowMeetingNotFoundException() {

        Mockito.when(meetingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Mockito.verify(meetingRepository, Mockito.never()).delete(any(Meeting.class));
        assertThrows(MeetingNotFoundException.class, () -> meetingService.deleteMeeting(meeting.getId()));
    }

    @Test
    void givenMeetingWithSpecifiedId_whenDeleteMeeting_thenDeleteMeeting() {

        Mockito.when(meetingRepository.findById(any(Integer.class))).thenReturn(Optional.of(meeting));
        
        assertDoesNotThrow(() -> meetingService.deleteMeeting(meeting.getId()));
    }
}
