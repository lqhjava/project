package com.lqh.client.services;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GroupChat {
    private JPanel groupPanel;
    private JTextArea readFromService;
    private JTextField sendToService;
    private JPanel friendsPanel;

    public GroupChat() {
        sendToService.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }
        });
    }
}
