# Dict Trick

这是一组方便处理词库的工具

目前已经完成初步整理的有：

## Clean

用于过滤词库中的废词，完成简繁转换，用于中文输入法使用。针对wiki词条和萌娘百科词条做了精准优化和大量测试。处理过程并非直接使用黑名单/正则进行匹配。

- 一定程度上保留了中英文混合词条
- 统一调整`-``·`等符号
- 从空格、标点切分词条
- 切分和抛弃大量含序号/数字/数量词的词条，包括但不限于`①⒛Ⅰⅻ甲`
- 切分和去除大量非常见汉字的词条
- 去除部分过短的词条
- ...

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
8. 程序运行结束，名称为`.dict.txt`没有额外后缀的文件为最终文件。如果没有使用`-less-output`参数，可以得到转换过程产生的其他文件，可以用于进一步分析和改善。



## 附言

我在使用此工具以及深蓝词库转换工具持续更新萌娘百科、维基百科的rime词库文件，但是由于你并不一定使用了和我相同的配置文件和相同版本的软件，会导致转换的结果可能存在差异。



转换结果和部分配置文件在我的[仓库](https://github.com/tumuyan/rime-melt)中：

- `pinyin_simp_wiki.dict.yaml`：[Github下载](https://github.com/tumuyan/rime-pinyin-simp/raw/master/pinyin_simp_wiki.dict.yaml)
- `pinyin_simp_moe.dict.yaml`：[Github下载](https://github.com/tumuyan/rime-pinyin-simp/raw/master/pinyin_simp_moe.dict.yaml)

- 废词文件：https://github.com/tumuyan/rime-melt/tree/master/others
