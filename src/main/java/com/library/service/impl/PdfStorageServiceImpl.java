package com.library.service.impl;

import com.library.exception.BusinessRuleException;
import com.library.service.PdfStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Service
public class PdfStorageServiceImpl implements PdfStorageService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final byte[] PDF_MAGIC_BYTES = "%PDF-".getBytes();

    private final Path uploadDir;

    public PdfStorageServiceImpl(@Value("${library.upload.pdf-dir:uploads/pdf}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create PDF upload directory: " + this.uploadDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Please choose a PDF file to upload");
        }

        String originalName = file.getOriginalFilename();
        boolean lookslikePdf = PDF_CONTENT_TYPE.equalsIgnoreCase(file.getContentType())
                && originalName != null
                && originalName.toLowerCase().endsWith(".pdf");

        if (!lookslikePdf || !hasPdfSignature(file)) {
            throw new BusinessRuleException("Only PDF files can be uploaded as a book's reading copy");
        }

        String fileName = UUID.randomUUID() + ".pdf";
        try {
            file.transferTo(uploadDir.resolve(fileName));
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to store the uploaded PDF. Please try again.");
        }
        return fileName;
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(uploadDir.resolve(fileName));
        } catch (IOException e) {
            // An orphaned file on disk isn't worth failing the book save over.
        }
    }

    private boolean hasPdfSignature(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(PDF_MAGIC_BYTES.length);
            return Arrays.equals(header, PDF_MAGIC_BYTES);
        } catch (IOException e) {
            return false;
        }
    }
}
