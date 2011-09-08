package ru.haruchan.notube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Yanus Poluektovich (ypoluektovich@gmail.com)
 */
public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		if (args.length > 0) {
			log.info("Running in CLI mode");
			processCommandLine(args);
		} else {
			log.info("Running in GUI mode");
			try {
				new Main().startGUI();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private static void processCommandLine(final String[] args) {
		final String clipId = ClipIdExtractor.extractFrom(args[0]);
		if (clipId == null) {
			System.out.println("Couldn't recognize a YouTube clip id");
			return;
		}
		System.out.println("Clip ID: " + clipId);
		try {
			final ClipInfo info = new ClipInfoLoader().loadInfo(clipId);
			System.out.println("Title: " + info.getClipTitle());
			if (args.length > 1) {
				final ClipFormat format = info.getFormat(args[1]);
				if (format == null) {
					System.out.println("No format with identifier: " + args[1]);
				} else {
					System.out.printf(
							"Selected format: %s%n",
							format
					);
					System.out.println("Loading...");
					format.load(new File(clipId + "." + format.getId()));
					System.out.println("Done!");
					return;
				}
			}
			System.out.println("Available formats:");
			for (final ClipFormat format : info.getFormats()) {
				System.out.println(
						format.getId() + "\t" + format.getDescription()
				);
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	private final ActionListener loadActionListener = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			run();
		}
	};
	private final Image iconImage;
	private final JFileChooser fileChooser = new JFileChooser(".");

	public Main() throws IOException {
		iconImage = loadIcon();
	}

	private void startGUI() throws AWTException {
		if (SystemTray.isSupported()) {
			final TrayIcon trayIcon = new TrayIcon(
					iconImage,
					"NoTube - click to download a clip",
					createPopup()
			);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(loadActionListener);
			SystemTray.getSystemTray().add(trayIcon);
		} else {
			JOptionPane.showMessageDialog(
					null,
					"System tray is not supported by your JVM.\n" +
							"You will have to restart the app for each download.",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			run();
		}
	}

	private void run() {
		final ClipInfo clipInfo = promptForClipInfo();
		if (clipInfo == null) {
			JOptionPane.showMessageDialog(
					null,
					"Couldn't recognize a YouTube clip id",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		final ClipFormat format = promptForFormat(clipInfo);
		if (format == null) {
			return;
		}
		int dialogResult = fileChooser.showSaveDialog(null);
		if (dialogResult == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(
					null,
					"An error occured while trying to show file chooser.",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
		}
		if (dialogResult != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			format.load(fileChooser.getSelectedFile());
			JOptionPane.showMessageDialog(null, "Done!");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					null,
					e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
		}
	}

	private ClipFormat promptForFormat(final ClipInfo clipInfo) {
		final List<ClipFormat> formats = clipInfo.getFormats();
		return (ClipFormat) JOptionPane.showInputDialog(
				null,
				buildFormatChoosePrompt(clipInfo),
				"Select format",
				JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(iconImage),
				formats.toArray(),
				formats.get(0)
		);
	}

	private ClipInfo promptForClipInfo() {
		final String clipString = JOptionPane.showInputDialog("What clip do you want to load?");
		if (clipString == null) {
			return null;
		}
		final String clipId = ClipIdExtractor.extractFrom(clipString);
		if (clipId == null) {
			return null;
		}
		try {
			return new ClipInfoLoader().loadInfo(clipId);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private String buildFormatChoosePrompt(final ClipInfo clipInfo) {
		return "Clip ID: " + clipInfo.getClipId() +
				"\nTitle: " + clipInfo.getClipTitle() +
				"\n\nSelect format to download";
	}

	private PopupMenu createPopup() {
		final PopupMenu menu = new PopupMenu();
		menu.add(new MenuItem("Load clip...")).addActionListener(
				loadActionListener
		);
		menu.add(new MenuItem("Quit")).addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						System.exit(0);
					}
				}
		);
		return menu;
	}

	private Image loadIcon() throws IOException {
		return ImageIO.read(getClass().getResourceAsStream("/youtube-icon-48.png"));
	}


}
