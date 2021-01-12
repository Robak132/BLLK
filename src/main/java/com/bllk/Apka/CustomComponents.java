package com.bllk.Apka;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Colors {
    private static Color bright_text_color, orange, blue, dark_grey, light_grey;
    public Colors() {
        bright_text_color = Color.decode("#EEEEEE");
        blue = Color.decode("#1891FF");
        orange = Color.decode("#FF7F00");
        dark_grey = Color.decode("#222222");
        light_grey = Color.decode("#333333");
    }
    public static Color getBrightTextColor() {
        return bright_text_color;
    }
    public static Color getOrange() {
        return orange;
    }
    public static Color getBlue() {
        return blue;
    }
    public static Color getDarkGrey() {
        return dark_grey;
    }
    public static Color getLightGrey() {
        return light_grey;
    }
}

class Fonts {
    private static Font radikal, adagio_slab;

    public Fonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        radikal = loadFont("radikalwut-bold.otf");
        adagio_slab = loadFont("adagio_slab-regular.otf");
        ge.registerFont(radikal);
        ge.registerFont(adagio_slab);
    }
    private Font loadFont(String font_name) {
        String path = "/fonts/" + font_name;
        Font font = new Font("Veranda", Font.PLAIN, 12);
        try {
            InputStream stream = Fonts.class.getResourceAsStream(path);
            font = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            System.out.println("Have not found font file. Using default Veranda.");
        }
        return font;
    }
    public static Font getStandardFont() {
        return adagio_slab.deriveFont(14f);
    }
    public static Font getHeaderFont() {
        return adagio_slab.deriveFont(20f);
    }
    public static Font getLogoFont() {
        return radikal.deriveFont(48f);
    }
}






class AccountPanel extends JPanel {
    public AccountPanel(String _account_name, String _account_number, String _balance, String _currency) {
        super();


        if (_account_name.equals(_account_number))
            _account_name = "Konto";
        JLabel balanceLabel = new JLabel(_balance);
        JLabel currencyLabel = new JLabel(_currency);

        balanceLabel.setForeground(Colors.getBrightTextColor());
        currencyLabel.setForeground(Colors.getBrightTextColor());

        balanceLabel.setFont(Fonts.getStandardFont());
        currencyLabel.setFont(Fonts.getStandardFont());

        this.setLayout(new FlowLayout());

        this.add(balanceLabel);
        this.add(currencyLabel);

        this.setBackground(Colors.getLightGrey());
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 50));
        this.setPreferredSize(new Dimension(150, -1));

        Font font = StartWindow.fonts.adagio_slab;
        font = font.deriveFont(12f);

        this.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Colors.getOrange(), 3, true),
                _account_name + " (nr " + _account_number + ")",
                TitledBorder.CENTER,
                TitledBorder.DEFAULT_POSITION,
                Fonts.getStandardFont(),
                Colors.getBrightTextColor()
                ));
    }
}



class ContactPanel extends JPanel {
    public ContactPanel(JPanel parent, MainUserPage page, int target_id, String name) {
        super();

        this.setLayout(new FlowLayout(FlowLayout.LEADING));

        JLabel nameLabel = new JLabel(name);
        JLabel targetidLabel = new JLabel("" + target_id);
        JButton deleteButton = new JButton("Usuń");

        deleteButton.addActionListener(e -> {
            page.connection.removeContact(page.login.getLogin(), page.login.getPasswordHash(), target_id);
            page.updateContacts();
            parent.remove(this);
            parent.updateUI();
        });
        this.add(nameLabel);
        this.add(targetidLabel);
        this.add(deleteButton);
    }
}

class InvestmentPanel extends JPanel {
    MainUserPage page;
    JSONObject inv;

    public InvestmentPanel(JPanel parent, MainUserPage _page, int _id, JSONObject _inv) {
        super();
        page = _page;
        inv = _inv;

        JLabel nameLabel = new JLabel(inv.getString("name"));
        JLabel valueLabel = new JLabel("" + inv.getDouble("value") / 100);
        JLabel currencyLabel = new JLabel(page.currencies.get(inv.getString("currencyid")));
        JLabel profitLabel = new JLabel(inv.getString("profit"));
        JLabel yearprofit = new JLabel(inv.getString("yearprofit"));
        JLabel capperiod = new JLabel(inv.getString("capperiod"));
        JLabel datecreated = new JLabel(inv.getString("datecreated"));
        JButton endbutton = new JButton("Zamknij lokatę");

        this.setBackground(Colors.getLightGrey());
        nameLabel.setForeground(Colors.getBrightTextColor());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setForeground(Colors.getBrightTextColor());
        currencyLabel.setForeground(Colors.getBrightTextColor());
        profitLabel.setForeground(Colors.getBrightTextColor());
        yearprofit.setForeground(Colors.getBrightTextColor());
        capperiod.setForeground(Colors.getBrightTextColor());
        datecreated.setForeground(Colors.getBrightTextColor());
        datecreated.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Colors.getOrange(), 3, true));
        this.setMaximumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(150, -1));

        this.add(nameLabel);
        JPanel p1 = new JPanel();
        p1.setBackground(Colors.getLightGrey());
        p1.add(valueLabel);
        p1.add(currencyLabel);
        this.add(p1);
        JPanel p2 = new JPanel();
        p2.setBackground(Colors.getLightGrey());
        p2.add(profitLabel);
        p2.add(yearprofit);
        p2.add(capperiod);
        this.add(p2);
        this.add(datecreated);

        endbutton.addActionListener(e -> {
            int accountid = removeInvestmentDialog();
            System.out.println(accountid);
            page.connection.removeInvestment(page.login.getLogin(), page.login.getPasswordHash(), _id, accountid);
            page.updateInvestmentsSummary();
            page.updateAccounts();
        });

        if (inv.has("dateended")) {
            JLabel dateendedLabel = new JLabel(inv.getString("dateended"));
            dateendedLabel.setForeground(Colors.getBrightTextColor());
            dateendedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(dateendedLabel);
        }
        endbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(endbutton);
    }
    int removeInvestmentDialog() {
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accounts_to_select = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : page.accounts.entrySet()) {
            if (account.getValue().getInt("currencyid") == inv.getInt("currencyid")) {
                accountBox.addItem(String.format("%s (%.2f %s)", page.getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, page.currencies.get(account.getValue().getString("currencyid"))));
                accounts_to_select.add(account.getKey());
            }
        }

        Object[] message = {
                "Na konto:", accountBox,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return accounts_to_select.get(accountBox.getSelectedIndex());
        }
        return -1;
    }
}