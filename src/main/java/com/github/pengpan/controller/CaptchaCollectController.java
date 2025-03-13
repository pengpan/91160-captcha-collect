package com.github.pengpan.controller;

import com.github.pengpan.entity.CaptchaCollect;
import com.github.pengpan.request.CaptchaCollectRequest;
import com.github.pengpan.service.CaptchaCollectService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 验证码采集表 前端控制器
 * </p>
 *
 * @author pengpan
 * @since 2025-01-23 22:50:49
 */
@Controller
@RequestMapping("/captchaCollect")
public class CaptchaCollectController {

    private final RateLimiter rateLimiter = RateLimiter.create(5);

    @Resource
    private CaptchaCollectService captchaCollectService;

    @PostMapping("/save")
    @ResponseBody
    public String save(@RequestBody CaptchaCollectRequest request) {
        if (!rateLimiter.tryAcquire()) {
            return "Too many requests, try again later.";
        }

        if (!StringUtils.hasText(request.getImage())) {
            return "image can't be empty";
        }
        if (!StringUtils.hasText(request.getCode())) {
            return "code can't be empty";
        }

        CaptchaCollect captchaCollect = new CaptchaCollect();
        captchaCollect.setImage(request.getImage());
        captchaCollect.setCode(request.getCode());

        boolean save = captchaCollectService.save(captchaCollect);

        return save ? "success" : "failed";
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> export(String start, String end) throws Exception{

        LocalDateTime startTime = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
        LocalDateTime endTime = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(LocalTime.MAX);

        List<CaptchaCollect> list = captchaCollectService.lambdaQuery()
                .ge(CaptchaCollect::getCreateTime, startTime)
                .le(CaptchaCollect::getCreateTime, endTime)
                .list();

        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        } else {
            dataFolder.delete();
            dataFolder.mkdirs();
        }

        for (CaptchaCollect captchaCollect : list) {
            saveImageToFile(captchaCollect.getImage(), captchaCollect.getCode(), dataFolder);
        }



        byte[] zipBytes = zipFiles(dataFolder);

        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(bais));
    }

    private void saveImageToFile(String base64Image, String code, File folder) throws IOException, NoSuchAlgorithmException {
        byte[] decodedBytes = Base64.decodeBase64(base64Image.split(",")[1]);
        String hashValue = generateHash(decodedBytes);
        String fileName = code + "_" + hashValue + ".png";
        Path path = Paths.get(folder.getAbsolutePath(), fileName);

        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(decodedBytes);
        }
    }

    private String generateHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().substring(0, 8); // 取前8位作为哈希值
    }

    private byte[] zipFiles(File folder) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(folder.toPath())
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(folder.toPath().relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return baos.toByteArray();
    }
}
