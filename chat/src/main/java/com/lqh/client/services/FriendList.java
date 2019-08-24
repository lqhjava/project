package com.lqh.client.services;

import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendList {
    private JPanel friendListPanel;
    private JScrollPane friendPanel;
    private JButton createGroupBtn;
    private JScrollPane groupPanel;

    private String myName;
    private ClientToService clientToService;

    //存储多有的在线的好友
    private Set<String> users;
    //缓存所有私聊界面
    private Map<String,PrivateChat> privateChatList = new ConcurrentHashMap<>();

    private class DaemonTask implements Runnable{
        //好友上线提醒
        private Scanner scanner = new Scanner(clientToService.getInputStream());
        @Override
        public void run() {
            while(true){
                //一直等待接受服务器发来信息
                if (scanner.hasNextLine()) {
                    String strFromServer = scanner.nextLine();
                    if(strFromServer.startsWith("{")){
                        MessageVO messageVO = (MessageVO) Commutils.jsonToObject(strFromServer,MessageVO.class);
                        //处理其他用户发来的私聊
                        if(messageVO.getType().equals(2)){
                            //处理服务器发来的私聊信息
                            //分解出是谁发来的，私聊信息。
                            String friendName = messageVO.getMsg().split("-")[0];
                            String msg = messageVO.getMsg();
                            //检查此私聊是否是第一次创建
                            if(privateChatList.containsKey(friendName)){
                                //不是第一次创建
                                PrivateChat privateChat = privateChatList.get(friendName);
                                privateChat.getFrame().setVisible(true);
                                //显示好友发来的信息
                                privateChat.readFromService(msg);
                            }else {
                                //第一次创建
                                PrivateChat privateChat = new PrivateChat(friendName,myName,clientToService);
                                privateChatList.put(friendName,privateChat);
                                privateChat.readFromService(msg);
                            }
                        }else if(messageVO.getType().equals(4)){

                        }

                    } else if (strFromServer.startsWith("newLogin:")){
                        // 好友上线提醒
                        String newFriend = strFromServer.split(":")[1];
                        JOptionPane.showMessageDialog(null,
                                newFriend+"上线了!","上线提醒",
                                JOptionPane.INFORMATION_MESSAGE);
                        users.add(newFriend);
                        // 再次刷新好友列表
                        reloadFriendList();
                    }
                }
            }

        }
    }

    public FriendList(String userName,ClientToService connectToServer, Set<String>  names){

        this.myName = userName;
        this.clientToService = connectToServer;
        this.users = names;

        JFrame frame = new JFrame(myName);
        frame.setContentPane(friendListPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        reloadFriendList();
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    //点击私聊事件
    private class ChangeLablePanel implements MouseListener {
        private String friendName;
        public ChangeLablePanel(String lableName){
            this.friendName = lableName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            //鼠标点击事件 私聊发起
            //如果缓存过私聊界面 直接打开
            if(privateChatList.containsKey(friendName)){
                PrivateChat privateChat = privateChatList.get(friendName);
                privateChat.getFrame().setVisible(true);

            }else {
                //第一次创建私聊
                PrivateChat privateChat = new PrivateChat(friendName,myName,clientToService);
                privateChatList.put(friendName,privateChat);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
        //点击好友 创建私聊
    }

    //点击创建群聊 开始群聊


    //加载好友列表
    public void reloadFriendList(){
        //加载好友列表
        JPanel friendLabelPanel = new JPanel();
        JLabel[] labels = new JLabel[users.size()];
        //遍历set集合
        Iterator<String> iterator= users.iterator();
        //设置标签为纵向对齐
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,
                BoxLayout.Y_AXIS));
       int i =0;
       while (iterator.hasNext()){
           String lableName = iterator.next();
           labels[i] = new JLabel(lableName);
           //为每个好友lable添加鼠标点击事件
           //TODO
           friendLabelPanel.add(labels[i]);
           i++;
       }
       this.friendPanel.setViewportView(friendLabelPanel);
       //设置滚动条为垂直
        this.friendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.friendPanel.revalidate();
    }
}
