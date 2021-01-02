package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.awt.*;import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginWindow {
    private static JFrame frame;
    private static ClientServerConnection connection;

    static JPanel loginPanel;
    JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel message;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        loginPanel = new LoginWindow().mainPanel;
        connection = new ClientServerConnection();
        frame.setContentPane(loginPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(400, 320));
    }

    public void Submit() {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            message.setText("No login or password given.");
            return;
        }
        try {
            message.setText("Checking...");
            Client client = connection.get_login(login, password);
            Login log = new Login(client.getID(), login, password);
            frame.setContentPane(new MainUserPage(frame, loginPanel, connection, client, log).menuPanel);
            loginField.setText("");
            passwordField.setText("");
            message.setText(" ");
        }
        catch (Exception ex) {
            message.setText("Invalid user...");
        }
    }

    public LoginWindow() {
        loginButton.addActionListener(e -> Submit());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Submit();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Submit();
                }
            }
        });
    }
    public void pullDataFromServer(String login, String password) {

    }
}