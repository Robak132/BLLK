package com.bllk.Apka.customComponents;

import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class TransactionPanel extends JPanel {
    private final String sender;
    private final String receiver;
    private final String date;
    private final String title;
    private String amount;
    private final String currency;
    private final String sign;
    private final String signText;
    char type;
    Color color;

    //"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"
    public TransactionPanel(String _sender, String _receiver, String _date, String _title, Double _unformattedAmount,
                            String _currency, char _type) {
        super();
        sender = _sender;
        receiver = _receiver;
        date = _date;
        title = _title;
        currency = _currency;
        type = _type;

        switch (type) {
            case 0:
                sign = "-";
                color = Colors.getRed();
                signText = "Przelew wychodzący";
                break;
            case 1:
                sign = "+";
                color = Colors.getGreen();
                signText = "Przelew przychodzący";

                break;
            case 2:
                sign = "⮂";
                color = Colors.getBlue();
                signText = "Przelew własny - wymiana waluty";
                break;
            default:
                sign = "?";
                signText = "?";
        }
        amount = String.format("%.2f", _unformattedAmount / 100.0);

        if (type == 0 || type == 1) {
            amount = sign + amount;
        }

        addSubcomponents();
        addListeners();
    }

    private void addSubcomponents() {
        JLabel signLabel = new JLabel(sign);
        JLabel senderLabel = new JLabel(sender);
        JLabel receiverLabel = new JLabel(receiver);
        JLabel dateLabel = new JLabel(date);
        JLabel titleLabel = new JLabel(title);
        JLabel amountLabel = new JLabel(amount);
        JLabel currencyLabel = new JLabel(currency);
        JLabel arrowLabel = new JLabel("⭢");

        // Colors and fonts
        for (JLabel label : Arrays.asList(signLabel, senderLabel, receiverLabel, dateLabel, titleLabel, amountLabel, arrowLabel)) {
            label.setFont(Fonts.getStandardFont());
            label.setForeground(Colors.getBrightTextColor());
            label.setPreferredSize(new Dimension(10, 25));
        }

        // Layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.setBackground(Colors.getGrey());
        this.setPreferredSize(new Dimension(600, 50));
        this.setMinimumSize(new Dimension(150, 50));
        this.setMaximumSize(new Dimension(5000, 50));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.gridwidth = 1;

        // Adding subcomponents
        signLabel.setToolTipText(signText);
        signLabel.setForeground(color);
        signLabel.setFont(Fonts.getAlternativeFont().deriveFont(40f));
        signLabel.setHorizontalAlignment(JLabel.CENTER);
        signLabel.setPreferredSize(new Dimension(80, 50));
        c.gridy = 0;
        c.gridx = 0;
        c.gridheight = 2;
        c.weightx = 0;
        this.add(signLabel, c);

        titleLabel.setForeground(Colors.getOrange());
        titleLabel.setFont(Fonts.getStandardFont().deriveFont(Font.BOLD));
        c.weightx = 1;
        c.gridx = 1;
        c.gridheight = 1;
        c.gridwidth = 3;
        this.add(titleLabel, c);

        //dateLabel.setPreferredSize(new Dimension(100, 50));
        dateLabel.setForeground(Colors.getBrightGrey());
        c.weightx = 1;
        c.gridx = 4;
        c.gridwidth = 2;
        this.add(dateLabel, c);

        amountLabel.setForeground(color);
        amountLabel.setFont(Fonts.getHeaderFont().deriveFont(26f));
        amountLabel.setHorizontalAlignment(JLabel.RIGHT);
        amountLabel.setPreferredSize(new Dimension(120, 50));
        c.insets = new Insets(0, 0, 0, 2);
        c.weightx = 0;
        c.gridx = 6;
        c.gridwidth = 1;
        c.gridheight = 2;
        this.add(amountLabel, c);

        currencyLabel.setForeground(color);
        currencyLabel.setFont(Fonts.getHeaderFont().deriveFont(18f));
        currencyLabel.setHorizontalAlignment(JLabel.LEFT);
        currencyLabel.setPreferredSize(new Dimension(45, 50));
        c.insets = new Insets(0, 2, 0, 0);
        c.gridx = 7;
        c.gridwidth = 1;
        this.add(currencyLabel, c);

        c.insets = new Insets(0, 0, 0, 0);
        c.weightx = 1;
        c.gridy = 1;
        c.gridx = 1;
        c.gridheight = 1;
        c.gridwidth = 2;
        this.add(senderLabel, c);

        arrowLabel.setHorizontalAlignment(JLabel.CENTER);
        arrowLabel.setFont(Fonts.getAlternativeFont());
        c.weightx = 0.1;
        c.gridx = 3;
        c.gridwidth = 1;
        this.add(arrowLabel, c);

        c.weightx = 1;
        c.gridx = 4;
        c.gridwidth = 2;
        this.add(receiverLabel, c);
    }

    private void addListeners() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Colors.getLightGrey());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(Colors.getGrey());
            }
        });
    }
}