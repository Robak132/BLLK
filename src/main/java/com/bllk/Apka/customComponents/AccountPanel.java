package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AccountPanel extends JPanel {
    private String accountName;
    private final String accountNumber;
    MainUserPage page;

    public AccountPanel(String _accountName, String _accountNumber, String _balance, String _currency,
                        MainUserPage _page, boolean isEligibleToBeDeleted) {
        super();
        page = _page;
        accountNumber = _accountNumber;
        accountName = _accountName;

        if (_accountName.equals(_accountNumber))
            accountName = "Konto";

        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        JButton deleteAccountButton = new JButton("Usuń konto");
        JButton renameAccountButton = new JButton("Zmień nazwę");

        balanceLabel.setHorizontalAlignment(JLabel.RIGHT);
        currencyLabel.setHorizontalAlignment(JLabel.LEFT);

        balanceLabel.setForeground(Colors.getBrightTextColor());
        currencyLabel.setForeground(Colors.getBrightTextColor());

        balanceLabel.setFont(Fonts.getStandardFont());
        currencyLabel.setFont(Fonts.getStandardFont());
        deleteAccountButton.setFont(Fonts.getStandardFont());
        renameAccountButton.setFont(Fonts.getStandardFont());

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(150, 100));
        this.setMinimumSize(new Dimension(150, 100));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 2);
        this.add(balanceLabel, c);
        c.gridx = 1;
        c.insets = new Insets(0, 2, 0, 0);
        this.add(currencyLabel, c);


        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = 1;
        c.gridx = 0;

        c.gridwidth = 1;
        this.add(renameAccountButton, c);

        if (!isEligibleToBeDeleted) {
            deleteAccountButton.setEnabled(false);
            deleteAccountButton.setToolTipText("Nie można usunąć. Te konto jest wymagane do spłaty kredytu.");
        }

        c.gridx = 1;
        this.add(deleteAccountButton, c);

        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                accountName + " (nr " + accountNumber + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        accountName + " (nr " + accountNumber + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        accountName + " (nr " + accountNumber + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });

        deleteAccountButton.addActionListener(e -> deleteAccount());
        renameAccountButton.addActionListener(e -> renameAccount());
    }
    private void deleteAccount() {
        new Thread(() -> {
            MainUserPage.getConnection().removeAccount(MainUserPage.getLogin().getLogin(),
                MainUserPage.getLogin().getPasswordHash(), Integer.parseInt(accountNumber));
            page.updateContacts();
            page.updateAccounts();
        }).start();
    }
    private void renameAccount() {
        JTextField newName = new JTextField();
        String newNameString;

        Object[] message = {
                "Chcesz zmienić nazwę konta '" + accountName + "'",
                "Nowa nazwa:", newName
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana nazwy konta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            newNameString = newName.getText();
            if (newNameString.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Pole nazwy nie może być puste.",
                        "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                new Thread(() -> {
                    MainUserPage.getConnection().createOrUpdateContact(MainUserPage.getLogin().getLogin(),
                        MainUserPage.getLogin().getPasswordHash(), newNameString, Integer.parseInt(accountNumber));
                    page.updateContacts();
                    page.updateAccounts();
                    page.updateInvestmentsSummary();
                    page.updateAccountsSummary();
                    page.updateMoney();
                }).start();
            }
        }
    }
}
