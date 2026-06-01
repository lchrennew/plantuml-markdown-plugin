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
            // 强制使用 Smetana 布局引擎，避免依赖 GraphViz
            // 通过系统属性 -Playout=smetana 来设置
            String originalLayout = System.getProperty("layout");
            System.setProperty("layout", "smetana");
            
            try {
                String source = plantUmlCode.trim();
                if (!source.startsWith("@start")) {
                    source = "@startuml\n" + source + "\n@enduml";
                }
                
                SourceStringReader reader = new SourceStringReader(source);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
                os.close();
                byte[] imageBytes = os.toByteArray();
                return Base64.getEncoder().encodeToString(imageBytes);
            } finally {
                // 恢复原来的系统属性
                if (originalLayout != null) {
                    System.setProperty("layout", originalLayout);
                } else {
                    System.clearProperty("layout");
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to render PlantUML diagram", e);
            return null;
        }
    }
}
