package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void write(JsonWriter out, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            out.nullValue();
        } else {
            out.value(localDateTime.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();  // Пропустить значение null
            return null;
        }
        String dateTimeString = in.nextString();
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
