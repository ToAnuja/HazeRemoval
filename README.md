# HazeRemoval
Java based "Iamge and Video Haze Removal Application" is used to remove haze from video and Image. Build the uploaded project in following environment before executing it:

Platform OS: Linux

Screen Resolution: 1920X1080

Language: java version "1.8.0_91"

Environment: opencv-3.3.0 or any similar versions must installed before running this application

Lib: jcommon.jar, log4j-1.2.17.jar, opencv-330.jar

IDE: NetBeans IDE 8.2

-Djava.library.path="<path_of_opencv>/opencv-3.4.0/build/lib/" add as VM option in netbeans to compile and run the application from netbeans program

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:<path_of_opencv>/opencv-3.4.0/build/lib/  [Command Prompt to enviornment setting]

Run Command: java -jar dist/HazeRemoval.jar 
