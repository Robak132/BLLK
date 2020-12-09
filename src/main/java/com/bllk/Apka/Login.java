package com.bllk.Apka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private DatabaseConnection connection;

    private JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JLabel message;

    public static void main(String[] args) {
        JFrame frame = new JFrame("BLLK Login");
        frame.setContentPane(new Login().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(400, 320));
    }

    public Login() {
        connection = new DatabaseConnection();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = passwordField.getText();
                try {
                    Logins current_login = connection.check_login(login, password);
                    message.setVisible(true);
                    message.setText("Hello");
                    new Account(connection, current_login.getAccountid());
                }
                catch (Exception ex) {
                    message.setVisible(true);
                    message.setText("Invalid user...");
                }
            }
        });
    }
}