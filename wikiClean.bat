chcp 65001
@echo off
echo input %1
java -jar %~dp0\out\artifacts\Clean\Clean-o %~dp0\out\clean  -i %1 -d 

pause