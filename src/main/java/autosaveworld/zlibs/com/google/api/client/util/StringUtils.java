package autosaveworld.zlibs.com.google.api.client.util;

import java.nio.charset.StandardCharsets;

/**
 * Utilities for strings, modernisiert für Java 8+.
 */
public class StringUtils {

  /**
   * Line separator to use for this OS, i.e. {@code "\n"} or {@code "\r\n"}.
   */
  public static final String LINE_SEPARATOR = System.lineSeparator();

  /**
   * Encodes the given string into a sequence of bytes using the UTF-8 charset.
   *
   * @param string the String to encode, may be null
   * @return encoded bytes, or null if the input string was null
   */
  public static byte[] getBytesUtf8(String string) {
    if (string == null) {
      return null;
    }
    return string.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Constructs a new String by decoding the specified array of bytes using the UTF-8 charset.
   *
   * @param bytes The bytes to be decoded into characters
   * @return A new String decoded from UTF-8, or null if the input was null.
   */
  public static String newStringUtf8(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private StringUtils() {
  }
}