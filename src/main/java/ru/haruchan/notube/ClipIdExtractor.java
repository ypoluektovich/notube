package ru.haruchan.notube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class ClipIdExtractor {
	private static final String MAYBE_HTTP = "(?:http://)?";
	private static final String MAYBE_WWW = "(?:www\\.)?";
	private static final String CLIP_ID_REGEX = "[-\\w]{11}";

	public static String extractFrom(final String arg) {
		String r = tryNormalUrl(arg);
		if (r != null) {
			return r;
		}
		r = tryJustId(arg);
		if (r != null) {
			return r;
		}
		r = tryYoutuBe(arg);
		if (r != null) {
			return r;
		}
		return null;
	}

	private static String tryJustId(final String arg) {
		return arg.matches("^" + CLIP_ID_REGEX + "$") ? arg : null;
	}

	private static String tryNormalUrl(final String arg) {
		final Matcher matcher = Pattern.compile(
				"^" + MAYBE_HTTP + MAYBE_WWW + "youtube\\.com/watch\\?(.+)$"
		).matcher(arg);
		if (!matcher.matches()) {
			return null;
		}
		final String[] params = matcher.group(1).split("&");
		for (final String param : params) {
			final String[] parts = param.split("=", 2);
			if (parts[0].equals("v")) {
				if (parts[1].matches("^" + CLIP_ID_REGEX + "$")) {
					return parts[1];
				}
			}
		}
		return null;
	}

	private static String tryYoutuBe(final String arg) {
		final Matcher matcher = Pattern.compile(
				"^" + MAYBE_HTTP + MAYBE_WWW +
						"youtu\\.be/(" + CLIP_ID_REGEX + ")$"
		).matcher(arg);
		return matcher.matches() ? matcher.group(1) : null;
	}
}
