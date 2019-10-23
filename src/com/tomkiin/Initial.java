package com.tomkiin;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Initial extends HuffmanTree {
    private boolean flag = false;  // 文件是否加密

    public static void main(String[] args) throws Exception {
        Initial initial = new Initial();
        System.out.println("----------文档内容:");
        char[] chars = initial.getFileChar();
        if (chars == null) return;
        for (char word : chars)
            System.out.print(word);
        System.out.println("\n\n----------统计词频");
        Map<Character, Integer> wordCount = initial.wordCount(chars);
        System.out.println(wordCount);
        System.out.println("\n----------初始化哈夫曼树节点");
        List<HuffmanNode> nodes = initial.initHuffmanNode(wordCount);
        System.out.println(nodes);
        System.out.println("\n----------创建哈夫曼树");
        initial.createHuffmanTree(nodes);
        System.out.println("\n----------获取哈夫曼编码");
        Map<Character, String> huffmanCode = new HashMap<Character, String>();
        initial.getHuffmanCode(initial.root, "", huffmanCode);
        System.out.println(huffmanCode);
        System.out.println("\n----------开始压缩文件");
        initial.compress(huffmanCode);
        System.out.print("\n----------压缩率:");
        initial.showCompressRate();
        System.out.println("\n----------开始解压文件");
        initial.decode(initial.root);
    }

    private char[] getFileChar() {  // 获取文件字符内容，并保存到字符数组里
        File file = new File("resource/test.txt");
        try (FileReader fileReader = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            fileReader.read(chars);
            return chars;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<Character, Integer> wordCount(char[] chars) {  // 统计词频
        Map<Character, Integer> wordMap = new HashMap<>();
        for (char aChar : chars) {
            if (wordMap.get(aChar) == null) wordMap.put(aChar, 1);
            else {
                int count = wordMap.get(aChar);
                wordMap.put(aChar, ++count);
            }
        }

        return wordMap;
    }

    private void getHuffmanCode(HuffmanNode root, String code, Map<Character, String> huffmanCode) {  // 根据词频和哈夫曼树将字符转化为哈夫曼编码
        if (root.lChild != null) getHuffmanCode(root.lChild, code + "0", huffmanCode);
        if (root.rChild != null) getHuffmanCode(root.rChild, code + "1", huffmanCode);
        if (root.lChild == null && root.rChild == null) {
            huffmanCode.put(root.data, code);
        }
    }

    private int changeStringToInt(String byteCode) {  // 八字节二进制转十进制
        return Integer.parseInt(byteCode, 2);
    }

    private char XOR(char char1, char char2) {  // 两个字符做异或运算
        int int1 = char1 - '0';
        int int2 = char2 - '0';
        int num = int1 ^ int2;
        return (char) (num + '0');
    }


    private void compress(Map<Character, String> huffmanCode) throws Exception {  // 开始压缩
        String password = "";
        System.out.println("是否需要加密? 1:是,0:否");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextInt() == 1) {
            flag = true;  // 文件标记为加密状态
            System.out.println("请输入密码:");
            password = scanner.next();
        }

        //构建文件输出流
        FileOutputStream fos = new FileOutputStream("resource/test.zip");
        FileInputStream fis = new FileInputStream("resource/test.txt");
        StringBuilder byteCode = new StringBuilder();
        int aChar = fis.read();  // 读取一个字符
        while (aChar != -1) {
            byteCode.append(huffmanCode.get((char) aChar));
            aChar = fis.read();  // 读取下一个字符
        }
        while (byteCode.length() % 8 != 0)  // 剩余不足8位用0补齐
            byteCode.append("0");
        fis.close();

        char[] byteCodeList = byteCode.toString().toCharArray();  // 将哈夫曼编码转化为二进制字符数组
        // 如果需要加密，使用密码对哈夫曼编码进行异或加密
        if (flag && password != "") {
            StringBuilder bytePassword = new StringBuilder();
            for (char achar : password.toCharArray())
                bytePassword.append(Integer.toBinaryString(achar));
            char[] byteBinPassword = bytePassword.toString().toCharArray();  // 将接收的密码转化为二进制字符数组
            int passwordLength = byteBinPassword.length;
            // 使用异或加密
            int index = 0;
            for (int i = 0; i < byteCodeList.length; i++) {
                if (index % passwordLength == 0) index = 0;
                byteCodeList[i] = XOR(byteCodeList[i], byteBinPassword[index]);
                index++;
            }
        }
        StringBuilder newByteCode = new StringBuilder();  // 保存加密后的哈夫曼编码
        for (char code : byteCodeList)
            newByteCode.append(code);

        String intCode;
        while (newByteCode.length() >= 8) {
            intCode = newByteCode.substring(0, 8);  // 每8位一组
            int number = changeStringToInt(intCode);
            fos.write(number);
            fos.flush();
            newByteCode.delete(0, 8);
        }

        fos.close();
        System.out.println("压缩成功");
    }

    private void showCompressRate() {  // 显示压缩率
        File file1 = new File("resource/test.txt");
        File file2 = new File("resource/test.zip");
        float file1Size = file1.length();
        float file2Size = file2.length();
        System.out.println((file2Size / file1Size) * 100 + "%");
    }

    private void decode(HuffmanNode root) throws Exception {  // 解压
        FileInputStream fis = new FileInputStream("resource/test.zip");
        int aValue = fis.read();
        StringBuilder byteCode = new StringBuilder();
        while (aValue != -1) {  // 还原哈夫曼编码序列
            String str = addLeftZero(Integer.toBinaryString(aValue));  // 给还原的哈夫曼编码左补0变为8位
            byteCode.append(str);
            aValue = fis.read();
        }

        char[] byteCodeList = byteCode.toString().toCharArray();

        if (flag) {
            System.out.println("该文件已经加密，请输入密码:（密码错误解压的文本会显示乱码）");
            Scanner scanner = new Scanner(System.in);
            String newPassword = scanner.next();

            StringBuilder byteNewPassword = new StringBuilder();
            for (char achar : newPassword.toCharArray())
                byteNewPassword.append(Integer.toBinaryString(achar));
            char[] byteBinNewPassword = byteNewPassword.toString().toCharArray();  // 将接收的密码转化为二进制字符数组

            // 使用异或解密
            int index = 0;
            for (int i = 0; i < byteCodeList.length; i++) {
                if (index % byteBinNewPassword.length == 0) index = 0;
                byteCodeList[i] = XOR(byteCodeList[i], byteBinNewPassword[index]);
                index++;
            }
        }

        StringBuilder text = new StringBuilder();
        HuffmanNode node = root;

        for (int i = 0; i < byteCode.length(); i++) {
            if (byteCodeList[i] == '0' && node.lChild != null) {
                node = node.lChild;
            }
            if (byteCodeList[i] == '1' && node.rChild != null) {
                node = node.rChild;
            }
            if (node.lChild == null && node.rChild == null) {
                text.append(node.data);
                node = root;
            }
        }
        System.out.println(text);

    }

    private String addLeftZero(String byteCode) {  // 左补0
        StringBuilder sb = new StringBuilder(byteCode);
        while (sb.length() < 8) {
            sb.insert(0, "0");
        }
        byteCode = sb.toString();
        return byteCode;
    }


}
