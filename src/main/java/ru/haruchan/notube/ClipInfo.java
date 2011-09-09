package ru.haruchan.notube;

import java.util.*;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class ClipInfo {

	private final String clipId;
	private final String clipTitle;
	private final Map<String, ClipFormat> formats = new HashMap<String, ClipFormat>();

	public ClipInfo(
			final String clipId,
			final String clipTitle,
			final Map<String, String> fmtMap,
			final Map<String, String> fmtUrlMap
	) throws ProcessingException {
		this.clipId = clipId;
		this.clipTitle = clipTitle;
		for (final String formatId : fmtMap.keySet()) {
			final String untrimmedUrl = fmtUrlMap.get(formatId);
			final int urlEnd = untrimmedUrl.indexOf("&quality=");
			if (urlEnd == -1) {
				throw new ProcessingException("Clip URL hack failed: couldn't find URL end marker");
			}
			final String url = untrimmedUrl.substring(0, urlEnd);
			// todo: get more useful info from trimmed part of url
			formats.put(
					formatId,
					new ClipFormat(
							formatId,
							fmtMap.get(formatId),
							url
					)
			);
		}
	}

	public String getClipId() {
		return clipId;
	}

	public String getClipTitle() {
		return clipTitle;
	}

	public List<ClipFormat> getFormats() {
		return new ArrayList<ClipFormat>(formats.values());
	}

	public ClipFormat getFormat(final String id) {
		return formats.get(id);
	}

}
