package com.github.youngerdryas89.moviescraper.controller.siteparsingprofile

import arrow.core.Either
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.youngerdryas89.moviescraper.controller.languagetranslation.Language
import java.io.File
import java.io.FileOutputStream

data class SettingsProfile(
    @JsonIgnore
    val version: Int = 1,
    val globalSettings: SpecificProfileSettings,
    val settings: HashMap<SpecificProfileInfo, SpecificProfileSettings>

)


suspend fun saveData(settingsProfile: SettingsProfile) : Either<Throwable, Unit> {
    return Either.catch {
        FileOutputStream("preferences.json").use { stream ->
            val mapper = createConfiguredObjectMapper()

            val module = SimpleModule()
            mapper.registerModule(module)

            val root = mapper.createObjectNode()

            root.put("version", settingsProfile.version)
            val settings = root.putObject("settings")
            settings.putPOJO("globalSettings", settingsProfile.globalSettings)

            val specific = settings.putObject("specificSettings")
            settingsProfile.settings.forEach { (key, value) ->
                specific.putPOJO(key.name, value)
            }

            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(root)
            stream.write(json)

        }
    }
}


suspend fun loadData() : Either<Throwable,SettingsProfile> {
    val fin = File("preferences.json").readText()
    val mapper = createConfiguredObjectMapper()

    val root = mapper.readTree(fin)

    if(!root.has("version"))
        return Either.Left(RuntimeException("ERROR: Configuration missing node: 'version' from preferences.json"))

    if(!root.get("version").isInt)
        return Either.Left(RuntimeException("ERROR: Expected Int from 'version' node in preferences.json"))

    val version = root.get("version").asInt();

    if(!root.has("globalSettings"))
        return Either.Left(RuntimeException("ERROR: Configuration missing node: 'globalSettings' from preferences.json"))

    val globalSettingsNode = root.get("globalSettings")

    if(!globalSettingsNode.isObject)
        return Either.Left(RuntimeException("ERROR: Expected Object from 'globalSettings' node in preferences.json"))

    val globalSettings = mapper.convertValue(globalSettingsNode, SpecificProfileSettings::class.java)

    if(root.has("specificSettings")) {
        val specificSettingsNode = root.get("specificSettings")
        if(!specificSettingsNode.isArray)
            return Either.Left(RuntimeException("ERROR: Expected Array from 'specificSettings' node in preferences.json"))

        val specificsReturn : HashMap<SpecificProfileInfo, SpecificProfileSettings> = hashMapOf()
        val keyData = specificSettingsNode.

        return Either.Right(SettingsProfile(version, globalSettings, specifics)
    }



}


fun defaultSettings(preferredLanguageSelection: Language) : SpecificProfileSettings {
    return SpecificProfileSettings(
        art = ArtSettings(),
        naming = NamingSettings(),
        scraper = ScraperSettings(preferredLanguage = preferredLanguageSelection)
    )
}

fun createConfiguredObjectMapper() : ObjectMapper {

    val mapper = ObjectMapper()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    val module = SimpleModule()
    mapper.registerModule(module)

    return mapper
}