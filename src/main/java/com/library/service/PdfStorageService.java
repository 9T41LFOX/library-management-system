package com.library.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Stores and removes the PDF files attached to books for the online book
 * reader feature. Kept separate from BookService because it deals with
 * filesystem I/O rather than persistence - BookServiceImpl delegates to it.
 */
public interface PdfStorageService {

    /**
     * Validates that the given file is a genuine PDF and stores it under the
     * configured upload directory with a generated, collision-free name.
     *
     * @param file the uploaded file (must not be null or empty)
     * @return the generated file name the content was stored under
     * @throws com.library.exception.BusinessRuleException if the file is
     *                                                       missing, empty,
     *                                                       not a PDF, or
     *                                                       could not be
     *                                                       written to disk
     */
    String store(MultipartFile file);

    /**
     * Deletes a previously stored PDF by its generated file name. Safe to
     * call with null/blank input or a file that no longer exists.
     */
    void delete(String fileName);
}
