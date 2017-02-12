package com.samuriex.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by ran on 2/11/17.
 */
public class FileUtils {

    public File multipartToFile(MultipartFile multipart, String newFilesName) throws IllegalStateException, IOException
    {
        File convFile = new File( newFilesName!=null? newFilesName:multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }
}
