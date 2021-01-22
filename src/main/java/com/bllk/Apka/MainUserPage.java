package com.bllk.Apka;

import com.bllk.Apka.customComponents.*;
import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    Map <String, Integer> contacts;
    private static Map <Integer, JSONObject> accounts;
    private static Map <String, String> currencies;
    boolean lock_combobox = false;

    public MainUserPage(Client _client, Login _login) {
        client = _client;
        login = _login;
        nameLabel.setText("Witaj " + client.getName() + "!");
        idLabel.setText("Numer klienta: " + client.getID());
        loginField.setText(login.getLogin());
        currencies = connection.getCurrencies();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        accountsSummaryPanel.setLayout(new GridBagLayout());
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        contactsSummary.setLayout(new BoxLayout(contactsSummary, BoxLayout.Y_AXIS));
        investmentsSummaryPanel.setLayout(new GridBagLayout());
        creditsSummaryPanel.setLayout(new GridBagLayout());

        updateFontsAndColors();
        updateAll();
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
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateAll();
            }
        });
    }

    void createAccountDialog() {
        JComboBox<String> currenciesComboBox = new JComboBox<>();

        for (Map.Entry<String, String> currency: currencies.entrySet()) {
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
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
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
        slidersLabelTable.put(2, new JLabel("0%") );
        slidersLabelTable.put(22, new JLabel("2%") );
        slidersLabelTable.put(42, new JLabel("4%") );
        slidersLabelTable.put(62, new JLabel("6%") );
        slidersLabelTable.put(82, new JLabel("8%") );
        slidersLabelTable.put(100, new JLabel("10%") );

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
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    long integerValue = Math.round(Double.parseDouble(value.getText().replace(",",".")) * 100);
                    int integerCapPeroid = Integer.parseInt(capPeroid.getText());

                    if (accounts.get(accountsToSelect.get(accountBox.getSelectedIndex())).getInt("value") < integerValue)
                        JOptionPane.showMessageDialog(null,"Nie posiadasz tyle pieniędzy.", "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                    else {
                        connection.createInvestment(login.getLogin(), login.getPasswordHash(), name.getText(),
                                integerValue, profitRate.getValue() / 1000.0, yearProfitRate.getValue() / 1000.0,
                                integerCapPeroid, accountsToSelect.get(accountBox.getSelectedIndex()));
                        updateInvestmentsSummary();
                        updateAccounts();
                        JOptionPane.showMessageDialog(null,"Operacja powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,"Błędna wartość.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
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
            JSlider slider = (JSlider)e.getSource();
            interestValue.setText("Oprocentowanie: " + String.format("%.1f", slider.getValue() / 10f) + "%");
            };
        ChangeListener commissionChangeListener = e -> {
            JSlider slider = (JSlider)e.getSource();
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
        slidersLabelTable.put(2, new JLabel("0%") );
        slidersLabelTable.put(22, new JLabel("2%") );
        slidersLabelTable.put(42, new JLabel("4%") );
        slidersLabelTable.put(62, new JLabel("6%") );
        slidersLabelTable.put(82, new JLabel("8%") );
        slidersLabelTable.put(100, new JLabel("10%") );

        commission.setLabelTable( slidersLabelTable );
        commission.setPaintLabels(true);
        interestRate.setLabelTable( slidersLabelTable );
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
            class WrongCreditNameException extends Exception
            { public WrongCreditNameException() {} }
            class WrongCreditAmountException extends Exception
            { public WrongCreditAmountException() {} }

            try {
                if (name.getText().length() < 1 || name.getText().length() > 100)
                    throw new WrongCreditNameException();
                long amount = Math.round(Double.parseDouble(value.getText().replace(",", ".")) * 100);
                if (amount < 10000 || amount > 100000000)
                    throw new WrongCreditAmountException();
                connection.createCredit(
                        login.getLogin(),
                        login.getPasswordHash(),
                        name.getText(),
                        amount,
                        interestRate.getValue() / 1000.0,
                        commission.getValue() / 1000.0,
                        Integer.parseInt((String) months.getValue()),
                        accountsToSelect.get(accountBox.getSelectedIndex())
                );
                updateCreditsSummary();
                updateAccounts();
                JOptionPane.showMessageDialog(null, "Operacja powiodła się.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,"Błędna wartość liczbowa jednego z parametrów.\nSprawdź, czy wprowadziłeś kwotę kredytu.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } catch (WrongCreditNameException e) {
                JOptionPane.showMessageDialog(null,"Błąd w nazwie kredytu.\nNazwa powinna zawierać od 1 do 100 znaków.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } catch (WrongCreditAmountException e) {
                JOptionPane.showMessageDialog(null,"Błąd kwoty kredytu.\nKwota kredytu powinna zawierać się od 100 do 1 000 000.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            }
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
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (connection.checkLogin(new_name_string))
                JOptionPane.showMessageDialog(null,"Login jest zajęty.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                if (connection.updateLogin(login.getLogin(), login.getPasswordHash(), new_name_string)) {
                    JOptionPane.showMessageDialog(null,"Zmiana loginu powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
                    loginField.setText(new_name_string);
                }
                else
                    JOptionPane.showMessageDialog(null,"Serwer odrzucił żądanie","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(null,"Pole nie może być puste.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (passwordLength < StartWindow.passwordMinimumLength || passwordLength > StartWindow.passwordMaximumLength)
                JOptionPane.showMessageDialog(null,"Hasło musi mieć od 8 do 16 znaków.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else if (!newPasswordString.equals(newPasswordRepeatString))
                JOptionPane.showMessageDialog(null,"Hasła nie są identyczne.","Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            else {
                String hashedPassword = BCrypt.hashpw(newPasswordString, BCrypt.gensalt(12));
                connection.updatePassword(login.getLogin(), hashedPassword);
                JOptionPane.showMessageDialog(null,"Zmiana hasła powiodła się.","Sukces", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    boolean currencyChangeWarning() {
        int n = JOptionPane.showConfirmDialog(
                frame,
                "Konto, na które zamierzasz wysłać przelew, zawiera inną walutę niż wysyłana, czy chcesz przewalutować?",
                "Przewalutowanie",
                JOptionPane.YES_NO_OPTION);
        return n==0;
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

        } catch(NumberFormatException ex) {
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
            }
            else {
                int payerID = activePayerAccount.getID();
                int targetID = Integer.parseInt(transfer_accountNumber.getText());
                int currencyID = activePayerAccount.getCurrencyID();
                long moneyValue = Math.round(Double.parseDouble(transfer_amount.getText().replace(",", ".")) * 100.0);
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
        }
        catch (Exception ex) {
            transfer_message.setText("Błąd transakcji: " + ex.getMessage());
        }
    }

    public String getContactIfPossible(int value) {
        for (Map.Entry<String, Integer> contact:contacts.entrySet())
            if (contact.getValue() == value)
                return contact.getKey();
        return String.valueOf(value);
    }

    /** Updates everything. */
    private void updateAll() {
        updateContacts();
        updateAccounts();
        updateContactsSummary();
        updateCreditsBalance();
        updateInvestmentsSummary();
        updateCreditsSummary();
    }
    /** Updates fonts of labels and sets backgrounds of fields. */
    private void updateFontsAndColors() {
        Font standardFont = Fonts.getStandardFont();
        Font headerFont = Fonts.getHeaderFont();
        Font logoFont = Fonts.getLogoFont();


        // Main elements
        logoLabel.setFont(logoFont);
        logoLabel.setForeground(Colors.getOrange());

        nameLabel.setFont(headerFont);

        logOutButton.setFont(standardFont);

        creditsSummaryPane.setBackground(Colors.getDarkGrey());
        investmentsSummaryPane.setBackground(Colors.getDarkGrey());

        for (JLabel jLabel : Arrays.asList(idLabel, nameLabel)) {
            jLabel.setForeground(Colors.getBrightTextColor());
        }

        for (JTabbedPane jTabbedPane : Arrays.asList(tabbedPane, financialProductsTabbedPane)) {
            jTabbedPane.setFont(standardFont);
            jTabbedPane.setForeground(Colors.getDarkGrey());
            jTabbedPane.setBackground(Colors.getBlue());
        }

        for (JPanel jPanel : Arrays.asList(accountsPanel, transactionPanel, transactionHistoryPanel,
                financialProductsPanel, contactsPanel, settingsPanel, investmentsPanel, creditsPanel, historyPanel,
                creditsBalancePanel)) {
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
    /** Updates contacts in transactions combobox.
     *  Runs updateTransactionTable()
     */
    public void updateContacts() {
        lock_combobox = true;
        contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());
        String temp = (String) transfer_contactBox.getSelectedItem();
        transfer_contactBox.removeAllItems();
        transfer_contactBox.addItem("");
        for (Map.Entry<String, Integer> contact: contacts.entrySet())
            transfer_contactBox.addItem(contact.getKey());
        transfer_contactBox.setSelectedItem(temp);
        updateTransactionTable();
        lock_combobox = false;
    }
    /** Updates transaction table. */
    private void updateTransactionTable() {
        historyPanel.removeAll();
        //String[] columns = new String[] {"Od", "Do", "Data", "Tytuł", "Wartość", "Waluta"};
        Map<Integer, JSONObject> transactions = connection.getTransactions(login.getLogin(), login.getPasswordHash());
        char type = 0;

        for (JSONObject transaction: transactions.values()) {
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
    /** Updates money header in transaction screen. */
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
    /** Updates accounts in transaction combobox.
     *  Runs updateMoney() and updateAccountsSummary()
     */
    public void updateAccounts() {
        transfer_accountSelectBox.removeAllItems();
        accounts = connection.getUserAccounts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            transfer_accountSelectBox.addItem(String.format("%s (%s)",
                    getContactIfPossible(account.getKey()),
                    currencies.get(account.getValue().getString("currencyid")))
            );
            accountBoxUnformatted.add(account.getKey());
            user_currencies.add(account.getValue().getInt("currencyid"));
        }

        tabbedPane.setEnabledAt(1, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(2, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(3, transfer_accountSelectBox.getItemCount() != 0);
        tabbedPane.setEnabledAt(4, transfer_accountSelectBox.getItemCount() != 0);
        updateMoney();
        updateAccountsSummary();
    }
    public void updateAccountsSummary() {
        accountsSummaryPanel.removeAll();
        int column = 0, row = 1, counter = 1, accountsCount = accounts.size();
        boolean canBeDeleted;

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.weighty = 0.1;
        c.gridwidth = 1;

        c.gridy = 0;
        if (accountsCount != 0) {
            for (int i = 0; i < 6; i++) {
                JLabel nothing = new JLabel("");
                nothing.setMaximumSize(new Dimension(1, 1));
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

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;


        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());
        List<String> currencies_used = new ArrayList<>();

        for (Map.Entry<Integer, JSONObject> credit : credits.entrySet()) {
            JSONObject values = credit.getValue();
            currencies_used.add(values.getString("currencyid"));
        }
        for (Map.Entry<Integer, JSONObject> investment : investments.entrySet()) {
            JSONObject values = investment.getValue();
            currencies_used.add(values.getString("currencyid"));
        }

        c.gridwidth = 2;
        for (Map.Entry<Integer, JSONObject> account: accounts.entrySet()) {
            String currencyID = account.getValue().getString("currencyid");
            String currencyName = currencies.get(currencyID);
            int balance = account.getValue().getInt("value");
            String formattedBalance = String.format("%.2f", balance/100.0);

            canBeDeleted = !currencies_used.contains(currencyID);

            AccountPanel accountPanel = new AccountPanel(getContactIfPossible(account.getKey()),
                    "" + account.getKey(), formattedBalance, currencyName, this, canBeDeleted);

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
        accountsSummaryPanel.updateUI();
    }
    private void updateContactsSummary() {
        contactsSummary.removeAll();
        Map<String, Integer> contacts = connection.getContacts(login.getLogin(), login.getPasswordHash());

        for (Map.Entry<String, Integer> contact: contacts.entrySet()) {
            if (!accounts.containsKey(contact.getValue())) {
                ContactPanel contactPanel = new ContactPanel(contactsSummary, this, contact.getValue(), contact.getKey());
                contactsSummary.add(contactPanel);
                contactsSummary.add(new JSeparator());
            }
        }
    }
    public void updateInvestmentsSummary() {
        Map<Integer, JSONObject> investments = connection.getInvestments(login.getLogin(), login.getPasswordHash());
        updateFinancialProductSummary(investmentsSummaryPanel, investments, (char) 0);
    }
    public void updateCreditsSummary() {
        Map<Integer, JSONObject> credits = connection.getCredits(login.getLogin(), login.getPasswordHash());

        updateFinancialProductSummary(creditsSummaryPanel, credits, (char) 1);
        updateCreditsBalance();
    }
    private void updateFinancialProductSummary(JPanel panelToUpdate, Map<Integer, JSONObject> mapOfProducts, char productType) {
        int column = 0, row = 1, counter = 1, productsCount = mapOfProducts.size();
        panelToUpdate.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0.1;
        c.gridwidth = 1;

        c.gridy = 0;

        if (productsCount != 0) {
            for (int i = 0; i < 4; i++) {
                JLabel nothing = new JLabel("");
                nothing.setMaximumSize(new Dimension(1, 1));
                c.gridx = i;
                panelToUpdate.add(nothing, c);
            }
        } else {
            JLabel noAccountInformation;
            c.gridx = 0;
            if (productType == 0) {
                noAccountInformation = new JLabel("Nie masz żadnej lokaty. Możesz dodać ją poniżej.");
            } else if (productType == 1) {
                noAccountInformation = new JLabel("Nie masz żadnego kredytu. Możesz dodać go poniżej.");
            } else {
                noAccountInformation = new JLabel("Nie masz żadnego produktu. Możesz dodać go poniżej.");
            }
            noAccountInformation.setFont(Fonts.getStandardFont());
            noAccountInformation.setForeground(Colors.getBrightTextColor());
            panelToUpdate.add(noAccountInformation, c);
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.gridwidth = 2;


        for (Map.Entry<Integer, JSONObject> product: mapOfProducts.entrySet()) {
            c.gridx = column % 2;
            c.gridy = row;

            JPanel productPanel;

            if (productType == 0) {
                productPanel = new InvestmentPanel(this, product.getKey(), product.getValue());
            } else if (productType == 1) {
                productPanel = new CreditPanel(this, product.getKey(), product.getValue());
            } else {
                productPanel = new JPanel();
                productPanel.add(new JLabel("Błąd."));
            }

            if (counter == productsCount) {
                if (productsCount % 2 == 1) column = 1;
            }

            counter++;
            c.gridx = column;
            c.gridy = row;
            column += 2;
            if (column % 4 == 0) {
                column = 0;
                row++;
            }
            panelToUpdate.add(productPanel, c);
        }
        panelToUpdate.updateUI();
    }
    private void updateCreditsBalance() {
        if (transfer_accountSelectBox.getItemCount() > 0) {
            activePayerAccount = connection.getAccount(login.getLogin(), login.getPasswordHash(), accountBoxUnformatted.get(transfer_accountSelectBox.getSelectedIndex()));
            String active_currency_shortcut = currencies.get("" + activePayerAccount.getCurrencyID());
            long credits_total = connection.getTotalCredits(login.getLogin(), login.getPasswordHash(), activePayerAccount.getCurrencyID());

            creditsBalance.setText(String.format("%.2f %s", credits_total / 100.0, active_currency_shortcut));
        }
    }

    public static ClientServerConnection getConnection() {
        return connection;
    }
    public static Login getLogin() {
        return login;
    }
    public static Map <String, String> getCurrencies() {
        return currencies;
    }
    public static Map <Integer, JSONObject> getAccounts() {
        return accounts;
    }
}