package org.cloudcafe.aws.chapter7.resources;


import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.chapter7.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;


@Controller
@Slf4j
public class ImageController {

    private static final String API_BASE_PATH = "/images";
    private static final String FILE_NAME = "{fileName:.+}";

    @Autowired
    private ImageService imageService;


    @Value("${logoutUrl}")
    private String logoutUrl;

    @GetMapping("/")
    public String findByUsername(Model model, Principal principal) {

        model.addAttribute("images", imageService.findByUsername(principal.getName()));
        model.addAttribute("extra", "Welcome to Cloud Cafe");
        return "index";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:" + logoutUrl;
    }

    @GetMapping(value = API_BASE_PATH + "/" + FILE_NAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> findOneImage(@PathVariable String fileName, Principal principal) {

        S3ObjectInputStream resource = imageService.findOneImage(fileName, principal.getName());
        try {
            return ResponseEntity.ok()
                    .body(new InputStreamResource(resource));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Could not find image " + fileName);
        }
    }


    @PostMapping(value = API_BASE_PATH)
    public String createFile(@RequestPart(name = "file") MultipartFile file, Principal principal) {
        imageService.createImage(file, principal.getName());
        return "redirect:/";
    }

    @RequestMapping(value = API_BASE_PATH + "/" + FILE_NAME, method = DELETE)
    public String deleteImage(@PathVariable("fileName") String fileName,Principal principal) {
        imageService.deleteImage(fileName,principal.getName());
        return "redirect:/";
    }
}
