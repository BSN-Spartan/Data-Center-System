package com.spartan.dc.core.util.common;


import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {

    private FileUtil() {
    }


    public static void writeContent2File(String content, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readContent(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] contentChar = new char[1024];

        try (FileReader fileReader = new FileReader(filePath)) {
            int num = fileReader.read(contentChar);
            for (int i = 0; i < num; i++) {
                stringBuilder.append(contentChar[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!StringUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) {
            return false;
        }
        if (!createOrExistsFile(file)) {
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeFileFromLineString(File file, String content, boolean append) {
        if (file == null || content == null) {
            return false;
        }
        if (!createOrExistsFile(file)) {
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            bw.write(content);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeFileFromByte(File file, byte[] data, boolean append) {
        if (file == null || data == null) {
            return false;
        }
        if (!createOrExistsFile(file)) {
            return false;
        }
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, append))) {
            os.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }


    public static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> queryFileNames(String filePath) {
        List<String> names = new ArrayList<>();
        File file = new File(filePath);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isFile()) {
                names.add(f.getName());
            }
        }
        return names;
    }

    public static List<String> queryFileNamesWithoutSuffix(String filePath) {
        List<String> names = new ArrayList<>();
        File file = new File(filePath);
        File[] fs = file.listFiles();
        if (null != fs) {
            for (File f : fs) {
                if (f.isFile()) {
                    names.add(f.getName().substring(0, f.getName().lastIndexOf(".")));
                }
            }
        }
        return names;
    }

    public static File getFileByPath(String filePath) {
        return StringUtils.isEmpty(filePath) ? null : new File(filePath);
    }


    public static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }


    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

}
