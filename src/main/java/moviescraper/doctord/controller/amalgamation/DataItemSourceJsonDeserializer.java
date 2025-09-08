package moviescraper.doctord.controller.amalgamation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;
import moviescraper.doctord.controller.siteparsingprofile.specific.SpecificProfile;
import moviescraper.doctord.model.dataitem.DataItemSource;

import java.io.IOException;

public class DataItemSourceJsonDeserializer extends JsonDeserializer<DataItemSource> {
    @Override
    public DataItemSource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String className = p.getText();
        try {
            var j = SpecificProfileFactory.getAll().stream().filter(i -> {
                return i.getParser().getParserName().equals(className);
            }).findFirst();

            if(j.isPresent())
                return j.get().getParser().createInstanceOfSameType();
            else
                throw new IOException(className + " is not present");
        } catch (Exception e) {
            throw new IOException("Could not deserialize DataItemSource: " + className, e);
        }
    }
}