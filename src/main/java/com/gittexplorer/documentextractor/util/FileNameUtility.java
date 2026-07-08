package com.gittexplorer.documentextractor.util;
import java.util.Locale;import org.springframework.stereotype.Component;import org.springframework.util.StringUtils;
@Component public class FileNameUtility { public String extensionOf(String filename){String clean=StringUtils.cleanPath(filename==null?"":filename);int i=clean.lastIndexOf('.');return i>=0&&i<clean.length()-1?clean.substring(i+1).toLowerCase(Locale.ROOT):"";} public String safeName(String filename){return StringUtils.cleanPath(filename==null?"unknown":filename);} }
