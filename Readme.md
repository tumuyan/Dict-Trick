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

## UserDBClean

rime系列输入法同步用户词库时，会生成userdb.txt格式的用户词库同步文件。  
使用此工具可以对用户词库文件切割、分析、整理。  

#### 基本逻辑
通过与原词库文件对比，可以根据特征对同步文件的内容进行分类：  
1. 词库中有这个词，并且编码相同（不需处理）  
2. 词库中有这个词，但是词条编码不同，说明有错误的输入习惯，或者可能有错误的编码  
3. 词库中没有这个词，说明是自造词  
   对自造词按c值排序，可以再次筛选结果
   1. 低频词，可能是错误的造词，需要确认并删除
   2. 高频词，可能需要加入词库中
   3. 极低频，直接删除而不输出到文件
   
经过分类后可以删除原同步文件和userdb文件夹，放置修改后的文件，执行同步

#### 参数说明
此工具和前两个参数有较大差异，特做说明（同样可以把这些参数写入config文件，并用-c参数调用）：

    -input                 待处理的文件
    -count-group a:b:c:d   按照频率对词条分组，abcd为冒号分割的递增的数字，设定了分组内词条的c值最大值。可以最多分10组。推荐参数 0:3:8:20
    -refer                 参考文件，即原方案中的词库文件
    # 如下参数暂未实装
    -blacklist-regex       词条和编码符合规则，筛选到文件
    -blacklist             需要删除的词条所在的文件

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
