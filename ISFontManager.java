package typewriter;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class ISFontManager {

    public static String[] getAvailableFontFamilyNames() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getAvailableFontFamilyNames();
    }

    public static Font[] getAllFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getAllFonts();
    }

    
    /* Unused for now but very much planned to be used in the future */
    public static Font getFontFromFamilyArray(String[] familyNames, int index, int fontSize) {
        if (familyNames == null || index < 0 || index >= familyNames.length) {
            return null;
        }
        return new Font(familyNames[index], Font.PLAIN, fontSize);
    }

    public static Font getFontFromFontArray(Font[] fonts, int index, int fontSize) {
        if (fonts == null || index < 0 || index >= fonts.length) {
            return null;
        }
        return fonts[index].deriveFont((float) fontSize);
    }

}