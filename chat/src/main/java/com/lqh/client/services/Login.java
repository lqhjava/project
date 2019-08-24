package com.lqh.client.services;

import com.lqh.client.dao.AccontDao;
import com.lqh.client.po.User;
import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Set;

public class Login {
    private JPanel login;
    private JPanel lablePanle;
    private JPanel usernamePanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton loginBtn;
    private JButton regBtn;
    private JPanel btnPanel;
    private JPanel passwordPanel;

    //DAO层
    private AccontDao accontDao = new AccontDao();


    public Login() {
        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        //点击注册按钮 regBtn
        //跳转到注册页面
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //注册页面
                new UserReg();
            }
        });

        //准备登陆
        //点击登陆按钮 loginBtn
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取登陆界面的数据
                String username = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                //调用DAO层方法
                User user = accontDao.lobin(username,password);
                //判断是否登陆成功
                if(user != null){
                    //登陆成功
                    JOptionPane.showMessageDialog(null,"登陆成功","提示信息",
                            JOptionPane.INFORMATION_MESSAGE);
                    //与服务器建立连接 并将信息加载到服务器缓存
                    ClientToService clientToService = new ClientToService();
                    //服务器获取到登陆的用户信息，准备发送给其他在线的好友
                    MessageVO messageVO = new MessageVO();
                    //告知服务器用户用户要干的事情
                    messageVO.setType(1);
                    //告知服务器用户的信息
                    messageVO.setMsg(username);
                    String msgJson = Commutils.objToJson(messageVO);
                    try{
                        //向服务器发送信息
                        PrintStream out = new PrintStream(clientToService.getOutputStream(),
                                true,"UTF-8");
                        out.println(msgJson);
                        //接受服务器返回的信息 获取用户的信息 好友
                        Scanner in = new Scanner(clientToService.getInputStream());
                        if(in.hasNextLine()){
                            String jsonStr = in.nextLine();
                            //获取用户好友
                            MessageVO msgFromService = (MessageVO) Commutils.
                                    jsonToObject(jsonStr,MessageVO.class);
                            Set<String> names = (Set<String>) Commutils.
                                    jsonToObject(msgFromService.getMsg(),Set.class);
                            //输出用户名
                            System.out.println(names);
                            //加载好友界面
                            //登陆界面不可见
                            frame.setVisible(false);
                            //跳转到用户好友界面
                            new FriendList(username,clientToService,names);
                        }

                    }catch(IOException io){
                        io.printStackTrace();
                    }


                }else{
                    //登陆失败
                    //停留在当前页面
                    JOptionPane.showMessageDialog(null,"登陆失败","失败信息",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        new Login();
    }



}
