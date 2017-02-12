package com.samuriex.analyze;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ran on 2/9/17.
 */
@RestController
public class FileController {

    @RequestMapping ("/upload")
    String test(){
        return "hro";
    }


}
