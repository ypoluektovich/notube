package ru.haruchan.notube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles subtitle retrieval for YouTube clips.
 *
 * @author hi117 (no email yet)
 */
public class ClipSubsLoader {
	private static final Logger log = LoggerFactory.getLogger(ClipInfoLoader.class);

	/**
	 * Saves all available subtitles for the given clip to disk in SRT format. Resulting files are
	 * named <code>&lt;clip_id&gt;.&lt;language_code&gt;.srt</code>, where:
	 * <ul>
	 *     <li><code>clip_id</code> is the YouTube clip hash-identifier;</li>
	 *     <li><code>language_code</code> is something like "en" or "ru" or whatever else is
	 *     available.</li>
	 * </ul>
	 *
	 * @param clipId the YouTube 11-symbol hash-id of the clip in question.
	 *
	 * @throws IOException if I/O errors happen while saving the subs.
	 * @throws ParsingException if YouTube incompatibly changes the format of subtitles.
	 */
	public void getSubs(final String clipId) throws IOException, ParsingException {
		final List<String> langs = new ArrayList<String>();

		/*
		 * format for raw response is 
		 * <track id="0" name="" lang_code="en" lang_original="English" lang_translated="English" lang_default="true"/>
		 * all we need is the lang_code though
		 */
		final Matcher match = Pattern.compile("lang_code=\"(\\w++)\"").matcher(getLangsRaw(clipId));
		while (match.find()) {
			final String lang = match.group(1);
			langs.add(lang);
			log.info("Subs available in: " + lang);
		}
		for (final String lang : langs) {
			final String subsRaw = getSubsRaw(clipId, lang);

			final Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(clipId + "." + lang + ".srt"),
					"UTF-8"
			));
			try {
				out.write(convert(subsRaw));
			} finally {
				out.close();
			}
		}
	}

	private String getLangsRaw(final String clipId) throws IOException {
		return retrieveUrl(new URL("http://www.youtube.com/api/timedtext?type=list&v=" + clipId));
	}

	private String getSubsRaw(final String clipId, final String lang) throws IOException {
		return retrieveUrl(new URL("http://www.youtube.com/api/timedtext?&v=" + clipId + "&lang=" + lang));
	}

	private String retrieveUrl(final URL url) throws IOException {
		final Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
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

	private String convert(final String youtube) throws ParsingException {
		/*
		 * ok, an SRT sub is formatted like this:
		 *
		 * 1
		 * 00:03:09,365 --> 00:03:12,034
		 * Maycomb was a tired old town...
		 *
		 * spacing/newlines are important
		 *
		 * youtube's subtitles are:
		 * <text start="7.66" dur="2.03">Я торжествую!</text>
		 *
		 */
		// here be dragons
		final Matcher startR = Pattern.compile("start=\"([0-9.]++)\"").matcher(youtube);
		final Matcher durR = Pattern.compile("dur=\"([0-9.]++)\"").matcher(youtube);
		final Matcher textR = Pattern.compile(">([^<]*+)</text>").matcher(youtube);
		final StringBuilder build = new StringBuilder();
		int i = 0;
		while (startR.find()){
			i++;
			//log.debug(startR.group());
			durR.find();
			//log.debug(durR.group());
			textR.find();
			//log.debug(textR.group());
			//log.debug(build);
			build.append(i).append("\n")
					.append(convertTime(startR.group(1))).append(" --> ")
					.append(convertTime(durR.group(1))).append("\n")
					.append(textR.group(1)).append("\n\n");
		}
		return build.toString();
	}

	private String convertTime(final String secondsS) throws ParsingException {
		final String[] saa = secondsS.split("\\.");
		// sanity check!
		if (saa.length > 2) {
			throw new ParsingException("Illegal format of time: " + secondsS);
		}
		final int rseconds = Integer.parseInt(saa[0]);
		final int seconds = rseconds % 60;
		final int cseconds = (saa.length == 2) ? Integer.parseInt(saa[1]) : 0;
		final int hours = (rseconds / 60) / 60;
		final int minutes = rseconds / 60 - hours * 60;
		return hours + ":" + minutes + ":" + seconds + "," + cseconds;
	}
}