package com.lqh.service;


import com.lqh.utils.Commutils;
import com.lqh.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器
 */
public class MulitThreadService {
    private static final Integer PORT;
    static{
        //加载服务器的配置
        Properties properties = Commutils.loadProperties("socket.properties");
        PORT = Integer.valueOf(properties.getProperty("PORT"));
    }
    //服务器用来缓存在线的客户
    private static Map<String, Socket> clients = new ConcurrentHashMap<>();

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
                        String msg = "newlogin:"+username;
                        for(Socket socket:clients.values()){
                            //发送
                            try{
                                PrintStream out = new PrintStream(socket.getOutputStream(),
                                        true,"UTF-8");
                                out.println(msg);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        //将用户保存到服务器缓存中
                        //服务器显示信息
                        System.out.println(username+"上线了");
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

                    }else if(msgFromClient.getType() == 3){
                        //注册群聊
                        //TODO
                    }
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
