package com.gittexplorer.documentextractor.regex;

import com.gittexplorer.documentextractor.exception.InvalidRegexConfigurationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identifiers")
public class RegexConfiguration {
    private final Map<String, IdentifierDefinition> definitions = new LinkedHashMap<>();
    private final Map<String, Pattern> compiledPatterns = new LinkedHashMap<>();
    public Map<String, IdentifierDefinition> getDefinitions() { return definitions; }
    public Map<String, Pattern> getCompiledPatterns() { return Map.copyOf(compiledPatterns); }
    public void setDefinitions(Map<String, IdentifierDefinition> values) {
        definitions.clear(); compiledPatterns.clear();
        if (values == null) { return; }
        values.forEach((name, definition) -> {
            if (definition == null || definition.regex() == null || definition.regex().isBlank()) throw new InvalidRegexConfigurationException("Regex is required for identifier " + name);
            try { compiledPatterns.put(name, Pattern.compile(definition.regex())); definitions.put(name, definition); }
            catch (PatternSyntaxException ex) { throw new InvalidRegexConfigurationException("Invalid regex for identifier " + name, ex); }
        });
    }
    public record IdentifierDefinition(String regex) { }
}
