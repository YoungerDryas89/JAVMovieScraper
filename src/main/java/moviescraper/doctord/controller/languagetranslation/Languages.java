package moviescraper.doctord.controller.languagetranslation;

public class Languages {
    public static String languageToString(Language language) {
        return switch (language) {
            case ENGLISH -> "English";
            case JAPANESE -> "日本語";
            case KOREAN -> "한국어";
            case CHINESE_SIMPLIFIED -> "汉语";
            case CHINESE -> "漢語";
            case TAIWANESE -> "Taiwanese";
            case SPANISH -> "Español";
            case MALAYSIAN -> "Bahasa Melayu";
            case THAI -> "ภาษาไทย";
            case VIETNAMESE -> "Tiếng Việt";
            case INDONESIAN -> "Bahasa Indonesia";
            case FILIPINO -> "Wikang Filipino";
            case PORTUGESE -> "Português";
            case GERMAN -> "Deutsch";
            case FRENCH -> "Français";
            case HINDI -> "हिन्दी";
        };
    }
}
