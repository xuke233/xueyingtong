package com.cburch.logisim.file;

import com.cburch.logisim.circuit.Analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**

 * 读取文件内容，这里只是将文件读取，打印出具体数据，可以根据自己的情况，修改代码

 * @author duyq

 *

 */

public class ReadFile {

//    public static void main(String[] args) {
//
//        //按行读取文件
//
//        ReadFile readFilesTest = new ReadFile("/Users/duyq/1.txt");
//
//        readFilesTest.readFileByLines();
//
//    }

    private String filePath;//文件路径，类似（在osx：/Users/duyq/1.txt，windows：c:/duyq/1.txt）

    public ReadFile(String filePath) {

        this.filePath = filePath;

    }

    /**

     * 按行读取文件

     *

     * @return

     */
    public static String[][] Answser;

    public void readFileByLines() {

        File file = new File(this.filePath);


        //判断文件是否存在

        if (!file.exists()) {

            System.out.println(this.filePath + "  文件不存在。");

        }

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(file));

            String tempString = "";
            int size=100;
            int[] num = new int[size];
                while ((tempString = reader.readLine()) != null) {

                    System.out.println(new String(tempString.getBytes("GBK"), "UTF-8"));

                }
            reader.close();

        } catch (IOException e) {

            e.printStackTrace();

            if (reader != null)

                try {

                    reader.close();

                } catch (IOException localIOException1) {

                }

        } finally {

            if (reader != null)

                try {
                    reader.close();
                } catch (IOException localIOException2) {
                }

        }

    }

}
