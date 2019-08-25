package com.lqh.client.services;

import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class GroupChat {
    private JPanel groupPanel;
    private JTextArea readFromService;
    private JTextField sendToService;
    private JPanel friendsPanel;

    //群聊名称
    private String groupName;
    //群聊成员
    private Set<String> friends;
    private String myName;
    private ClientToService clientToService;

    private JFrame frame;

    public GroupChat(String groupName,
                     Set<String> friends,
                     String myName,
                     ClientToService clientToService) {
        this.groupName = groupName;
        this.friends = friends;
        this.myName = myName;
        this.clientToService = clientToService;

        frame = new JFrame(groupName);
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setVisible(true);

        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()){
            String lableName = iterator.next();
            JLabel jLabel = new JLabel(lableName);
            friendsPanel.add(jLabel);
        }

        sendToService.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //记录输入信息
                StringBuilder sb = new StringBuilder();
                sb.append(sendToService.getText());
                //按下回车
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String strToService = sb.toString();
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType(4);
                    messageVO.setMsg(myName+"-"+strToService);
                    messageVO.setTo(groupName);

                    try {
                        PrintStream out = new PrintStream(clientToService.getOutputStream(),
                                true,"UTF-8");
                        out.println(Commutils.objToJson(messageVO));
                        System.out.println("客户端发送的信息为："+messageVO);
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    public void readFromServer(String msg) {
        readFromService.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
