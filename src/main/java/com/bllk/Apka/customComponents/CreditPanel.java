package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreditPanel extends JPanel {
    String creditName;
    MainUserPage page;
    JSONObject credit;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _credit) {
        super();
        page = _page;
        credit = _credit;
        creditName = credit.getString("name");

        JLabel valueLabel = new JLabel("" + credit.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(MainUserPage.getCurrencies().get(credit.getString("currencyid")));
        JLabel interestLabel = new JLabel(credit.getString("interest"));
        JLabel commissionLabel = new JLabel(credit.getString("commission"));
        JLabel RRSOLabel = new JLabel(credit.getString("rrso"));
        JLabel dateCreatedLabel = new JLabel(credit.getString("datecreated"));
        JLabel dateEndedLabel = new JLabel(credit.getString("dateended"));
        JLabel remainingLabel = new JLabel(credit.getString("remaining"));
        JLabel monthlyLabel = new JLabel(credit.getString("monthly"));
        JLabel monthsRemainingLabel = new JLabel(credit.getString("monthsremaining"));
        JButton payInstallmentButton = new JButton("Spłać ratę kredytu");

        for (JLabel jLabel : Arrays.asList(valueLabel, currencyLabel, interestLabel, commissionLabel, RRSOLabel,
                dateCreatedLabel, dateEndedLabel, remainingLabel, monthlyLabel, monthsRemainingLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
            jLabel.setPreferredSize(new Dimension(10, 25));
        }
        payInstallmentButton.setFont(Fonts.getStandardFont());

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(200, 150));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 2);
        this.add(valueLabel, c);
        c.gridx = 1;
        c.insets = new Insets(0, 2, 0, 0);
        this.add(currencyLabel, c);


        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = 1;

        c.gridx = 0;
        c.gridwidth = 2;
        this.add(interestLabel, c);
        this.add(commissionLabel, c);
        this.add(RRSOLabel, c);
        this.add(dateCreatedLabel, c);
        this.add(dateEndedLabel, c);
        this.add(remainingLabel, c);
        this.add(currencyLabel, c);
        this.add(monthlyLabel, c);
        this.add(currencyLabel, c);
        this.add(monthsRemainingLabel, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        this.add(payInstallmentButton, c);


        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                creditName,
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));
        payInstallmentButton.addActionListener(e -> {
            int accountID = payInstallmentDialog();
            if (accountID >= 0) {
                MainUserPage.getConnection().updateCredit(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), _id, accountID);
                page.updateCreditsSummary();
                page.updateAccounts();
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        creditName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        creditName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });
    }

    int payInstallmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        List<Long> values_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : MainUserPage.getAccounts().entrySet()) {
            if (account.getValue().getInt("currencyid") == credit.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, MainUserPage.getCurrencies().get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
                values_to_select.add(account.getValue().getLong("value"));
            }
        }

        Object[] message = {
                "Zapłać z konta:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Spłać ratę kredytu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (values_to_select.get(accountBox.getSelectedIndex()) >= credit.getLong("monthly"))
                return accounts_to_select.get(accountBox.getSelectedIndex());
            else
                JOptionPane.showMessageDialog(null,"Nie posiadasz wystarczająco pieniędzy.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}
