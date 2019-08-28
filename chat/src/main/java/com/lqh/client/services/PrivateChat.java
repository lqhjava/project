package com.lqh.client.services;

import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrivateChat {
    private JPanel privateChatPanel;
    private JTextArea readFromService;
    private JTextField sendToService;

    private String friendName;
    private String myName;
    private ClientToService clientToService;

    private JFrame frame;
    private PrintStream out;



    //向服务器发送私聊信息
    public PrivateChat(String friendName,
                       String myName,
                       ClientToService clientToService) {
        this.friendName = friendName;
        this.myName = myName;
        this.clientToService = clientToService;

        try {
            this.out = new PrintStream(clientToService.getOutputStream(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //私聊标题
        frame = new JFrame("与"+friendName+"私聊中");
        frame.setContentPane(privateChatPanel);
        //当关闭私聊时并不是直接退出 而是把私聊界面隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);

        //获取键盘输入
        sendToService.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToService.getText());
                //用户发送信息 按下Enter键
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    //发送至服务器
                    String msg = sb.toString();
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType(2);
                    messageVO.setMsg(myName+"-"+msg);
                    messageVO.setTo(friendName);
                    PrivateChat.this.out.println(Commutils.objToJson(messageVO));
                    //将自己发送的信息展示到自己的私聊界面
                    readFromService(myName+"说："+msg);
                    sendToService.setText("");
                }
            }
        });
    }

    public void readFromService(String str){
        readFromService.append(str+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
