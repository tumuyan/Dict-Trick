package com.tumuyan.dictspider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    private String path_w = "";
    private String default_path_w = "";
    private String default_path = "";
    private String default_opencc_path = "";
    private String default_opencc_config = "";
    private String[] default_blacklist;
    private String[] default_blacklist_fix;
    private String[] default_blacklist_regex;
    private boolean auto_delete = false; // 如果输出的文件已存在，自动删除文件（可能没用
    private boolean debug = false; // debug自用，无需填入参数，使用默认路径
    private Integer pageLimit = Integer.MAX_VALUE; //  debug使用，限制爬虫爬的页面数量
    private List<String> input_files = new ArrayList<>(); // 输入文件的列表
    private String opencc_path = "";  // opencc可执行文件所在的路径（不含文件名）
    private String opencc_config = ""; // opencc的配置文件所在的路径，是opencc_path的相对路径

    private List<String> blacklist = new ArrayList<>(); // 废词
    private List<String> blacklist_fix = new ArrayList<>(); // 修复过杀废词
    private List<String> blacklist_regex = new ArrayList<>(); // 废词正则表达式

    public void setDefault_opencc_config(String default_opencc_config) {
        this.default_opencc_config = default_opencc_config;
    }

    public void setDefault_opencc_path(String default_opencc_path) {
        this.default_opencc_path = default_opencc_path;
    }

    public void setDefault_blacklist(String[] default_blacklist) {
        this.default_blacklist = default_blacklist;
    }

    public void setDefault_blacklist_fix(String[] default_blacklist_fix) {
        this.default_blacklist_fix = default_blacklist_fix;
    }

    public void setDefault_blacklist_regex(String[] default_blacklist_regex) {
        this.default_blacklist_regex = default_blacklist_regex;
    }


    public List<String> getBlacklist() {
        return blacklist;
    }

    public List<String> getBlacklist_fix() {
        return blacklist_fix;
    }

    public List<String> getBlacklist_regex() {
        return blacklist_regex;
    }

    public String getOpencc_path() {
        return opencc_path;
    }

    public String getOpencc_config() {
        return opencc_config;
    }

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

    public static String[] short_name = new String[]{
            "h", "a", "d", "o", "p", "i", "cc", "ccc", "b", "bf", "bs"
    };
    public static List<String> full_name = Arrays.asList(
            "help", "a", "debug", "output", "pagelimit", "input", "opencc", "opencc-config", "blacklist", "blacklist-fix", "blackstring"
    );


    public void Parse(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
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
                boolean no_value = true;
                while (args.length > i) {
                    arg = args[i];
                    if (full_name.contains(arg.replaceFirst("^-", ""))) {
                        i--;
                        break;
                    }
                    if (!input_files.contains(arg)) {
                        File file = new File(arg);
                        if (file.exists()) {
                            input_files.add(arg);
                            System.out.println("Input: " + arg);
                            no_value = false;
                        } else
                            System.out.println("[Err]Input file not exist: " + arg);

                    }
                    i++;
                }

                if (no_value) {
                    System.out.println("[Err]Input arg not exist.");
                }
            } else if (arg.equals("-cc") || arg.equals("-opencc")) {
                i++;
                if (args.length > i) {
                    File file = new File(args[i]);
                    if (file.exists()) {
                        opencc_path = args[i];
                        System.out.println("Opencc: " + opencc_path);
                    } else
                        System.out.println("[Err]opencc file not exist: " + args[i]);

                } else {
                    System.out.println("[Err]opencc arg not exist.");
                }
            } else if (arg.equals("-ccc") || arg.equals("-opencc-config")) {
                i++;
                if (args.length > i) {
                    File file = new File(args[i]);
                    if (file.exists()) {
                        opencc_config = args[i];
                        System.out.println("Opencc config: " + opencc_config);
                    } else
                        System.out.println("[Err]opencc config file not exist: " + args[i]);

                } else {
                    System.out.println("[Err]opencc config arg not exist.");
                }
            } else if (arg.equals("-b") || arg.equals("-blacklist")) {
                i++;
                arg = args[i];
                boolean no_value = true;
                while (args.length > i) {
                    if (full_name.contains(arg.replaceFirst("^-", ""))) {
                        i--;
                        break;
                    }
                    if (!blacklist.contains(arg)) {
                        File file = new File(arg);
                        if (file.exists()) {
                            blacklist.add(arg);
                            System.out.println("Blacklist: " + arg);
                            no_value = false;
                        } else
                            System.out.println("[Err]Blacklist file not exist: " + arg);

                    }
                    i++;
                    arg = args[i];
                }

                if (no_value) {
                    System.out.println("[Err]Blacklist arg not exist.");
                }
            } else if (arg.equals("-bf") || arg.equals("-blacklist-fix")) {
                i++;
                arg = args[i];
                boolean no_value = true;
                while (args.length > i) {
                    if (full_name.contains(arg.replaceFirst("^-", ""))) {
                        i--;
                        break;
                    }
                    if (!blacklist_fix.contains(arg)) {
                        File file = new File(arg);
                        if (file.exists()) {
                            blacklist_fix.add(arg);
                            System.out.println("Blacklist fix file: " + arg);
                            no_value = false;
                        } else
                            System.out.println("[Err]Blacklist fix file not exist: " + arg);

                    }
                    i++;
                    arg = args[i];
                }

                if (no_value) {
                    System.out.println("[Err]Blacklist fix arg not exist.");
                }
            } else if (arg.equals("-b") || arg.equals("-blacklist")) {
                i++;
                arg = args[i];
                boolean no_value = true;
                while (args.length > i) {
                    if (full_name.contains(arg.replaceFirst("^-", ""))) {
                        i--;
                        break;
                    }
                    if (!blacklist_regex.contains(arg)) {
                        blacklist_regex.add(arg);
                        System.out.println("Blacklist regex: " + arg);
                        no_value = false;
                    }
                    i++;
                    arg = args[i];
                }

                if (no_value) {
                    System.out.println("[Err]Blacklist regex arg not exist.");
                }
            }
        }

    }

    //   验证是否设置了输出路径,如果执行爬虫任务，缺少此参数自动退出
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


    //   验证是否设置了输入路径，如果执行转换任务，缺少此参数必须退出
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

        if (path_w.length() < 1) {
            path_w = path.replace(".dict.txt", "");
        }
        return true;

    }

    //如果没有opencc参数，可以不做Opencc转换，无需退出
    public boolean verifyOpencc() {
        if (opencc_path.length() < 1) {
            if (debug && default_opencc_path.length() > 1) {
                opencc_path = default_opencc_path;
            } else {
                System.out.println("[Err]Opencc path missing.");
                return false;
            }
        }

        if (opencc_config.length() < 1) {
            if (debug && default_opencc_config.length() > 1) {
                opencc_config = default_opencc_config;
            } else {
                System.out.println("[Err]Opencc config arg missing.");
                return false;
            }
        }
        return true;
    }

    //如果没有 blacklist参数，可以不做黑名单过滤，无需退出
    public boolean verifyBlacklist() {
        if (blacklist.size() < 1) {
            if (debug && default_blacklist != null) {
                blacklist = Arrays.asList(default_blacklist);
            } else {
                System.out.println("[Err]Blacklist arg missing.");
                return false;
            }
        }

        if (blacklist_fix.size() < 1) {
            if (debug && default_blacklist_fix != null) {
                blacklist_fix = Arrays.asList(default_blacklist_fix);
            } else {
                System.out.println("[Err]Blacklist arg missing.");
//                return false;
            }
        }

        if (blacklist_regex.size() < 1) {
            if (debug && default_blacklist_regex != null) {
                blacklist_regex = Arrays.asList(default_blacklist_regex);
            } else {
                System.out.println("[Err]Blacklist regex arg missing.");
//                return false;
            }
        }

        return true;
    }
}
