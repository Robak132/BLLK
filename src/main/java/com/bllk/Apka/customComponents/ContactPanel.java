package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ContactPanel extends JPanel {
    private final String contactName;
    private final MainUserPage page;
    private final int targetID;

    public ContactPanel(JPanel parent, MainUserPage _page, int _targetID, String name) {
        super();

        contactName = name;
        page = _page;
        targetID = _targetID;

        this.setLayout(new FlowLayout());
        this.setBackground(Colors.getGrey());
        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                name + " (nr " + targetID + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));

        JButton deleteButton = new JButton("Usuń kontakt");
        JButton renameContactButton = new JButton("Zmień nazwę");
        deleteButton.setFont(Fonts.getStandardFont());
        renameContactButton.setFont(Fonts.getStandardFont());


        deleteButton.addActionListener(e -> {
            MainUserPage.getConnection().removeContact(MainUserPage.getLogin().getLogin(),
                    MainUserPage.getLogin().getPasswordHash(), targetID);
            _page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(deleteButton);
        this.add(renameContactButton);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        name + " (nr " + targetID + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        name + " (nr " + targetID + ")",
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });

        renameContactButton.addActionListener(e -> renameContact());
    }
    private void renameContact() {
        JTextField newName = new JTextField();
        String newNameString;

        Object[] message = {
                "Chcesz zmienić nazwę kontaktu '" + contactName + "'",
                "Nowa nazwa:", newName
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana nazwy kontaktu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            newNameString = newName.getText();
            if (newNameString.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Pole nazwy nie może być puste.",
                        "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                new Thread(() -> {
                    MainUserPage.getConnection().createOrUpdateContact(MainUserPage.getLogin().getLogin(),
                            MainUserPage.getLogin().getPasswordHash(), newNameString, targetID);
                    page.updateContactsSummary();
                    page.updateContacts();
                    page.updateAccounts();
                }).start();
            }
        }
    }
}
