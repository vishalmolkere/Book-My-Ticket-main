package com.jsp.book.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CloudinaryHelper {

	private static final String MOVIE_FOLDER = "BMT-Movies";
	private static final String THEATER_FOLDER = "BMT-Theater";
	private static final String QR_FOLDER = "BMT-Theater-QR";

	private static final String FALLBACK_IMAGE = "https://placehold.co/600x400/EEE/31343C";

	public String generateImageLink(MultipartFile file) {
		return upload(file, MOVIE_FOLDER);
	}

	public String getTheaterImageLink(MultipartFile file) {
		return upload(file, THEATER_FOLDER);
	}

	public String saveTicketQr(byte[] qr) {
		return upload(qr, QR_FOLDER);
	}

	public void deleteFile(String fileUrl) {
		if (fileUrl == null || fileUrl.isEmpty() || fileUrl.equals(FALLBACK_IMAGE)) {
			return;
		}
		if (fileUrl.startsWith("/uploads/")) {
			String relativePath = fileUrl.substring(1); // remove leading slash -> "uploads/BMT-Movies/filename.jpg"
			try {
				Path path = Paths.get(relativePath);
				Files.deleteIfExists(path);
			} catch (IOException e) {
				System.err.println("Failed to delete local file: " + fileUrl + " - " + e.getMessage());
			}
		}
	}

	/* ---------- Private helpers ---------- */

	private String upload(MultipartFile file, String folder) {
		if (file == null || file.isEmpty()) {
			return FALLBACK_IMAGE;
		}
		try {
			return upload(file.getBytes(), folder, file.getOriginalFilename());
		} catch (IOException e) {
			return FALLBACK_IMAGE;
		}
	}

	private String upload(byte[] data, String folder) {
		return upload(data, folder, "qr.png");
	}

	private String upload(byte[] data, String folder, String originalFilename) {
		try {
			File directory = new File("uploads/" + folder);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// Generate a unique filename to avoid conflicts
			String cleanFilename = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_") : "file";
			String uniqueFilename = UUID.randomUUID().toString() + "_" + cleanFilename;

			File targetFile = new File(directory, uniqueFilename);
			try (FileOutputStream fos = new FileOutputStream(targetFile)) {
				fos.write(data);
			}

			// Return the web-accessible URL path
			return "/uploads/" + folder + "/" + uniqueFilename;
		} catch (IOException e) {
			return FALLBACK_IMAGE;
		}
	}
}
