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

public class InvestmentPanel extends JPanel {
    String investmentName;
    MainUserPage page;
    JSONObject inv;

    public InvestmentPanel(MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;
        investmentName = inv.getString("name");

        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(MainUserPage.getCurrencies().get(inv.getString("currencyid")));
        JLabel profitLabel = new JLabel(inv.getString("profit"));
        JLabel yearProfitLabel = new JLabel(inv.getString("yearprofit"));
        JLabel capPeriodLabel = new JLabel(inv.getString("capperiod"));
        JLabel dateCreatedLabel = new JLabel(inv.getString("datecreated"));

        JButton closeInvestmentButton = new JButton("Zamknij lokatę");

        for (JLabel jLabel : Arrays.asList(yearProfitLabel, capPeriodLabel, dateCreatedLabel,
                valueLabel, currencyLabel, profitLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
            jLabel.setPreferredSize(new Dimension(10, 25));
        }
        closeInvestmentButton.setFont(Fonts.getStandardFont());

        valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        currencyLabel.setHorizontalAlignment(JLabel.LEFT);

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
        this.add(profitLabel, c);
        c.gridx = 1;
        this.add(yearProfitLabel, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        this.add(capPeriodLabel, c);
        c.gridy = 3;
        this.add(dateCreatedLabel, c);
        c.gridy = 4;
        this.add(closeInvestmentButton, c);


        if (inv.has("dateended")) {
            JLabel dateEndedLabel = new JLabel(inv.getString("dateended"));
            dateEndedLabel.setForeground(Colors.getBrightTextColor());
            this.add(dateEndedLabel);
        }



        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                investmentName,
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
        ));
        closeInvestmentButton.addActionListener(e -> {
            int accountID = removeInvestmentDialog();
            System.out.println(accountID);
            MainUserPage.getConnection().removeInvestment(MainUserPage.getLogin().getLogin(),
                    MainUserPage.getLogin().getPasswordHash(), _id, accountID);
            page.updateInvestmentsSummary();
            page.updateAccounts();
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getBlue(), 3, true),
                        investmentName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                        investmentName,
                        TitledBorder.CENTER,
                        TitledBorder.DEFAULT_POSITION,
                        Fonts.getStandardFont(),
                        Colors.getBrightTextColor()
                ));
            }
        });
    }

    int removeInvestmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : MainUserPage.getAccounts().entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()),
                        account.getValue().getDouble("value") / 100,
                        MainUserPage.getCurrencies().get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
            }
        }

        Object[] message = {
                "Na konto:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Nowa lokata",JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return accounts_to_select.get(accountBox.getSelectedIndex());
        }
        return -1;
    }
}
