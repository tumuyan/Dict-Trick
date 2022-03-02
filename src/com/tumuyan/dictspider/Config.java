package com.tumuyan.dictspider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private String path_w = "";
    String default_path_w = "";
    String default_path = "";
    boolean auto_delete = false;
    boolean debug = false;
    Integer pageLimit = Integer.MAX_VALUE;
    List<String> input_files = new ArrayList<>();

    public void setDefault_path(String default_path) {
        this.default_path = default_path;
    }

    public void setDefault_path_w(String default_path_w) {
        this.default_path_w = default_path_w;
    }

    public List<String> getInput_files() {
        return input_files;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public String getPath_w() {
        return path_w;
    }

    public boolean isAuto_delete() {
        return auto_delete;
    }

    public Config() {

    }

    public void Parse(String[] args) {

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.equals("-h") || arg.equals("-help")) {
                System.out.println("help\n");
                return;
            } else if (arg.equals("-a")) {
                auto_delete = true;
            } else if (arg.equals("-d") || arg.equals("-debug")) {
                debug = true;
            } else if (arg.equals("-o") || arg.equals("-output")) {
                i++;
                if (args.length > i) {

                    path_w = args[i].trim().replaceFirst("(\\.[^./\\\\]+)?[/\\\\]?$", ".");
                    File file = new File(path_w).getParentFile();
//                    File file=new File(path_w.replaceFirst("[^/\\\\]+$",""));
                    if (file == null) {
//                        File f = new File(new File(System.getProperty("user.dir")),path_w);
//                        File f = new File(DumpMoeGirl.class.getClassLoader().getResource("").getFile());
                        File f = new File(DumpMoeGirl.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                        f = new File(f.getParentFile(), path_w);

                        file = f.getParentFile();
                        path_w = f.getPath();
                    }

                    if (file.exists()) {
                        System.out.println("Output to: " + path_w);
                    } else {
                        path_w = "";
                        System.out.println("[Err]Output folder not exist: " + file.getPath());
                    }

                } else {
                    System.out.println("[Err]Output arg not exist.");
                }
            } else if (arg.equals("-p") || arg.equals("-pagelimit")) {
                i++;
                if (args.length > i) {
                    pageLimit = Integer.parseInt(args[i]);
                    if (pageLimit == null) {
                        pageLimit = Integer.MAX_VALUE;
                        System.out.println("[Err]unexpected pageLimit arg and disable pageLimit: " + args[i]);
                    }
                } else {
                    System.out.println("[Err]pageLimit arg not exist.");
                }
            } else if (arg.equals("-i") || arg.equals("-input")) {
                i++;
                if (!input_files.contains(args[i])) {
                    File file = new File(args[i]);
                    if (file.exists()) {
                        input_files.add(args[i]);
                        System.out.println("Input: " + args[i]);
                    } else
                        System.out.println("[Err]Input file not exist: " + args[i]);

                } else {
                    System.out.println("[Err]Input arg not exist.");
                }
            }
        }

    }

    //   验证是否设置了输出路径
    public boolean verifyOutputPath() {
        if (path_w.length() < 1) {
            if (debug && default_path_w.length() > 0)
                path_w = default_path_w;
            else {
                System.out.println("[Err]Output path missing.");
                return false;
            }
        }
        return true;
    }


    //   验证是否设置了输入路径
    public boolean verifyInputPath() {
        String path = "";

        if (input_files.size() < 1) {
            if (debug && default_path.length() > 1) {
                path = default_path;
                input_files = new ArrayList<>();
                input_files.add(path);
            } else {
                System.out.println("[Err]Input path missing.");
                return false;
            }
        }

        if (path_w.length() < 0) {
            path_w = path.replace(".dict.txt", "");
        }
        return true;

    }
}
