package org.cloudcafe.aws.rekognition.resources;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudcafe.aws.rekognition.services.ImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
@Slf4j
public class ImageController {

    private static final String API_BASE_PATH = "/images";
    private static final String FILE_NAME = "{fileName:.+}";

    private ImageService imageService;



    @GetMapping("/")
    public Mono<String> findAllImages(Model model) {
        model.addAttribute("images", imageService.findAllImages());
        model.addAttribute("extra", "Welcome to Cloud Cafe");
        return Mono.just("index");
    }


    @GetMapping(value = API_BASE_PATH + "/" + FILE_NAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> findOneImage(@PathVariable String fileName) {
        return imageService.
                findOneImage(fileName)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .body(new InputStreamResource(resource));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.badRequest()
                                .body("Could not find image " + fileName);
                    }
                });
    }


    @PostMapping(value = API_BASE_PATH)
    public Mono<String> createFile(@RequestPart(name = "file") Flux<FilePart> files) {
        return imageService.createImage(files)
                .log("creating image on controller")
                .then(Mono.just("redirect:/"));
    }

    @DeleteMapping(value = API_BASE_PATH + "/" + FILE_NAME)
    public Mono<String> deleteImage(@PathVariable String fileName) {
        return imageService.deleteImage(fileName).then(Mono.just("redirect:/"));
    }
}
