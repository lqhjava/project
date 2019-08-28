package com.lqh.client.services;

import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    //缓存所有群聊名称和群星恒源
    private Map<String,Set<String>> groupChatLsit = new ConcurrentHashMap<>();
    //存储所有的群聊界面
    private Map<String,GroupChat> groupList = new ConcurrentHashMap<>();


   //好友上线
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
                            String msg = messageVO.getMsg().split("-")[1];
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
                                privateChat.readFromService(friendName+"说："+msg);
                            }
                        }else if(messageVO.getType().equals(4)){
                            //群聊
                           String groupName = messageVO.getTo().split("-")[0];
                            String senderName = messageVO.getMsg().split("-")[0];
                            String groupMsg = messageVO.getMsg().split("-")[1];
                            // 若此群名称在群聊列表
                            if (groupChatLsit.containsKey(groupName)) {
                                if (groupList.containsKey(groupName)) {
                                    // 群聊界面弹出
                                    GroupChat groupChatGUI = groupList.get(groupName);
                                    groupChatGUI.getFrame().setVisible(true);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }else {
                                    Set<String> names = groupChatLsit.get(groupName);
                                    GroupChat groupChatGUI = new GroupChat(groupName,
                                            names,myName,clientToService);
                                    groupList.put(groupName,groupChatGUI);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }
                            }else {
                                // 若群成员第一次收到群聊信息
                                // 1.将群名称以及群成员保存到当前客户端群聊列表
                                Set<String> friends = (Set<String>) Commutils.jsonToObject(messageVO.getTo().split("-")[1],
                                        Set.class);
                                groupChatLsit.put(groupName, friends);
                                reloadGroupList();
                                // 2.弹出群聊界面
                                GroupChat groupChatGUI = new GroupChat(groupName,
                                        friends,myName,clientToService);
                                groupList.put(groupName,groupChatGUI);
                                groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                            }

                        }

                    } else if (strFromServer.startsWith("newLogin:")){
                        // 好友上线提醒
                        String newFriend = strFromServer.split(":")[1];
                        users.add(newFriend);
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
        //创建线程接受服务端的信息
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();
        //创建群组
        createGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreatGroupChat(userName,users,connectToServer,
                        FriendList.this);
            }
        });
    }

    //点击私聊事件
    private class PrvateChatClick implements MouseListener {
        private String friendName;
        public PrvateChatClick(String lableName){
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
    private class GroupChatClick implements MouseListener{
        private String groupName;

        public GroupChatClick(String groupName){
            this.groupName = groupName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            //鼠标点击 创建群聊
            //如果已经有打开过群聊
            if(groupList.containsKey(groupName)){
                GroupChat groupChat = groupList.get(groupName);
                groupChat.getFrame().setVisible(true);
            }else {
                //新的群聊
                Set<String> names = groupChatLsit.get(groupName);
                GroupChat groupChat = new GroupChat(groupName,names,myName,clientToService);
                groupList.put(groupName,groupChat);
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
    }

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
           //userLabels[i].addMouseListener(new PrivateLabelAction(userName));
           labels[i].addMouseListener(new PrvateChatClick(lableName));
           friendLabelPanel.add(labels[i]);
           i++;
       }
       this.friendPanel.setViewportView(friendLabelPanel);
       //设置滚动条为垂直
        this.friendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.friendPanel.revalidate();
    }

    //加载群聊列表
    public void reloadGroupList(){
        //存储所有群聊名称
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel,
                BoxLayout.Y_AXIS));
        JLabel[] labels = new JLabel[groupChatLsit.size()];
        //遍历Map
        Set<Map.Entry<String,Set<String>>> entries = groupChatLsit.entrySet();
        Iterator<Map.Entry<String,Set<String>>> iterator =
                entries.iterator();
        int i = 0;
        while (iterator.hasNext()){
            //获取去群名称及群好友
            Map.Entry<String,Set<String>> entry = iterator.next();
            //显示
            labels[i] = new JLabel(entry.getKey());
            labels[i].addMouseListener(new GroupChatClick(entry.getKey()));
            groupNamePanel.add(labels[i]);
            i++;
        }
        groupPanel.setViewportView(groupNamePanel);
        groupPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupPanel.revalidate();
    }

    //添加群聊
    public void addGroup(String groupName,Set<String> friends){
        groupChatLsit.put(groupName,friends);
    }
}
