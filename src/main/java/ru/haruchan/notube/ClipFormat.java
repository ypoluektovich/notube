package ru.haruchan.notube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
* @author Yanus Poluektovich (ypoluektovich@gmail.com)
*/
public class ClipFormat {
	private static final Logger log = LoggerFactory.getLogger(ClipFormat.class);

	private static final Map<String, String> FORMAT_EXTENSIONS;
	static {
		FORMAT_EXTENSIONS = new HashMap<String, String>();
		FORMAT_EXTENSIONS.put("5", "flv");
		FORMAT_EXTENSIONS.put("18", "mp4");
		FORMAT_EXTENSIONS.put("34", "flv");

	}

	public static String getExtension(final String id) {
		return FORMAT_EXTENSIONS.get(id);
	}

	private final String id;
	private final String description;
	private final String urlString;

	ClipFormat(final String id, final String description, final String url) {
		this.id = id;
		this.description = description;
		this.urlString = url;
	}

	public void load(final File destination) throws IOException {
		log.debug("Starting clip loading");
		final URL url = new URL(urlString);
		final URLConnection connection = url.openConnection();
		final String cookie = "VISITOR_INFO1_LIVE=cookie_hack";
		connection.setRequestProperty("Cookie", cookie);
		log.debug("Set cookie: {}", cookie);
		connection.connect();
		final InputStream in = connection.getInputStream();
		try {
			final OutputStream out = new BufferedOutputStream(
					new FileOutputStream(destination));
			try {
				byte[] buf = new byte[8192];
				int l;
				while ((l = in.read(buf)) != -1) {
					out.write(buf, 0, l);
				}
				out.flush();
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	@Override
	public String toString() {
		final String extension = getExtension(id);
		return "(" + id + " - " + (extension == null ? "?" : extension) + ") " +
				getDescription();
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
