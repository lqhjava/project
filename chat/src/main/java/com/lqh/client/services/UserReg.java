package com.lqh.client.services;

import com.lqh.client.dao.AccontDao;
import com.lqh.client.po.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg {
    private JPanel UserReg;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JTextField briefText;
    private JButton confimBtn;

    //调用相关业务的准备
    private AccontDao accontDao = new AccontDao();

    public UserReg(){
        JFrame jFrame = new JFrame("用户注册");
        jFrame.setContentPane(UserReg);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.pack();
        jFrame.setVisible(true);

        //准备开始注册
        //点击提交confimBtn 开始注册

        confimBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取注册界面的数据
                String username = usernameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //准备数据
                User user = new User();
                user.setUserName(username);
                user.setPassword(password);
                user.setBrief(brief);
                //调用dao层的方法将数据写入数据库
                if(accontDao.userReg(user)){
                    //注册成功 提醒用户注册成功
                    JOptionPane.showMessageDialog(null,
                            "注册成功!","成功信息",
                            JOptionPane.INFORMATION_MESSAGE);
                    //返回登陆页面
                    jFrame.setVisible(false);
                }else{
                    //注册失败  提示用户注册失败
                    JOptionPane.showMessageDialog(null,"注册失败","失败信息",
                    JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        new UserReg();
    }
}
