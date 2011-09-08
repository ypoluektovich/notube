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
	) {
		this.clipId = clipId;
		this.clipTitle = clipTitle;
		for (final String formatId : fmtMap.keySet()) {
			formats.put(
					formatId,
					new ClipFormat(
							formatId,
							fmtMap.get(formatId),
							fmtUrlMap.get(formatId)
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
