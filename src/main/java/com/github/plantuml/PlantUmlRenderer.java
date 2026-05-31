package com.github.plantuml;

import com.intellij.openapi.diagnostic.Logger;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public final class PlantUmlRenderer {

    private static final Logger LOG = Logger.getInstance(PlantUmlRenderer.class);

    private PlantUmlRenderer() {
    }

    public static String renderToBase64Png(String plantUmlCode) {
        try {
            String source = plantUmlCode.trim();
            if (!source.startsWith("@start")) {
                source = "@startuml\n!pragma layout smetana\n" + source + "\n@enduml";
            } else if (!source.contains("!pragma layout")) {
                source = source.replaceFirst("(@start\\w+)", "$1\n!pragma layout smetana");
            }
            SourceStringReader reader = new SourceStringReader(source);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
            os.close();
            byte[] imageBytes = os.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            LOG.warn("Failed to render PlantUML diagram", e);
            return null;
        }
    }
}
