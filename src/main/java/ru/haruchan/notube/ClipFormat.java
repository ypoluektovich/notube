package ru.haruchan.notube;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
* @author Yanus Poluektovich (ypoluektovich@gmail.com)
*/
public class ClipFormat {
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
		final URL url = new URL(urlString);
		final InputStream in = url.openStream();
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
