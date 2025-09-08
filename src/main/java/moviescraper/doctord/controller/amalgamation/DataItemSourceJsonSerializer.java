package moviescraper.doctord.controller.amalgamation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import moviescraper.doctord.model.dataitem.DataItemSource;

import java.io.IOException;

public class DataItemSourceJsonSerializer extends JsonSerializer<DataItemSource> {
    @Override
    public void serialize(DataItemSource value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getDataItemSourceName());
    }
}