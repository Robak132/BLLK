package com.bllk.Apka;

import com.bllk.Apka.customComponents.*;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainUserPage {
    static JFrame frame = StartWindow.frame;
    static ClientServerConnection connection = StartWindow.connection;
    JPanel currentPanel;

    Client client;
    static Login login;

    private JLabel logoLabel, nameLabel;
    private JTextField transfer_amount;
    private JButton transfer_sendMoneyButton, logOutButton;
    private JLabel transfer_message;
    private JLabel transfer_currentBalance;
    private JLabel idLabel;
    private JPanel transactionPanel;
    private JTabbedPane tabbedPane;
    private JTextField transfer_title;
    private JComboBox<String> transfer_accountSelectBox;
    private JLabel transfer_payerBalance;
    private JLabel transfer_currencyLabel;
    private JScrollPane historyPane;
    private JButton createAccountButton;
    private JComboBox<String> transfer_contactBox;
    private JTextField transfer_accountNumber;
    private JButton transfer_addContactButton;
    private JPanel accountsSummaryPanel;
    private JPanel contactsSummary;
    private JLabel creditsBalance;
    private JPanel investmentsSummaryPanel;
    private JButton createInvestmentButton;
    private JButton createCreditButton;
    private JTabbedPane financialProductsTabbedPane;
    private JLabel accountsSummaryLabel;
    private JLabel transfer_currentBalanceLabel;
    private JLabel transfer_titleLabel;
    private JLabel transfer_fromLabel;
    private JLabel transfer_contactNameLabel;
    private JLabel transfer_toLabel;
    private JLabel transfer_valueLabel;
    private JPanel financialProductsPanel;
    private JPanel transactionHistoryPanel;
    private JPanel accountsPanel;
    private JPanel contactsPanel;
    private JPanel settingsPanel;
    private JPanel creditsSummaryPanel;
    private JPanel creditsBalancePanel;
    private JPanel investmentsPanel;
    private JPanel creditsPanel;
    private JButton changeLoginButton;
    private JButton changePasswordButton;
    private JTextField loginField;
    private JLabel loginPasswordLabel;
    private JLabel creditsBalanceLabel;
    private JPanel historyPanel;
    private JScrollPane accountsSummaryPane;
    private JScrollPane creditsSummaryPane;
    private JScrollPane investmentsSummaryPane;

    List<Integer> user_currencies = new ArrayList<>();
    List<Integer> accountBoxUnformatted = new ArrayList<>();
    Account activePayerAccount = null;
    Map<String, Integer> contacts;
    private static Map<Integer, JSONObject> accounts;
    private static Map<String, String> currencies;
    boolean lock_combobox = false;

    public MainUserPage(Client _client, Login _login) {
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        loginField.setText(login.getLogin());
        currencies = connection.getCurrencies();

        accountsSummaryPanel.setLayout(new GridBagLayout());
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        investmentsSummaryPanel.setLayout(new BoxLayout(investmentsSummaryPanel, BoxLayout.Y_AXIS));
        creditsSummaryPanel.setLayout(new BoxLayout(creditsSummaryPanel, BoxLayout.Y_AXIS));
        updateFontsAndColors();

        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        updateContacts();
        updateAccounts();
        updateTransactionTable();
        updateAccountsSummary();
        updateContactsSummary();
        updateCreditsBalance();
        updateInvestmentsSummary();
        updateCreditsSummary();

        transfer_sendMoneyButton.addActionListener(e -> makeTransaction());
        logOutButton.addActionListener(e -> {
            StartWindow.startingPanel.setSize(currentPanel.getSize());
            frame.setContentPane(StartWindow.startingPanel);
        });
        transfer_accountSelectBox.addActionListener(e -> updateMoney());
        transfer_addContactButton.addActionListener(e -> {
            addContact();
            updateContacts();
            updateContactsSummary();
        });
        transfer_contactBox.addActionListener(e -> {
            if (!lock_combobox) {
                String name = (String) transfer_contactBox.getSelectedItem();
                Integer accountid = contacts.get(name);
                if (accountid != null)
                    transfer_accountNumber.setText("" + accountid);
            }
        });
        createAccountButton.addActionListener(e -> createAccountDialog());
        createInvestmentButton.addActionListener(e -> addInvestmentDialog());
        createCreditButton.addActionListener(e -> addCreditDialog());
        changeLoginButton.addActionListener(e -> changeLoginDialog());
        changePasswordButton.addActionListener(e -> changePasswordDialog());
    }

    void createAccountDialog() {
        JComboBox<String> currenciesComboBox = new JComboBox<>();

        for (Map.Entry<String, String> currency : currencies.entrySet()) {
            if (!user_currencies.contains(Integer.parseInt(currency.getKey()))) {
                currenciesComboBox.addItem(currency.getValue());
            }
        }

        Object[] message = {
                "Wybierz walutę", currenciesComboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Dodawanie nowego konta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (Map.Entry<String, String> entry : currencies.entrySet()) {
                if (Objects.equals(currenciesComboBox.getSelectedItem(), entry.getValue())) {
                    int currency_id = Integer.parseInt(entry.getKey());
                    connection.createAccount(login.getLogin(), login.getPasswordHash(), currency_id);
                }
            }
        }
        updateAccounts();
    }

    void addInvestmentDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accountsToSelect = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()),
                    account.getValue().getDouble("value") / 100,
                    currencies.get(account.getValue().getString("currencyid"))));
            accountsToSelect.add(account.getKey());
        }
        JTextField value = new JTextField();
        JTextField capPeroid = new JTextField();

        JLabel profitRateValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel yearProfitRateValue = new JLabel("Oprocentowanie roczne: 5,0%");
        JSlider profitRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider yearProfitRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        profitRate.addChangeListener(e -> profitRateValue.setText("Oprocentowanie: " + String.format("%.1f", profitRate.getValue() / 10f) + "%"));
        yearProfitRate.addChangeListener(e -> yearProfitRateValue.setText("Oprocentowanie roczne: " + String.format("%.1f", yearProfitRate.getValue() / 10f) + "%"));

        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();
        slidersLabelTable.put(2, new JLabel("0%"));
        slidersLabelTable.put(22, new JLabel("2%"));
        slidersLabelTable.put(42, new JLabel("4%"));
        slidersLabelTable.put(62, new JLabel("6%"));
        slidersLabelTable.put(82, new JLabel("8%"));
        slidersLabelTable.put(100, new JLabel("10%"));

        profitRate.setMajorTickSpacing(10);
        profitRate.setMinorTickSpacing(5);
        profitRate.setPaintTicks(true);
        profitRate.setLabelTable(slidersLabelTable);
        profitRate.setPaintLabels(true);

        yearProfitRate.setMajorTickSpacing(10);
        yearProfitRate.setMinorTickSpacing(5);
        yearProfitRate.setPaintTicks(true);
        yearProfitRate.setLabelTable(slidersLabelTable);
        yearProfitRate.setPaintLabels(true);

        Object[] message = {
                "Nazwa:", name,
                "Z konta:", accountBox,
                "Kwota początkowa", value,
                profitRateValue, profitRate,
                yearProfitRateValue, yearProfitRate,
                "Okres kapitalizacji [mies.]", capPeroid,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowa lokata", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (name.getText().isEmpty() || value.getText().isEmpty() || capPeroid.getText().isEmpty())
                JOptionPane.showMessageDialog(null, "Pole nie może być puste.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    long integerValue = (long) Double.parseDouble(value.getText().replace(",", ".")) * 100;
                    int integerCapPeroid = Integer.parseInt(capPeroid.getText());

                    if (accounts.get(accountsToSelect.get(accountBox.getSelectedIndex())).getInt("value") < integerValue)
                        JOptionPane.showMessageDialog(null, "Nie posiadasz tyle pieniędzy.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                    else {
                        connection.createInvestment(login.getLogin(), login.getPasswordHash(), name.getText(),
                                integerValue, profitRate.getValue() / 1000.0, yearProfitRate.getValue() / 1000.0,
                                integerCapPeroid, accountsToSelect.get(accountBox.getSelectedIndex()));
                        updateInvestmentsSummary();
                        updateAccounts();
                        JOptionPane.showMessageDialog(null, "Operacja powiodła się.", "Sukces", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Błędna wartość.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    void addCreditDialog() {
        JTextField name = new JTextField();
        JComboBox<String> accountBox = new JComboBox<>();
        List<Integer> accountsToSelect = new ArrayList<>();
        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            accountBox.addItem(String.format("%s (%.2f %s)", getContactIfPossible(account.getKey()), account.getValue().getDouble("value") / 100, currencies.get(account.getValue().getString("currencyid"))));
            accountsToSelect.add(account.getKey());
        }
        JTextField value = new JTextField();
        value.setText("1000");
        JLabel interestValue = new JLabel("Oprocentowanie: 5,0%");
        JLabel commissionValue = new JLabel("Prowizja: 5,0%");

        JSlider interestRate = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        JSlider commission = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

        ChangeListener interestChangeListener = e -> {
            JSlider slider = (JSlider) e.getSource();
            interestValue.setText("Oprocentowanie: " + String.format("%.1f", slider.getValue() / 10f) + "%");
        };
        ChangeListener commissionChangeListener = e -> {
            JSlider slider = (JSlider) e.getSource();
            commissionValue.setText("Prowizja: " + String.format("%.1f", slider.getValue() / 10f) + "%");
        };

        commission.addChangeListener(commissionChangeListener);
        commission.setMajorTickSpacing(10);
        commission.setMinorTickSpacing(5);
        commission.setPaintTicks(true);
        commission.setPaintLabels(true);

        interestRate.addChangeListener(interestChangeListener);
        interestRate.setMajorTickSpacing(10);
        interestRate.setMinorTickSpacing(5);
        interestRate.setPaintTicks(true);
        interestRate.setPaintLabels(true);

        //Create the label table
        Hashtable<Integer, JLabel> slidersLabelTable = new Hashtable<>();
        slidersLabelTable.put(2, new JLabel("0%"));
        slidersLabelTable.put(22, new JLabel("2%"));
        slidersLabelTable.put(42, new JLabel("4%"));
        slidersLabelTable.put(62, new JLabel("6%"));
        slidersLabelTable.put(82, new JLabel("8%"));
        slidersLabelTable.put(100, new JLabel("10%"));

        commission.setLabelTable(slidersLabelTable);
        commission.setPaintLabels(true);
        interestRate.setLabelTable(slidersLabelTable);
        interestRate.setPaintLabels(true);

        List<String> monthsList = new ArrayList<>();
        for (int i = 1; i <= 120; i++)
            monthsList.add(i + "");
        SpinnerListModel monthsModel = new SpinnerListModel(monthsList.toArray());
        JSpinner months = new JSpinner(monthsModel);
        months.setValue("24");

        Object[] message = {
                "Nazwa:", name,
                "Na konto:", accountBox,
                "Kwota początkowa", value,
                interestValue, interestRate,
                commissionValue, commission,
                "Całkowity okres spłaty [mies.]", months,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nowy kredyt", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            connection.createCredit(
                    login.getLogin(),
                    login.getPasswordHash(),
                    name.getText(),
                    (long) (Double.parseDouble(value.getText()) * 100),
                    interestRate.getValue() / 1000.0,
                    commission.getValue() / 1000.0,
                    Integer.parseInt((String) months.getValue()),
                    accountsToSelect.get(accountBox.getSelectedIndex())
            );
            updateCreditsSummary();
            updateAccounts();
            JOptionPane.showMessageDialog(null, "Operacja powiodła się.", "Sukces", JOptionPane.ERROR_MESSAGE);
        }
    }

    void changeLoginDialog() {
        JTextField new_name = new JTextField();

        Object[] message = {
                "Czy chcesz zmienić login '" + login.getLogin() + "'?",
                "Nowy login:", new_name
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana loginu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String new_name_string = new_name.getText();
            if (new_name_string.isEmpty())
                JOptionPane.showMessageDialog(null, "Pole nie może być puste.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (connection.checkLogin(new_name_string))
                JOptionPane.showMessageDialog(null, "Login jest zajęty.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                if (connection.updateLogin(login.getLogin(), login.getPasswordHash(), new_name_string)) {
                    JOptionPane.showMessageDialog(null, "Zmiana loginu powiodła się.", "Sukces", JOptionPane.ERROR_MESSAGE);
                    loginField.setText(new_name_string);
                } else
                    JOptionPane.showMessageDialog(null, "Serwer odrzucił żądanie", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void changePasswordDialog() {
        JTextField newPassword = new JPasswordField();
        JTextField newPasswordRepeat = new JPasswordField();

        Object[] message = {
                "Czy chcesz zmienić twoje hasło?",
                "Nowe hasło:", newPassword,
                "Powtórz hasło", newPasswordRepeat
        };

        int option = JOptionPane.showConfirmDialog(null, message,
                "Zmiana hasła", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newPasswordString = newPassword.getText();
            String newPasswordRepeatString = newPasswordRepeat.getText();
            int passwordLength = newPasswordString.length();

            if (newPasswordString.isEmpty())
                JOptionPane.showMessageDialog(null, "Pole nie może być puste.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (passwordLength < StartWindow.passwordMinimumLength || passwordLength > StartWindow.passwordMaximumLength)
                JOptionPane.showMessageDialog(null, "Hasło musi mieć od 8 do 16 znaków.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (!newPasswordString.equals(newPasswordRepeatString))
                JOptionPane.showMessageDialog(null, "Hasła nie są identyczne.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                String hashedPassword = BCrypt.hashpw(newPasswordString, BCrypt.gensalt(12));
                connection.updatePassword(login.getLogin(), hashedPassword);
                JOptionPane.showMessageDialog(null, "Zmiana hasła powiodła się.", "Sukces", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    boolean currencyChangeWarning() {
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Konto, na które zamierzasz wysłać przelew, zawiera inną walutę niż wysyłana, czy chcesz przewalutować?",
                "Przewalutowanie",
                JOptionPane.YES_NO_OPTION);
        return n == 0;
    }

    void addContact() {
        try {
            int accountID = Integer.parseInt(transfer_accountNumber.getText());
            String name = (String) transfer_contactBox.getSelectedItem();
            if (!name.equals("")) {
                if (!connection.checkAccount(Integer.parseInt(transfer_accountNumber.getText())))
                    throw new NumberFormatException();
                connection.createOrUpdateContact(login.getLogin(), login.getPasswordHash(), name, accountID);
                transfer_message.setText(String.format("Konto %d: %s", accountID, name));
            }

        } catch (NumberFormatException ex) {
            transfer_message.setText("Nie można dodać kontaktu: błędny numer konta.");
            System.out.println(ex.getMessage());
        } catch (InputMismatchException ex) {
            transfer_message.setText("Nie można dodać kontaktu: błędna nazwa.");
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            transfer_message.setText("Nie można dodać kontaktu.");
            System.out.println(ex.getMessage());
        }
    }

    void makeTransaction() {
        try {
            if (activePayerAccount == null) {
                transfer_message.setText("Błąd transakcji: Nie posiadasz żadnego konta.");
            } else {
                int payerID = activePayerAccount.getID();
                int targetID = Integer.parseInt(transfer_accountNumber.getText());
                int currencyID = activePayerAccount.getCurrencyID();
                long moneyValue = (long) (Double.parseDouble(transfer_amount.getText()) * 100);
                String title = transfer_title.getText();

                if (activePayerAccount.getID() == targetID) {
                    transfer_message.setText("Błąd transakcji: Konto docelowe jest takie samo jak początkowe.");
                } else if (moneyValue > activePayerAccount.getValue() || moneyValue <= 0) {
                    transfer_message.setText("Błąd transakcji: Błędna kwota przelewu.");
                } else if (!connection.checkAccount(Integer.parseInt(transfer_accountNumber.getText()))) {
                    transfer_message.setText("Błąd transakcji: Konto docelowe nie istnieje.");
                } else if (transfer_title.getText().equals("")) {
                    transfer_message.setText("Błąd transakcji: Tytuł nie może być pusty.");
                } else {
                    if (connection.getBasicAccount(targetID).getCurrencyID() == currencyID || (connection.getBasicAccount(targetID).getCurrencyID() != currencyID && currencyChangeWarning())) {
                        transfer_message.setText(String.format("Przesłano %.2f %s na konto %d.", moneyValue / 100.0, currencies.get("" + activePayerAccount.getCurrencyID()), targetID));
                        connection.makeTransfer(login.getLogin(), login.getPasswordHash(), payerID, targetID, title, moneyValue, currencyID);
                        updateMoney();
                        updateTransactionTable();
                        updateAccountsSummary();
                    }
                }
            }
        } catch (Exception ex) {
            transfer_message.setText("Błąd transakcji: " + ex.getMessage());
        }
    }

    public String getContactIfPossible(int value) {
        for (Map.Entry<String, Integer> contact : contacts.entrySet())
            if (contact.getValue() == value)
                return contact.getKey();
        return String.valueOf(value);
    }

    private void updateFontsAndColors() {
        Colors colors = new Colors();
        Fonts fonts = new Fonts();

        Font standardFont = Fonts.getStandardFont();
        Font headerFont = Fonts.getHeaderFont();
        Font logoFont = Fonts.getLogoFont();


        // Main elements
        logoLabel.setFont(logoFont);
        logoLabel.setForeground(Colors.getOrange());

        nameLabel.setFont(headerFont);

        logOutButton.setFont(standardFont);

        for (JLabel jLabel : Arrays.asList(idLabel, nameLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
        }

        for (JTabbedPane jTabbedPane : Arrays.asList(tabbedPane, financialProductsTabbedPane)) {
            jTabbedPane.setFont(standardFont);
            jTabbedPane.setForeground(Colors.getDarkGrey());
            jTabbedPane.setBackground(Colors.getBlue());
        }

        for (JPanel jPanel : Arrays.asList(accountsPanel, transactionPanel, transactionHistoryPanel,
                financialProductsPanel, contactsPanel, settingsPanel, investmentsPanel, creditsPanel, historyPanel)) {
            jPanel.setFont(standardFont);
            jPanel.setForeground(Colors.getBrightTextColor());
            jPanel.setBackground(Colors.getDarkGrey());
        }

        // Accounts
        createAccountButton.setFont(standardFont);
        accountsSummaryLabel.setFont(Fonts.getHeaderFont());
        accountsSummaryLabel.setForeground(Colors.getBrightTextColor());
        accountsSummaryPanel.setForeground(Colors.getBrightTextColor());
        accountsSummaryPanel.setBackground(Colors.getDarkGrey());
        accountsSummaryPane.setBackground(Colors.getDarkGrey());

        // Transfers
        for (JLabel jLabel : Arrays.asList(transfer_contactNameLabel, transfer_currencyLabel, transfer_currentBalance,
                transfer_currentBalanceLabel, transfer_fromLabel, transfer_message, transfer_payerBalance,
                transfer_titleLabel, transfer_toLabel, transfer_valueLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
            jLabel.setFont(standardFont);
        }
        for (JTextField jTextField : Arrays.asList(transfer_accountNumber, transfer_amount, transfer_title)) {
            jTextField.setForeground(Colors.getBrightTextColor());
            jTextField.setBackground(Colors.getBrightGrey());
            jTextField.setFont(standardFont);
        }
        for (JComboBox<String> jComboBox : Arrays.asList(transfer_contactBox, transfer_accountSelectBox)) {
            jComboBox.setForeground(Colors.getOrange());
            jComboBox.setBackground(Colors.getGrey());
            jComboBox.setFont(standardFont);
        }
        for (JButton jButton : Arrays.asList(transfer_addContactButton, transfer_sendMoneyButton)) {
            jButton.setFont(standardFont);
        }

        // Transfer history
        historyPane.setFont(standardFont);
        historyPane.setBackground(Colors.getDarkGrey());
        historyPane.getViewport().setBackground(Colors.getDarkGrey());

        // Settings
        loginPasswordLabel.setFont(Fonts.getHeaderFont());
        loginPasswordLabel.setForeground(Colors.getBrightTextColor());
        loginField.setFont(standardFont);

        // Investments
        createInvestmentButton.setFont(standardFont);

        // Credits
        createCreditButton.setFont(standardFont);
        creditsBalance.setFont(Fonts.getHeaderFont());
        creditsBalance.setForeground(Colors.getBrightTextColor());
        creditsBalanceLabel.setFont(Fonts.getHeaderFont());
        creditsBalanceLabel.setForeground(Colors.getBrightTextColor());

        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            tabbedPane.setForeground(Colors.getOrange());
            financialProductsTabbedPane.setForeground(Colors.getOrange());
        }
    }

    public void updateContacts() {
        lock_combobox = true;
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        String temp = (String) transfer_contactBox.getSelectedItem();
        transfer_contactBox.removeAllItems();
        transfer_contactBox.addItem("");
        for (Map.Entry<String, Integer> contact : contacts.entrySet())
            transfer_contactBox.addItem(contact.getKey());
        transfer_contactBox.setSelectedItem(temp);
        updateTransactionTable();
        lock_combobox = false;
    }

    private void updateTransactionTable() {
        historyPanel.removeAll();
        //String[] columns = new String[] {"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"};
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        char type = 0;

        for (JSONObject transaction : transactions.values()) {
            int senderID = transaction.getInt("senderid");
            int receiverID = transaction.getInt("receiverid");

            if (accounts.containsKey(senderID) && !accounts.containsKey(receiverID)) {
                type = 0; // outgoing
            } else if (accounts.containsKey(receiverID) && !accounts.containsKey(senderID)) {
                type = 1; // incoming
            } else if (accounts.containsKey(senderID) && accounts.containsKey(receiverID)) {
                type = 2; // between own accounts
            } else {
                System.out.println("Something went terribly wrong.");
            }

            historyPanel.add(new TransactionPanel(getContactIfPossible(senderID),
                    getContactIfPossible(receiverID),
                    transaction.getString("date"),
                    transaction.getString("title"),
                    transaction.getDouble("value"),
                    currencies.get(transaction.getString("currencyid")),
                    type
            ));
            historyPanel.add(new Box.Filler(new Dimension(1, 1), new Dimension(100, 1), new Dimension(600, 1)));
        }
    }

    public void updateMoney() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            if (transfer_accountSelectBox.getItemCount() > 0) {
                activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
                String activeCurrencyShortcut = currencies.get("" + activePayerAccount.getCurrencyID());
                long total_balance = connection.getTotalSavings(login.getLogin(), login.getPasswordHash(), activePayerAccount.getCurrencyID());

                transfer_currentBalance.setText(String.format("%.2f %s", total_balance / 100.0, activeCurrencyShortcut));
                transfer_payerBalance.setText(String.format("%.2f %s", activePayerAccount.getValue() / 100.0, activeCurrencyShortcut));
                transfer_currencyLabel.setText(activeCurrencyShortcut);
            }
        }
    }

    public void updateAccounts() {
        transfer_accountSelectBox.removeAllItems();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            transfer_accountSelectBox.addItem(String.format("%s (%s)",
                    getContactIfPossible(account.getKey()),
                    currencies.get(account.getValue().getString("currencyid")))
            );
            accountBoxUnformatted.add(account.getKey());
            user_currencies.add(account.getValue().getInt("currencyid"));
        }

        tabbedPane.setEnabledAt(2, transfer_accountSelectBox.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }

    public void updateAccountsSummary() {
        accountsSummaryPanel.removeAll();
        int column = 0, row = 1, counter = 1, accountsCount = accounts.size();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;

        c.gridy = 0;
        if (accountsCount != 0) {
            for (int i = 0; i < 6; i++) {
                JLabel nothing = new JLabel("");
                c.gridx = i;
                accountsSummaryPanel.add(nothing, c);
            }
        } else {
            c.gridx = 0;
            JLabel noAccountInformation = new JLabel("Nie masz żadnego konta. Możesz dodać je poniżej.");
            noAccountInformation.setFont(Fonts.getStandardFont());
            noAccountInformation.setForeground(Colors.getBrightTextColor());
            accountsSummaryPanel.add(noAccountInformation, c);
        }

        c.gridwidth = 2;
        for (Map.Entry<Integer, JSONObject> account : accounts.entrySet()) {
            String currencyID = account.getValue().getString("currencyid");
            String currencyName = currencies.get(currencyID);
            int balance = account.getValue().getInt("value");
            String formattedBalance = String.format("%.2f", balance / 100.0);

            AccountPanel accountPanel = new AccountPanel(getContactIfPossible(account.getKey()),
                    "" + account.getKey(), formattedBalance, currencyName, this);

            if (counter == accountsCount - 1) {
                if (accountsCount % 3 == 2) column = 1;
            } else if (counter == accountsCount) {
                if (accountsCount % 3 == 1) column = 2;
            }

            counter++;
            c.gridx = column;
            c.gridy = row;
            column += 2;
            if (column % 6 == 0) {
                column = 0;
                row++;
            }
            accountsSummaryPanel.add(accountPanel, c);
        }
        refreshFrame();
    }

    private void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact : contacts.entrySet()) {
            if (!accounts.containsKey(contact.getValue())) {
                ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
                contactsSummary.add(contactPanel);
                contactsSummary.add(new JSeparator());
            }
        }
    }

    public void updateInvestmentsSummary() {
        investmentsSummaryPanel.removeAll();
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> investment : investments.entrySet()) {
            InvestmentPanel investmentPanel = new InvestmentPanel(this, investment.getKey(), investment.getValue());
            investmentsSummaryPanel.add(investmentPanel);
        }
        investmentsSummaryPanel.updateUI();
    }

    public void updateCreditsSummary() {
        creditsSummaryPanel.removeAll();
        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> credit : credits.entrySet()) {
            CreditPanel creditPanel = new CreditPanel(this, credit.getKey(), credit.getValue());
            creditsSummaryPanel.add(creditPanel);
        }
        updateCreditsBalance();
        creditsSummaryPanel.updateUI();
    }

    private void updateCreditsBalance() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            String active_currency_shortcut = currencies.get("" + activePayerAccount.getCurrencyID());
            long credits_total = connection.getTotalCredits(login.getLogin(), login.getPasswordHash(), activePayerAccount.getCurrencyID());

            creditsBalance.setText(String.format("%.2f %s", credits_total / 100.0, active_currency_shortcut));
        }
    }

    private void refreshFrame() {
        Dimension dimension = frame.getSize();
        frame.setSize(dimension.width, dimension.height + 10);
        frame.setSize(dimension);
    }

    public static ClientServerConnection getConnection() {
        return connection;
    }

    public static Login getLogin() {
        return login;
    }

    public static Map<String, String> getCurrencies() {
        return currencies;
    }

    public static Map<Integer, JSONObject> getAccounts() {
        return accounts;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        currentPanel = new JPanel();
        currentPanel.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        currentPanel.setBackground(new Color(-14540254));
        currentPanel.setMinimumSize(new Dimension(640, 320));
        currentPanel.setPreferredSize(new Dimension(640, 320));
        currentPanel.setRequestFocusEnabled(true);
        logoLabel = new JLabel();
        Font logoLabelFont = this.$$$getFont$$$("Radikal WUT", -1, 48, logoLabel.getFont());
        if (logoLabelFont != null) logoLabel.setFont(logoLabelFont);
        logoLabel.setForeground(new Color(-33024));
        logoLabel.setText("BLLK");
        currentPanel.add(logoLabel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logOutButton = new JButton();
        logOutButton.setText("Wyloguj się");
        currentPanel.add(logOutButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(-15166977));
        Font tabbedPaneFont = this.$$$getFont$$$("Adagio_Slab", -1, 14, tabbedPane.getFont());
        if (tabbedPaneFont != null) tabbedPane.setFont(tabbedPaneFont);
        tabbedPane.setForeground(new Color(-14540254));
        tabbedPane.setOpaque(false);
        tabbedPane.setTabLayoutPolicy(0);
        currentPanel.add(tabbedPane, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
        accountsPanel = new JPanel();
        accountsPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        accountsPanel.setBackground(new Color(-14540254));
        tabbedPane.addTab("Konta", accountsPanel);
        accountsSummaryLabel = new JLabel();
        Font accountsSummaryLabelFont = this.$$$getFont$$$("Adagio_Slab", Font.BOLD, 20, accountsSummaryLabel.getFont());
        if (accountsSummaryLabelFont != null) accountsSummaryLabel.setFont(accountsSummaryLabelFont);
        accountsSummaryLabel.setForeground(new Color(-1118482));
        accountsSummaryLabel.setText("Podsumowanie kont:");
        accountsPanel.add(accountsSummaryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        accountsSummaryPane = new JScrollPane();
        accountsPanel.add(accountsSummaryPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        accountsSummaryPanel = new JPanel();
        accountsSummaryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        accountsSummaryPanel.setBackground(new Color(-14540254));
        accountsSummaryPane.setViewportView(accountsSummaryPanel);
        createAccountButton = new JButton();
        createAccountButton.setText("Stwórz konto");
        accountsPanel.add(createAccountButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transactionPanel = new JPanel();
        transactionPanel.setLayout(new GridLayoutManager(15, 4, new Insets(10, 10, 10, 10), -1, -1));
        transactionPanel.setBackground(new Color(-14540254));
        transactionPanel.setEnabled(true);
        tabbedPane.addTab("Wykonaj przelew", transactionPanel);
        final Spacer spacer1 = new Spacer();
        transactionPanel.add(spacer1, new GridConstraints(14, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(139, 14), null, 0, false));
        transfer_amount = new JTextField();
        transfer_amount.setBackground(new Color(-14540254));
        transfer_amount.setForeground(new Color(-33024));
        transfer_amount.setText("");
        transactionPanel.add(transfer_amount, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        transfer_toLabel = new JLabel();
        transfer_toLabel.setForeground(new Color(-1118482));
        transfer_toLabel.setText("Na konto:");
        transactionPanel.add(transfer_toLabel, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(201, 16), null, 0, false));
        transfer_valueLabel = new JLabel();
        transfer_valueLabel.setForeground(new Color(-1118482));
        transfer_valueLabel.setText("Kwota:");
        transactionPanel.add(transfer_valueLabel, new GridConstraints(10, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        transactionPanel.add(spacer2, new GridConstraints(0, 3, 15, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        transfer_sendMoneyButton = new JButton();
        transfer_sendMoneyButton.setFocusable(false);
        transfer_sendMoneyButton.setText("Wykonaj przelew");
        transactionPanel.add(transfer_sendMoneyButton, new GridConstraints(12, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_message = new JLabel();
        transfer_message.setForeground(new Color(-33024));
        transfer_message.setText(" ");
        transactionPanel.add(transfer_message, new GridConstraints(13, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_currentBalanceLabel = new JLabel();
        transfer_currentBalanceLabel.setForeground(new Color(-1118482));
        transfer_currentBalanceLabel.setHorizontalAlignment(4);
        transfer_currentBalanceLabel.setText("Suma (wszystkie waluty):");
        transactionPanel.add(transfer_currentBalanceLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(201, 16), null, 0, false));
        transfer_currentBalance = new JLabel();
        Font transfer_currentBalanceFont = this.$$$getFont$$$("Arial Black", -1, 18, transfer_currentBalance.getFont());
        if (transfer_currentBalanceFont != null) transfer_currentBalance.setFont(transfer_currentBalanceFont);
        transfer_currentBalance.setForeground(new Color(-1118482));
        transfer_currentBalance.setText("0000000.00 PLN");
        transactionPanel.add(transfer_currentBalance, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(165, -1), null, 0, false));
        transfer_titleLabel = new JLabel();
        transfer_titleLabel.setForeground(new Color(-1118482));
        transfer_titleLabel.setText("Tytuł przelewu:");
        transactionPanel.add(transfer_titleLabel, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_title = new JTextField();
        transfer_title.setBackground(new Color(-14540254));
        transfer_title.setForeground(new Color(-33024));
        transactionPanel.add(transfer_title, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_accountSelectBox = new JComboBox();
        transfer_accountSelectBox.setBackground(new Color(-14540254));
        transfer_accountSelectBox.setEditable(false);
        transfer_accountSelectBox.setForeground(new Color(-33024));
        transactionPanel.add(transfer_accountSelectBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_fromLabel = new JLabel();
        transfer_fromLabel.setForeground(new Color(-1118482));
        transfer_fromLabel.setText("Z konta:");
        transactionPanel.add(transfer_fromLabel, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_payerBalance = new JLabel();
        Font transfer_payerBalanceFont = this.$$$getFont$$$("Arial Black", -1, 18, transfer_payerBalance.getFont());
        if (transfer_payerBalanceFont != null) transfer_payerBalance.setFont(transfer_payerBalanceFont);
        transfer_payerBalance.setForeground(new Color(-1118482));
        transfer_payerBalance.setText("");
        transactionPanel.add(transfer_payerBalance, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(165, -1), null, 0, false));
        transfer_currencyLabel = new JLabel();
        Font transfer_currencyLabelFont = this.$$$getFont$$$("Arial Black", -1, 18, transfer_currencyLabel.getFont());
        if (transfer_currencyLabelFont != null) transfer_currencyLabel.setFont(transfer_currencyLabelFont);
        transfer_currencyLabel.setForeground(new Color(-1118482));
        transfer_currencyLabel.setText("PLN");
        transactionPanel.add(transfer_currencyLabel, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(165, -1), null, 0, false));
        final Spacer spacer3 = new Spacer();
        transactionPanel.add(spacer3, new GridConstraints(0, 0, 15, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        transfer_accountNumber = new JTextField();
        transfer_accountNumber.setBackground(new Color(-14540254));
        transfer_accountNumber.setEditable(true);
        transfer_accountNumber.setForeground(new Color(-33024));
        transactionPanel.add(transfer_accountNumber, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_contactNameLabel = new JLabel();
        transfer_contactNameLabel.setForeground(new Color(-1118482));
        transfer_contactNameLabel.setText("Nazwa kontaktu:");
        transactionPanel.add(transfer_contactNameLabel, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_contactBox = new JComboBox();
        transfer_contactBox.setEditable(true);
        transfer_contactBox.setEnabled(true);
        transfer_contactBox.setForeground(new Color(-33024));
        transfer_contactBox.setOpaque(true);
        transfer_contactBox.setVerifyInputWhenFocusTarget(true);
        transfer_contactBox.setVisible(true);
        transactionPanel.add(transfer_contactBox, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_addContactButton = new JButton();
        transfer_addContactButton.setText("Dodaj kontakt");
        transactionPanel.add(transfer_addContactButton, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        transactionPanel.add(spacer4, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        transactionHistoryPanel = new JPanel();
        transactionHistoryPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        transactionHistoryPanel.setBackground(new Color(-14540254));
        tabbedPane.addTab("Historia przelewów", transactionHistoryPanel);
        historyPane = new JScrollPane();
        historyPane.setBackground(new Color(-14540254));
        historyPane.setForeground(new Color(-4473925));
        transactionHistoryPanel.add(historyPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        historyPanel = new JPanel();
        historyPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        historyPane.setViewportView(historyPanel);
        financialProductsPanel = new JPanel();
        financialProductsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        financialProductsPanel.setBackground(new Color(-14540254));
        financialProductsPanel.setForeground(new Color(-1118482));
        tabbedPane.addTab("Produkty finansowe", financialProductsPanel);
        financialProductsTabbedPane = new JTabbedPane();
        financialProductsTabbedPane.setBackground(new Color(-15166977));
        financialProductsTabbedPane.setForeground(new Color(-14540254));
        financialProductsTabbedPane.setOpaque(false);
        financialProductsPanel.add(financialProductsTabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        creditsPanel = new JPanel();
        creditsPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        creditsPanel.setBackground(new Color(-14540254));
        creditsPanel.setEnabled(false);
        creditsPanel.setForeground(new Color(-4473925));
        financialProductsTabbedPane.addTab("Kredyty", creditsPanel);
        creditsBalancePanel = new JPanel();
        creditsBalancePanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 0, 10, 0), -1, -1));
        creditsBalancePanel.setBackground(new Color(-14540254));
        creditsPanel.add(creditsBalancePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creditsBalance = new JLabel();
        Font creditsBalanceFont = this.$$$getFont$$$("Arial Black", -1, 18, creditsBalance.getFont());
        if (creditsBalanceFont != null) creditsBalance.setFont(creditsBalanceFont);
        creditsBalance.setText("0000000.00 PLN");
        creditsBalancePanel.add(creditsBalance, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creditsBalanceLabel = new JLabel();
        Font creditsBalanceLabelFont = this.$$$getFont$$$(null, -1, 20, creditsBalanceLabel.getFont());
        if (creditsBalanceLabelFont != null) creditsBalanceLabel.setFont(creditsBalanceLabelFont);
        creditsBalanceLabel.setText("Suma twoich kredytów do spłacenia:");
        creditsBalancePanel.add(creditsBalanceLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creditsSummaryPane = new JScrollPane();
        creditsPanel.add(creditsSummaryPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        creditsSummaryPanel = new JPanel();
        creditsSummaryPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        creditsSummaryPanel.setBackground(new Color(-14540254));
        creditsSummaryPane.setViewportView(creditsSummaryPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 5, 5));
        panel1.setBackground(new Color(-14540254));
        creditsSummaryPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 5, 5));
        panel2.setBackground(new Color(-14540254));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-14540254));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JButton button1 = new JButton();
        button1.setText("Weź nowy kredyt");
        panel3.add(button1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel3.add(spacer5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel3.add(spacer6, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel3.add(spacer7, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel3.add(spacer8, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        createCreditButton = new JButton();
        createCreditButton.setText("Weź kredyt");
        creditsPanel.add(createCreditButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        investmentsPanel = new JPanel();
        investmentsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        investmentsPanel.setBackground(new Color(-14540254));
        financialProductsTabbedPane.addTab("Lokaty", investmentsPanel);
        investmentsSummaryPane = new JScrollPane();
        investmentsPanel.add(investmentsSummaryPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        investmentsSummaryPanel = new JPanel();
        investmentsSummaryPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 5, 5));
        investmentsSummaryPanel.setBackground(new Color(-14540254));
        investmentsSummaryPane.setViewportView(investmentsSummaryPanel);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setBackground(new Color(-14540254));
        investmentsSummaryPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 5, 5));
        panel5.setBackground(new Color(-14540254));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setBackground(new Color(-14540254));
        panel4.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 75), null, 0, false));
        final JButton button2 = new JButton();
        button2.setText("Otwórz nową lokatę");
        panel6.add(button2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel6.add(spacer9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel6.add(spacer10, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel6.add(spacer11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        panel6.add(spacer12, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        createInvestmentButton = new JButton();
        createInvestmentButton.setText("Otwórz nową lokatę");
        investmentsPanel.add(createInvestmentButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contactsPanel = new JPanel();
        contactsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contactsPanel.setBackground(new Color(-14540254));
        tabbedPane.addTab("Kontakty", contactsPanel);
        contactsSummary = new JPanel();
        contactsSummary.setLayout(new GridBagLayout());
        contactsSummary.setBackground(new Color(-14540254));
        contactsPanel.add(contactsSummary, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setBackground(new Color(-14540254));
        contactsPanel.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayoutManager(5, 3, new Insets(10, 10, 10, 10), -1, -1));
        settingsPanel.setBackground(new Color(-14540254));
        tabbedPane.addTab("Ustawienia", settingsPanel);
        changeLoginButton = new JButton();
        changeLoginButton.setText("Zmień login");
        settingsPanel.add(changeLoginButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer13 = new Spacer();
        settingsPanel.add(spacer13, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer14 = new Spacer();
        settingsPanel.add(spacer14, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        changePasswordButton = new JButton();
        changePasswordButton.setText("Zmień hasło");
        settingsPanel.add(changePasswordButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer15 = new Spacer();
        settingsPanel.add(spacer15, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        loginPasswordLabel = new JLabel();
        loginPasswordLabel.setHorizontalAlignment(0);
        loginPasswordLabel.setHorizontalTextPosition(0);
        loginPasswordLabel.setText("Login i hasło");
        settingsPanel.add(loginPasswordLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loginField = new JTextField();
        loginField.setEditable(false);
        loginField.setHorizontalAlignment(0);
        settingsPanel.add(loginField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        idLabel = new JLabel();
        Font idLabelFont = this.$$$getFont$$$("Adagio_Slab", -1, 10, idLabel.getFont());
        if (idLabelFont != null) idLabel.setFont(idLabelFont);
        idLabel.setForeground(new Color(-1118482));
        idLabel.setText("Numer klienta: X");
        currentPanel.add(idLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        nameLabel = new JLabel();
        Font nameLabelFont = this.$$$getFont$$$("Adagio_Slab", Font.BOLD, 20, nameLabel.getFont());
        if (nameLabelFont != null) nameLabel.setFont(nameLabelFont);
        nameLabel.setForeground(new Color(-1118482));
        nameLabel.setText("witaj IMIĘ!");
        currentPanel.add(nameLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        transfer_valueLabel.setLabelFor(transfer_amount);
        transfer_titleLabel.setLabelFor(transfer_title);
        transfer_fromLabel.setLabelFor(transfer_accountSelectBox);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return currentPanel;
    }
}