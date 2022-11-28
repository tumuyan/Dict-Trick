chcp 65001
@echo off
echo input %1
java -jar %~dp0out\artifacts\CleanL\CleanL.jar -i %1 -d 

pause