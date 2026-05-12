package autosaveworld.zlibs.com.google.api.client.util;

import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Proxy für Base64-Operationen, angepasst auf die Standard Java-API.
 */
public class Base64 {

  private static final Encoder ENCODER = java.util.Base64.getEncoder();
  private static final Encoder URL_ENCODER = java.util.Base64.getUrlEncoder().withoutPadding();
  private static final Decoder DECODER = java.util.Base64.getDecoder();
  private static final Decoder URL_DECODER = java.util.Base64.getUrlDecoder();

  public static byte[] encodeBase64(byte[] binaryData) {
    if (binaryData == null) return null;
    return ENCODER.encode(binaryData);
  }

  public static String encodeBase64String(byte[] binaryData) {
    if (binaryData == null) return null;
    return ENCODER.encodeToString(binaryData);
  }

  public static byte[] encodeBase64URLSafe(byte[] binaryData) {
    if (binaryData == null) return null;
    return URL_ENCODER.encode(binaryData);
  }

  public static String encodeBase64URLSafeString(byte[] binaryData) {
    if (binaryData == null) return null;
    return URL_ENCODER.encodeToString(binaryData);
  }

  public static byte[] decodeBase64(byte[] base64Data) {
    if (base64Data == null) return null;
    return DECODER.decode(base64Data);
  }

  public static byte[] decodeBase64(String base64String) {
    if (base64String == null) return null;
    try {
      return DECODER.decode(base64String);
    } catch (IllegalArgumentException e) {
      // Falls der String URL-Safe kodiert ist, nutzen wir den URL-Decoder als Fallback
      return URL_DECODER.decode(base64String);
    }
  }

  private Base64() {
  }
}