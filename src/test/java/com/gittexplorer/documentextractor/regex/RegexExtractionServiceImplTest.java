package com.gittexplorer.documentextractor.regex;
import static org.assertj.core.api.Assertions.assertThat;
import com.gittexplorer.documentextractor.service.impl.RegexExtractionServiceImpl;import java.util.Map;import org.junit.jupiter.api.Test;
class RegexExtractionServiceImplTest { @Test void extractsConfiguredIdentifiersAndRemovesDuplicates(){RegexConfiguration cfg=new RegexConfiguration();cfg.setDefinitions(Map.of("EMAIL",new RegexConfiguration.IdentifierDefinition("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")));var svc=new RegexExtractionServiceImpl(cfg);assertThat(svc.extractIdentifiers("a@test.com a@test.com b@test.com").get("EMAIL")).containsExactly("a@test.com","b@test.com");} }
