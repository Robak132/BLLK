package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ContactPanel extends JPanel {
    public ContactPanel(JPanel parent, MainUserPage page, int targetID, String name) {
        super();

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

        JButton deleteButton = new JButton("Usuń");
        deleteButton.setFont(Fonts.getStandardFont());

        deleteButton.addActionListener(e -> {
            MainUserPage.getConnection().removeContact(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), targetID);
            page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(deleteButton);
    }
}
