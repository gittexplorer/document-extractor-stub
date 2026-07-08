package com.gittexplorer.documentextractor.util;
import java.security.MessageDigest;import java.security.NoSuchAlgorithmException;import java.util.HexFormat;import org.springframework.stereotype.Component;
@Component public class HashUtility { public String sha256(byte[] bytes){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 is not available",e);}} }
