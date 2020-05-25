package org.cloudcafe.aws.chapter3.resources;


import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.chapter3.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;


@Controller
@Slf4j
public class ImageController {

    private static final String API_BASE_PATH = "/images";
    private static final String FILE_NAME = "{fileName:.+}";

    @Autowired
    private ImageService imageService;

    @GetMapping("/")
    public String findAll(Model model) {

        model.addAttribute("images", imageService.findAll());
        model.addAttribute("extra", "Welcome to Cloud Cafe");
        return "index";
    }

    @GetMapping(value = API_BASE_PATH + "/" + FILE_NAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> findOneImage(@PathVariable String fileName) {

        S3ObjectInputStream resource = imageService.findOneImage(fileName);
        return ResponseEntity.ok().body(new InputStreamResource(resource));
    }


    @PostMapping(value = API_BASE_PATH)
    public String createFile(@RequestPart(name = "file") MultipartFile file) {
        imageService.createImage(file);
        return "redirect:/";
    }

    @RequestMapping(value = API_BASE_PATH + "/" + FILE_NAME, method = DELETE)
    public String deleteImage(@PathVariable("fileName") String fileName) {
        imageService.deleteImage(fileName);
        return "redirect:/";
    }
}
