package com.znahodochka.backend.storage.controller;

import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateAttachmentDto;
import com.znahodochka.backend.storage.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/storage")
@RestController
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('BASIC')")
    public AttachmentDto upload(
            @RequestParam("file") MultipartFile file,
            final UsernamePasswordAuthenticationToken token) throws IOException {
        Long userId = (Long) token.getPrincipal();
        return attachmentService.upload(file, userId);
    }

    @GetMapping("/publication/{publicationId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<AttachmentDto> getByPublication(@PathVariable UUID publicationId) {
        return attachmentService.getByPublicationId(publicationId);
    }

    @PutMapping("/publication/{publicationId}/attachments")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void updateAttachments(
            @PathVariable UUID publicationId,
            @RequestBody List<UpdateAttachmentDto> attachments,
            final UsernamePasswordAuthenticationToken token) {
        attachmentService.updatePublicationAttachments(publicationId, attachments, (Long) token.getPrincipal());
    } //такое же сделать для обсуждений

    @GetMapping("/download/{fileUri}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileUri) {
        Resource file = attachmentService.downloadFile(fileUri);
        String ext = fileUri.substring(fileUri.lastIndexOf('.') + 1).toLowerCase();
        MediaType mediaType = switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(file);
    }

}
