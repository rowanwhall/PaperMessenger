package personal.rowan.paperforspotify.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class StringUtil {

	public static String urlDecode(String encodedString) {
		String decodedString;
		try {
			decodedString = URLDecoder.decode(encodedString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return encodedString;
		}
		return decodedString;
	}

	public static String urlEncode(String decodedString) {
		String encodedString;
		try {
			encodedString = URLEncoder.encode(decodedString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return decodedString;
		}
		return encodedString;
	}

}
