package com.bllk.Apka.customComponents;

import com.bllk.Apka.MainUserPage;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CreditPanel extends JPanel {
    String creditName;
    MainUserPage page;
    JSONObject credit;

    public CreditPanel(MainUserPage _page, int _id, JSONObject _credit) {
        super();
        page = _page;
        credit = _credit;
        creditName = credit.getString("name");

        String currencyShortcut = MainUserPage.getCurrencies().get(credit.getString("currencyid"));

        JLabel valueLabel = new JLabel(String.format("%.2f", credit.getLong("value") / 100) + " " + currencyShortcut, SwingConstants.CENTER);
        //valueLabel.setFont(new Font(valueLabel.getFont() + "", Font.PLAIN, valueLabel.getFont().getSize() * 2));

        JLabel interestLabel        = new JLabel("Oprocentowanie: " + credit.getDouble("interest")   * 100 + "%");
        JLabel commissionLabel      = new JLabel("Prowizja: "       + credit.getDouble("commission") * 100 + "%");
        JLabel RRSOLabel            = new JLabel("RRSO: "           + credit.getDouble("rrso")       * 100 + "%");
        JLabel dateCreatedLabel     = new JLabel("Data rozpoczęcia kredytu: " + credit.getString("datecreated").substring(0, 10));
        JLabel dateEndedLabel       = new JLabel("Data zakończenia kredytu: " + credit.getString("dateended").substring(0, 10));
        JLabel remainingLabel       = new JLabel("Suma do spłaty: "         + String.format("%.2f", credit.getDouble("remaining") / 100) + " " + currencyShortcut);
        JLabel monthlyLabel         = new JLabel("Miesięczna rata: "        + String.format("%.2f", credit.getDouble("monthly") / 100)   + " " + currencyShortcut);
        JLabel monthsRemainingLabel = new JLabel("Liczba pozostałych rat: " + credit.getString("monthsremaining"));

        JButton payInstallmentButton = new JButton("Spłać ratę kredytu");

        for (JLabel jLabel : Arrays.asList(valueLabel, interestLabel, commissionLabel, RRSOLabel,
                dateCreatedLabel, dateEndedLabel, remainingLabel, monthlyLabel, monthsRemainingLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(Fonts.getStandardFont());
            jLabel.setPreferredSize(new Dimension(50, 25));
        }
        payInstallmentButton.setFont(Fonts.getStandardFont());

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(200, 200));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        List<JLabel> list_of_labels = Arrays.asList(remainingLabel, interestLabel, monthlyLabel, commissionLabel, monthsRemainingLabel, RRSOLabel,
                dateCreatedLabel, new JLabel(), dateEndedLabel, new JLabel());

        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        //c.gridheight = 2;
        this.add(valueLabel, c);
        //c.gridheight = 1;
        //c.gridy++;
        //c.insets = new Insets(0, 1, 0, 1);

        for (int i = 0; i < list_of_labels.size();) {
            c.gridwidth = 3;
            c.gridx = 0;
            c.gridy++;
            this.add(list_of_labels.get(i++), c);
            c.gridwidth = 2; // GridBagConstraints.REMAINDER;
            c.gridx = 3;
            this.add(list_of_labels.get(i++), c);
        }

        c.gridwidth = 1;
        c.gridy++;
        c.gridx = 0;
        // this.add(new JLabel(""), c);
        // c.gridx = 1;
        // c.gridwidth = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;;
        this.add(payInstallmentButton, c);
        // c.gridx = 4;
        // c.gridwidth = 1;
        // this.add(new JLabel(""), c);


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
