package com.github.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class TestRender {
    public static void main(String[] args) {
        try {
            // Test a simple sequence diagram
            String plantUmlCode = """
                @startuml
                Bob -> Alice : hello
                Alice -> Bob : hi
                @enduml
                """;
            
            // 直接测试渲染，不依赖 IntelliJ
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
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                
                if (base64 != null) {
                    System.out.println("Successfully rendered PlantUML! Base64 length: " + base64.length());
                    
                    // Write to a file to view
                    try (FileOutputStream fos = new FileOutputStream("/tmp/test_diagram_1_2026_5.png")) {
                        fos.write(imageBytes);
                        System.out.println("Saved to /tmp/test_diagram_1_2026_5.png");
                    }
                } else {
                    System.err.println("Failed to render");
                }
            } finally {
                // Restore original property
                if (originalLayout != null) {
                    System.setProperty("layout", originalLayout);
                } else {
                    System.clearProperty("layout");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
