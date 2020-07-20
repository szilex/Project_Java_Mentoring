package com.euvic.mentoring.config;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MeetingSerializer extends StdSerializer<Meeting> {

    public MeetingSerializer() {
        this(null);
    }

    public MeetingSerializer(Class<Meeting> t) {
        super(t);
    }

    @Override
    public void serialize(Meeting meeting, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField("id", meeting.getId());
        gen.writeStringField("date", meeting.getDate().toString());
        gen.writeStringField("startTime", meeting.getStartTime().toString());
        gen.writeStringField("endTime", meeting.getEndTime().toString());
        gen.writeNumberField("mentorId", meeting.getMentor().getId());

        User student = meeting.getStudent();
        if (student != null) {
            gen.writeNumberField("studentId", student.getId());
        }

        gen.writeEndObject();
    }
}
