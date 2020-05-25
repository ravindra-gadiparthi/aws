package org.cloudcafe.aws.chapter3.resources;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class AWSExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        log.error(e.getLocalizedMessage(), e);
        model.addAttribute("exception", e.getClass().getName());
        model.addAttribute("message", e.getMessage());
        model.addAttribute("stack", Objects.toString(e.getStackTrace()));
        return "error";
    }
}
