package com.lqh.service;


import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器
 */
public class MulitThreadService {
    private static final String IP;
    private static final Integer PORT;
    static{
        //加载服务器的配置
        Properties properties = Commutils.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.valueOf(properties.getProperty("port"));
    }
    //服务器用来缓存在线的客户
    private static Map<String, Socket> clients = new ConcurrentHashMap<>();
    //服务器用来缓存群名和群成员的
    private static Map<String,Set<String>> groups = new ConcurrentHashMap<>();

    //服务器处理用户发来的请求
    public static class ExecuteClient implements Runnable{
        private Socket client;
        private Scanner in;
        private PrintStream out;
        public ExecuteClient(Socket client){
                this.client = client;
                try{
                    this.in = new Scanner(client.getInputStream());
                    this.out = new PrintStream(client.getOutputStream(),
                            true,"UTF-8");
                }catch(IOException e){
                    e.printStackTrace();
                }
        }

        @Override
        public void run() {
            while (true){
                //服务器一直等待连接
                if(in.hasNextLine()){
                    String strFromClient = in.nextLine();
                    MessageVO msgFromClient = (MessageVO) Commutils.
                            jsonToObject(strFromClient,MessageVO.class);
                    //处理请求
                    if(msgFromClient.getType().equals(1)){
                        //登陆
                        String username = msgFromClient.getMsg();
                        Set<String> names = clients.keySet();
                        //设置信息
                        MessageVO msgToClient = new MessageVO();
                        msgFromClient.setType(1);
                        msgToClient.setMsg(Commutils.objToJson(names));
                        out.println(Commutils.objToJson(msgToClient));
                        String msg = "newLogin:"+username;
                        //将用户保存到服务器缓存中
                        //服务器显示信息
                        System.out.println(username+"上线了");
                        System.out.println(msg);
                        sendUserLogin(msg);
                        clients.put(username,client);
                        //统计在线人数
                        System.out.println("在线人数"+ clients.size());
                    }else if(msgFromClient.getType().equals(2)){
                        //私聊  Type==2
                        //获取要发送用户
                        String friendName = msgFromClient.getTo();
                        Socket clientSocket =clients.get(friendName);
                        //向用户发送
                        try {
                            PrintStream out = new PrintStream(clientSocket.getOutputStream(),true,"UTF-8");
                            MessageVO msgToFriend = new MessageVO();
                            msgToFriend.setType(2);
                            msgToFriend.setMsg(msgFromClient.getMsg());
                            System.out.println("收到私聊信息，信息为"+msgFromClient.getMsg());
                            out.println(Commutils.objToJson(msgToFriend));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else if(msgFromClient.getType().equals(3)){
                        //注册群聊
                        //获取信息
                        String groupName = msgFromClient.getMsg();
                        MessageVO messageVO = new MessageVO();
                        Set<String> friends = (Set<String>)Commutils.jsonToObject(
                                msgFromClient.getTo(),Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新的群聊注册成功，群聊名称为"+groupName+"一共有"+groups.size()+"个群");
                    }else if(msgFromClient.getType().equals(4)){
                        System.out.println("服务器接受的信息为"+msgFromClient);
                        //群聊信息  发送给每个群成员
                        String groupName = msgFromClient.getTo();
                        //保存群名
                        Set<String> names = groups.get(groupName);
                        Iterator<String> iterator = names.iterator();
                        while (iterator.hasNext()){
                            String socketName = iterator.next();
                            Socket client = clients.get(socketName);
                            try {
                                PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                                MessageVO messageVO = new MessageVO();
                                messageVO.setType(4);
                                messageVO.setMsg(msgFromClient.getMsg());
                                messageVO.setTo(groupName+"-"+Commutils.objToJson(names));
                                out.println(Commutils.objToJson(messageVO));
                                System.out.println("服务器发送的信息为"+messageVO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

        }
        private void sendUserLogin(String msg) {
            for (Map.Entry<String,Socket> entry: clients.entrySet()) {
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //建立基站
        ServerSocket serverSocket = new ServerSocket(PORT);
        //创建线程处理连接
        //ExecutorServices executorServices = new FixedThreadPool();
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for(int i = 0; i< 50; i++){
            System.out.println("等待客户端连接");
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接......端口号为"+client.getPort()+"连接");
            executorService.submit(new ExecuteClient(client));
        }
    }

}
