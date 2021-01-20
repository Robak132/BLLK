package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreditPanel extends JPanel {
    MainUserPage page;
    JSONObject credit;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _credit) {
        super();
        page = _page;
        credit = _credit;

        JLabel nameLabel = new JLabel(credit.getString("name"));
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

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JLabel jLabel : Arrays.asList(nameLabel, valueLabel, currencyLabel, interestLabel, commissionLabel, RRSOLabel, dateCreatedLabel, dateEndedLabel, remainingLabel, monthlyLabel, monthsRemainingLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
        }
        payInstallmentButton.setFont(Fonts.getStandardFont());
        dateCreatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBackground(Colors.getGrey());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(150, -1));

        this.add(nameLabel);
        JPanel p1 = new JPanel();
        p1.setBackground(Colors.getGrey());
        p1.add(valueLabel);
        p1.add(currencyLabel);
        this.add(p1);
        JPanel p2 = new JPanel();
        p2.setBackground(Colors.getGrey());
        p2.add(interestLabel);
        p2.add(commissionLabel);
        p2.add(RRSOLabel);
        this.add(p2);
        JPanel p3 = new JPanel();
        p3.setBackground(Colors.getGrey());
        p3.add(dateCreatedLabel);
        p3.add(dateEndedLabel);
        this.add(p3);
        JPanel p4 = new JPanel();
        p4.setBackground(Colors.getGrey());
        p4.add(remainingLabel);
        p4.add(currencyLabel);
        p4.add(monthlyLabel);
        p4.add(currencyLabel);
        p4.add(monthsRemainingLabel);
        this.add(p4);

        payInstallmentButton.addActionListener(e -> {
            int accountID = payInstallmentDialog();
            if (accountID >= 0) {
                MainUserPage.getConnection().updateCredit(MainUserPage.getLogin().getLogin(), MainUserPage.getLogin().getPasswordHash(), _id, accountID);
                page.updateCreditsSummary();
                page.updateAccounts();
            }
        });
        payInstallmentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(payInstallmentButton);
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
