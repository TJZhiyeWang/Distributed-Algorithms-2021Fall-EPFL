package cs451.Utils;

import java.io.*;

public class Logger {
    String directory;
    File file;
    OutputStream outputStream;
    Logger(String dir){
        this.directory = dir;
        file = new File(directory);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();//if not found, set up a new file
        }
        try {
             outputStream = new FileOutputStream(file); //open the file, ready to write in
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //write broadcast or delivery record to the file
    public void write(String msg){
        byte data[] = msg.getBytes();
        try {
            outputStream.write(data);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //close the output stream
    public void close(){
        try {
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
