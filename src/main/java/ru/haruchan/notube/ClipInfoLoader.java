package ru.haruchan.notube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class ClipInfoLoader {
	private static final Logger log = LoggerFactory.getLogger(ClipInfoLoader.class);
	private static final Marker FLASHVARS_MARKER = MarkerFactory.getMarker("flashvars");
	private static final Marker URLMAP_MARKER = MarkerFactory.getMarker("urlmap");

	private static final String CLIP_TITLE_VAR = "title";

	public ClipInfo loadInfo(final String clipId) throws ParsingException, IOException {
		final Map<String, String> varMap = getFlashvarsMap(clipId);
		if (!clipId.equals(varMap.get("video_id"))) {
			throw new ParsingException("Clip ID in the page script is different from the user-supplied one");
		}
		final Map<String, String> urlMap = new HashMap<String, String>();
		final Map<String, String> fmtMap = new HashMap<String, String>();
		parseFmt(varMap.get("fmt_list"), varMap.get("url_encoded_fmt_stream_map"), fmtMap, urlMap);
		return new ClipInfo(
				clipId,
				varMap.get(CLIP_TITLE_VAR),
				fmtMap,
				urlMap
		);
	}

	private void parseFmt(
			final String fmtMapString, final String urlMapString,
			final Map<String, String> fmtMap, final Map<String, String> urlMap
	) {
		final String[] fmtEntries = fmtMapString.split(",");
		final String[] urlEntries = urlMapString.substring(4).split(",url=");
		for (int entry=0; entry < urlEntries.length; entry++) {
			final String url = percentDecode(urlEntries[entry]);
			final String[] parts = fmtEntries[entry].split("/",2);
			final String formatId = parts[0];
			final String formatDesc = parts[1];
			fmtMap.put(formatId, formatDesc);
			urlMap.put(formatId, url);
			if (log.isDebugEnabled(URLMAP_MARKER)) {
				log.debug(URLMAP_MARKER, "Parsed format: id={}, description={}", formatId, formatDesc);
				log.debug(URLMAP_MARKER, "Format URL: {}", url);
			}
		}
	}

	private Map<String, String> getFlashvarsMap(final String clipId)
			throws IOException, ParsingException {
		final String pageContent = loadClipPage(clipId);
		final Matcher matcher = Pattern.compile("flashvars=\"([^\"]*)\"")
				.matcher(pageContent);
		if (!matcher.find()) {
			throw new ParsingException("flashvars not found");
		}
		final String flashvarsString = matcher.group(1);
		final String[] vars = flashvarsString.split("&amp;");
		final Map<String, String> varMap = new HashMap<String, String>();
		for (final String var : vars) {
			final String[] parts = var.split("=", 2);
			final String varName = parts[0];
			final String varValue = percentDecode(parts[1]);
			varMap.put(varName, varValue);
		}
		varMap.put(CLIP_TITLE_VAR, getClipTitle(pageContent));
		if (log.isDebugEnabled(FLASHVARS_MARKER)) {
			log.debug(FLASHVARS_MARKER, "Listing flashvars");
			final List<String> varKeys = new ArrayList<String>(varMap.keySet());
			Collections.sort(varKeys);
			for (final String varKey : varKeys) {
				log.debug(FLASHVARS_MARKER, "{}={}", varKey, varMap.get(varKey));
			}
		}
		return varMap;
	}

	private String getClipTitle(final String clipPageSource) {
		final Matcher matcher = Pattern.compile(
				"<meta name=\"title\" content=\"([^\"]+)\">"
		).matcher(clipPageSource);
		return matcher.find() ? matcher.group(1) : "(failed to extract title)";
	}

	private String percentDecode(final String src) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < src.length()) {
			final char c = src.charAt(i);
			if (c != '%') {
				sb.append(c);
				i++;
				continue;
			}
			final int encodedByte = Integer.parseInt(src.substring(i+1, i+3), 16);
			sb.append((char) encodedByte);
			i += 3;
		}
		return sb.toString();
	}

	private String loadClipPage(final String clipId) throws IOException {
		final URL url = new URL("http://www.youtube.com/watch?v=" + clipId);
		final InputStreamReader reader = new InputStreamReader(
				url.openStream(), "UTF-8"
		);
		try {
			final StringBuilder sb = new StringBuilder();
			final char[] cbuf = new char[8192];
			int l;
			while ((l = reader.read(cbuf)) != -1) {
				sb.append(cbuf, 0, l);
			}
			return sb.toString();
		} finally {
			reader.close();
		}
	}
}
