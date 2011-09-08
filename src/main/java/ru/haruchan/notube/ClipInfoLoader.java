package ru.haruchan.notube;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class ClipInfoLoader {
	private static final String CLIP_TITLE_VAR = "title";

	public ClipInfo loadInfo(final String clipId)
									throws ParsingException, IOException {
					final Map<String, String> varMap = getFlashvarsMap(clipId);
					if (!clipId.equals(varMap.get("video_id"))) {
									throw new ParsingException("Clip ID in the page script is different from the user-supplied one");
					}
					final Map<String, String> urlMap = new HashMap<String, String>();
					final Map<String, String> fmtMap = new HashMap<String, String>();
					parseFmt(varMap.get("url_encoded_fmt_stream_map"), varMap.get("fmt_list"), urlMap, fmtMap);
					return new ClipInfo(
													clipId,
													varMap.get(CLIP_TITLE_VAR),
													fmtMap,
													urlMap
					);
	}

	private void parseFmt(
	final String mapURLString, 
	final String mapFMTString, 
	final Map<String, String> mapURL, 
	final Map<String, String> mapFMT
	) {
					final String[] entriesURL = mapURLString.substring(4).split(",url=");
					final String[] entriesFMT = mapFMTString.split(",");
					for (int entry=0;entry<entriesURL.length;entry++) {
									final String url = percentDecode(entriesURL[entry]);
									final String[] partsFMT = entriesFMT[entry].split("/",2);
									mapURL.put(partsFMT[0], url);
									mapFMT.put(partsFMT[0], partsFMT[1]);
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
