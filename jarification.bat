@echo off
setlocal

rem Définir le nom du projet
set "projet=framework"

rem Définir le chemin d'accès au répertoire des sources et au répertoire de destination des fichiers compilés
set "src=.\src"
set "bin=.\bin"
set "LibTestDestination=..\TestFramework\lib"

rem Chemin vers le répertoire contenant les bibliothèques nécessaires
set "libDirectory=.\lib"

rem lister les packages du source
dir /s /B "%src%\*.java" > sources.txt

:: Compiler les fichiers java en utilisant les jar
javac -d "%bin%" -cp "%libDirectory%\*" @sources.txt

rem Aller dans le répertoire de destination des fichiers compilés
cd "%bin%"

rem Compresser dans un fichier jar
jar -cvf "../lib/%projet%.jar" *

echo Fichier .jar créé : %projet%.jar

cd ..

xcopy /E /I /Y "%libDirectory%\" "%LibTestDestination%"

del sources.txt

endlocal