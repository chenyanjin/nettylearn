package ck.ll;

import communicate.CommunicateClient;
import nettying.Message;

import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Created by chenkai on 2014/10/9.
 */
public class A {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        scanner.findInLine("(\\w+)\\s(.+)");
//        MatchResult mr = scanner.match();
//
//        String dest = mr.group(1);
//        String content = mr.group(2);
//        System.out.println("input-name="+dest+";age="+content);


        CommunicateClient cc = CommunicateClient.getInstance();
        boolean b1 = false,b2 = false,b3 = false;
         b1 = cc.testConnect("127.0.0.1",9342,"ck");
//         b2 = cc.testConnect("127.0.0.1",9342,"ll");
//         b3 = cc.testConnect("127.0.0.1",9342,"ck");
        cc.sendMessage(new Message(2,"hello ck","ck","ck"),"ck");
        System.out.println("b1="+b1+" b2="+b2+" b3="+b3);
    }
}
