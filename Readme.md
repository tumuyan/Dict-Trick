# Dict Trick

这是一组方便处理词库的工具

目前已经完成初步整理的有：

## Clean

用于过滤词库中的废词，完成简繁转换



## DumpMoeGirl

用户dump萌娘百科的词库，并调用Clean工具完成处理



## 使用方法

1. 下载或者build jar文件
2. 下载opencc （由于java具有跨平台性，而opencc本身就是跨平台的，理论上Linux也可以使用这个工具）
3. 根据需求编辑opencc的简繁转换配置文件
4. 根据需求编辑废词文件
5. 根据需求编辑配置文件，仓库中的`config.txt`是一个示例，已经备注了使用的参数（配置文件可以是任何名称）
6. 使用命令 `java -jar DumpMoegirl.jar -c config.txt` 来调用配置文件完成爬虫任务
   使用命令 `java -jar Clean.jar -c config.txt` 来调用配置文件完成纯文本词条过滤任务
7. 当然也可以不使用配置文件，直接在命令行内输入所需参数

