module moviescraper.doctord {
    requires javafx.swing;
    requires javafx.controls;
    requires commons.cli;
    requires org.apache.commons.io;
    requires java.desktop;
    requires org.apache.commons.lang3;
    requires org.jsoup;
    requires imgscalr.lib;
    requires json.io;
    requires jgoodies.forms;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.commons.csv;
    requires org.json;
    requires java.logging;
    requires org.apache.commons.codec;
    requires xstream;
    requires java.sql;
    requires org.junit.jupiter.api;
    requires annotations;
    requires org.jetbrains.annotations;
    requires commons.collections;
    requires com.github.benmanes.caffeine;
    exports moviescraper.doctord;
    exports moviescraper.doctord.controller.amalgamation;
    exports moviescraper.doctord.controller.siteparsingprofile;
    exports moviescraper.doctord.controller.siteparsingprofile.specific;
    exports moviescraper.doctord.model.dataitem;
    opens moviescraper.doctord.controller.amalgamation to json.io, xstream;
    opens moviescraper.doctord.controller.siteparsingprofile to xstream;
    opens moviescraper.doctord.controller.siteparsingprofile.specific to xstream;
    opens moviescraper.doctord.model.preferences to xstream;
    opens moviescraper.doctord.model.dataitem to xstream;
    opens moviescraper.doctord.controller.xmlserialization to xstream;
}
