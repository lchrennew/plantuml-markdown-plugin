package com.github.plantuml;

import org.intellij.markdown.ast.ASTNode;
import org.intellij.plugins.markdown.extensions.CodeFenceGeneratingProvider;
import org.jetbrains.annotations.NotNull;

public class PlantUmlCodeFenceProvider implements CodeFenceGeneratingProvider {

    private static final String[] SUPPORTED_LANGUAGES = {"plantuml", "puml"};

    @Override
    public boolean isApplicable(@NotNull String language) {
        for (String supported : SUPPORTED_LANGUAGES) {
            if (supported.equalsIgnoreCase(language)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @NotNull
    public String generateHtml(@NotNull String language, @NotNull String raw, @NotNull ASTNode node) {
        String base64 = PlantUmlRenderer.renderToBase64Png(raw);
        if (base64 == null) {
            return "<pre><code>" + escapeHtml(raw) + "</code></pre>";
        }
        return "<img src=\"data:image/png;base64," + base64 + "\" alt=\"PlantUML Diagram\" />";
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
