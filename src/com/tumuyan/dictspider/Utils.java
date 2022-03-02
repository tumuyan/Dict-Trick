package com.tumuyan.dictspider;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    // 把文本文件读取为HashSet<String>，每行符合长度的文本为一个元素
    public static Set<String> ReadWords(String path, int wordLengthMin) {
        Set<String> words = new HashSet<>();
        if (path == null)
            return words;

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
//              如果匹配到空行
                if (line.length() < wordLengthMin)
                    continue;
                words.add(line.trim());
            }
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public static Set<String> ReadWords(String path) {
        return ReadWords(path, 2);
    }


    // 把Set<String>写入到指定文件中
    public static void WriteList(Set<String> keys, String path, boolean auto_delete, boolean show_log) throws Exception {
        File file = new File(path);

        if (keys.size() > 0) {
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
                    System.out.println("[Done]Delete " + file.getPath());
                }
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        int counter = 0;

        for (String s : keys) {
            try {
                fileOutputStream.write('\n');
                fileOutputStream.write(s.getBytes());
                counter++;
                if (show_log)
                    System.out.println(Thread.currentThread().getName() + " " + counter + "/" + keys.size() + "  " + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileOutputStream.close();
        System.out.println("[Done] size=" + counter + " \t" + path);
    }


    //    把string写入到指定文件
    public static boolean Write(String path, StringBuffer content, boolean auto_delete) throws Exception {

        File file = new File(path);
        if (file.exists() && auto_delete) {
            if (file.isFile())
                file.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }

    public static boolean WriteFile(String path, StringBuffer content) throws Exception {

        File file = new File(path);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }

    public static synchronized void write(FileOutputStream fileOutputStream, String content) throws Exception {
        fileOutputStream.write(content.getBytes());
    }

// 调用OpenCC完成文件简繁转换

    public static void OpenCC_T2S(String inputPath, String outputPath, String openccPath) {
        OpenCC(inputPath, outputPath, openccPath, "t2s.json");
    }

    public static void OpenCC(String inputPath, String outputPath, String openccPath, String openccConfig) {
        String command = (openccPath + File.separator + "opencc -i " + inputPath + " -o " + outputPath + " -c " + openccPath + File.separator + "t2s.json");

        System.out.println("exec OpenCC\nCommand = " + command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(
                    process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println("error!");
            }

            bis.close();
            br.close();

            System.out.println("finish OpenCC");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
