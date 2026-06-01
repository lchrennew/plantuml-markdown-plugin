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
            String source = plantUmlCode.trim();
            StringBuilder modifiedSource = new StringBuilder();
            
            if (!source.startsWith("@start")) {
                // 如果没有 @startuml，添加它和 Smetana pragma
                modifiedSource.append("@startuml\n!pragma layout smetana\n").append(source).append("\n@enduml");
            } else {
                // 如果已经有 @start 开头，在这之后添加 Smetana pragma
                int newlineIndex = source.indexOf('\n');
                if (newlineIndex == -1) {
                    // 没有换行符，只有一行
                    if (!source.toLowerCase().contains("!pragma layout")) {
                        modifiedSource.append(source).append("\n!pragma layout smetana");
                    } else {
                        modifiedSource.append(source);
                    }
                } else {
                    // 有换行符，检查第一行后面是否有布局 pragma
                    String firstLine = source.substring(0, newlineIndex);
                    String rest = source.substring(newlineIndex + 1);
                    modifiedSource.append(firstLine).append("\n");
                    
                    if (!source.toLowerCase().contains("!pragma layout")) {
                        modifiedSource.append("!pragma layout smetana\n");
                    }
                    modifiedSource.append(rest);
                }
            }
            
            LOG.info("Rendering PlantUML diagram with source:\n" + modifiedSource);
            
            SourceStringReader reader = new SourceStringReader(modifiedSource.toString());
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
