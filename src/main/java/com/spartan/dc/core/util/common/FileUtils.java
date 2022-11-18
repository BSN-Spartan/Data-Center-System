package com.spartan.dc.core.util.common;

import com.spartan.dc.core.enums.SystemConfIconPathEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @Author : wjx
 */
public class FileUtils {

    public static void uplod(HttpServletRequest request,MultipartFile file){
        // File upload is to upload to the absolute path, "upload" is the directory where the image is stored, get the file extension and confirm the file type
        String path = request.getSession().getServletContext().getRealPath(SystemConfIconPathEnum.ICON_PATH.getName());
        // Get the file name
        String fileName = file.getOriginalFilename();
        File targetFile = new File(path, fileName);

        if (!targetFile.exists()) {
            targetFile.mkdirs();
            // If an "access denied" error occurs when creating a file, modify the above code to the next two lines of code, and then the program can run successfully.
//		    /*targetFile.getParentFile().mkdirs();
//            targetFile.createNewFile();
        }
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * file：Query the specific location of the file in the library
    * */
    public static void delete(String file){
            File file2 = new File(file);
            file2.delete();
    }
}
