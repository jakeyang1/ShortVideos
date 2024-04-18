package org.example.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.Result;
import org.example.utils.SystemConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    @GetMapping("blog")
    public Result uploadImage(@RequestParam("file")MultipartFile image) {

        try {

            String originalFilename = image.getOriginalFilename();
            String fileName = createNewFileName(originalFilename);
            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName));

            log.debug("文件返回成功， {}", fileName);

            return Result.ok(fileName);


        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);

        }
    }

        @GetMapping("/blog/delete")
                public Result deleteBlogImg(@RequestParam("name") String filename){

            File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, filename);
            if (file.isDirectory()) {
                return  Result.fail("失败的错误名称");
            }

            FileUtil.del(file);
            return  Result.ok();

        }


        public String createNewFileName(String orignalFilename){
           String suffix = StrUtil.subAfter(orignalFilename, ".", true);

           String name = UUID.randomUUID().toString();

           int hash = name.hashCode();
           int d1 = hash & 0xF;
           int d2 = (hash >>4) & 0xF;

          File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/blog/{}/{}", d1, d2));
           if (!dir.exists()) {
               dir.mkdirs();
           }

           return String.format("blogs/{}/{}/{}.{}", d1, d2, name,suffix);

        }
    }

