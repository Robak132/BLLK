package com.bllk.Apka;

import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class StartWindow {
    static JFrame frame;
    static ClientServerConnection connection;
    static JPanel startingPanel;

    private JPanel mainPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JLabel login_mainErrorLabel;
    private JTabbedPane mainTabbedPane;
    private JLabel logoLabel;
    private JTextField register_login;
    private JPasswordField register_password, register_repeatPassword;
    private JTextField register_name, register_surname;
    private JComboBox<Integer> register_yearsComboBox, register_monthsComboBox, register_daysComboBox;
    private JTextField register_street, register_number, register_city, register_postcode;
    private JComboBox<String> register_countriesComboBox;
    private JButton register_button;
    private JRadioButton femaleRadioButton, maleRadioButton;
    private JLabel register_loginErrorLabel, register_passwordErrorLabel, register_repeatPasswordErrorLabel;
    private JLabel register_nameErrorLabel, register_surnameErrorLabel, register_genderErrorLabel;
    private JLabel register_address1ErrorLabel, register_address2ErrorLabel;
    private JLabel register_mainErrorLabel;
    private JLabel loginLabel, passwordLabel;
    private JLabel register_loginLabel, register_passwordLabel, register_repeatPasswordLabel;
    private JLabel register_nameLabel, register_surnameLabel, register_birthDateLabel, register_genderLabel;
    private JLabel register_yearLabel, register_monthLabel, register_dayLabel;
    private JLabel register_streetLabel, register_numberLabel, register_cityLabel, register_postcodeLabel, register_countryLabel;
    private JLabel register_loginHeaderLabel, register_personalHeaderLabel;
    private JPanel registerTab;

    private String login, password, repeatedPassword;
    private String name, surname, gender;
    private String street, buildingNumber, city, postcode, country;
    private Integer year, month, day;
    private boolean isDataValid;

    static final Integer passwordMinimumLength = 8, passwordMaximumLength = 30;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        connection = new ClientServerConnection();
        if (!connection.checkConnection())
            System.exit(-1);
        startingPanel = new StartWindow().mainPanel;
        frame.setContentPane(startingPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(1024, 720));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void performLogin() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        Client client = null;
        Login log = null;

        if (login.isEmpty() || password.isEmpty()) {
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Pole loginu i hasła nie może być puste.");
            return;
        }

        try {
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Logowanie...");
            String passwordSalt = connection.getSalt(login);
            String hashedPassword = BCrypt.hashpw(password, passwordSalt);

            client = connection.getClient(login, hashedPassword);
            log = new Login(client.getID(), login, hashedPassword);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            login_mainErrorLabel.setVisible(true);
            login_mainErrorLabel.setText("Błędny login lub hasło.");
        }

        frame.setContentPane(new MainUserPage(client, log).currentPanel);
        loginField.setText("");
        passwordField.setText("");
        login_mainErrorLabel.setText("");
        login_mainErrorLabel.setVisible(false);
    }

    private void performRegister() {
        if (areAllFieldsValid()) {
            register_mainErrorLabel.setVisible(false);
            String password_hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String date = year + "-" + month + "-" + day;
            connection.createClient(name, surname, date, gender, street, buildingNumber, city, postcode, country, login, password_hash);
            register_mainErrorLabel.setText("Konto zostało utworzone. Możesz się zalogować :)");
            register_mainErrorLabel.setForeground(Color.green);
        } else {
            register_mainErrorLabel.setForeground(Color.red);
            register_mainErrorLabel.setText("Formularz został błędnie uzupełniony.");
        }
        register_mainErrorLabel.setVisible(true);
    }

    private void changePassword() {
        JTextField login = new JTextField();
        JPasswordField newPassword = new JPasswordField(), newPasswordRepeat = new JPasswordField();

        Object[] message = {
                "Nazwa użytkownika:", login,
                "Nowe hasło:", newPassword,
                "Powtórz hasło", newPasswordRepeat
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Resetowanie hasła", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newPasswordString = String.valueOf(newPassword.getPassword());
            String newPasswordRepeatString = String.valueOf(newPasswordRepeat.getPassword());
            String typedLogin = login.getText();
            String hashedPassword;
            int passwordLength = newPasswordString.length(); //, login_length = typedLogin.length();

            if (typedLogin.isEmpty() || newPasswordString.isEmpty() || newPasswordRepeatString.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Pole loginu i hasła nie może być puste. Proszę spróbować ponownie.",
                        "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
            } else {
                if (!connection.checkLogin(typedLogin)) {
                    JOptionPane.showMessageDialog(frame,
                            "Nie istnieje konto o podanym loginie. Proszę spróbować ponownie.",
                            "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (passwordLength < passwordMinimumLength || passwordLength > passwordMaximumLength) {
                        JOptionPane.showMessageDialog(frame,
                                "Hasło powinno mieć długość pomiędzy " + passwordMinimumLength + ", a " + passwordMaximumLength + " znaków. Proszę spróbować ponownie.",
                                "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (newPasswordString.equals(newPasswordRepeatString)) {
                            hashedPassword = BCrypt.hashpw(newPasswordString, BCrypt.gensalt(12));
                            connection.updatePassword(login.getText(), hashedPassword);
                            JOptionPane.showMessageDialog(frame, "Zmiana hasła powiodła się.",
                                    "Sukces", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame,
                                    "Wprowadzone hasła są różne. Proszę spróbować ponownie.",
                                    "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    public StartWindow() {
        mainPanel.updateUI();
        updateFontsAndColors();
        makeErrorLabelsInvisible();

        loginButton.addActionListener(e -> performLogin());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        mainTabbedPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (register_yearsComboBox.getItemCount() == 0) {
                    LocalDateTime now = LocalDateTime.now();
                    year = now.getYear();
                    month = now.getMonthValue();
                    day = now.getDayOfMonth();
                    register_yearsComboBox.removeAllItems();
                    fillYearComboBox(LocalDateTime.now());
                }
                if (register_countriesComboBox.getItemCount() == 0) {
                    fillCountriesComboBox();
                }
            }
        });
        register_yearsComboBox.addActionListener(e -> {
            year = (Integer) register_yearsComboBox.getSelectedItem();
            if (year == null)
                year = LocalDateTime.now().getYear();
            register_monthsComboBox.removeAllItems();
            register_daysComboBox.removeAllItems();
            fillMonthComboBox(LocalDateTime.now(), year);
            register_monthsComboBox.setEnabled(true);
        });
        register_monthsComboBox.addActionListener(e -> {
            month = (Integer) register_monthsComboBox.getSelectedItem();
            if (month == null)
                month = 1;
            register_daysComboBox.removeAllItems();
            fillDaysComboBox(LocalDateTime.now(), year, month);
            register_daysComboBox.setEnabled(true);
        });
        register_daysComboBox.addActionListener(e -> day = (Integer) register_daysComboBox.getSelectedItem());

        // Data validation
        register_login.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                login = getDataFromField(register_login, register_loginErrorLabel, 6, 30);
                validateLogin();
            }
        });
        register_password.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                password = getPasswordFromField(register_password, register_passwordErrorLabel, passwordMinimumLength, passwordMaximumLength);
            }
        });
        register_repeatPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                repeatedPassword = getPasswordFromField(register_repeatPassword, register_repeatPasswordErrorLabel, passwordMinimumLength, passwordMaximumLength);
                validatePasswords();
            }
        });
        register_name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                name = getDataFromField(register_name, register_nameErrorLabel, 0, 60);
            }
        });
        register_surname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                surname = getDataFromField(register_surname, register_surnameErrorLabel, 0, 60);
            }
        });
        register_street.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                street = getDataFromField(register_street, register_address1ErrorLabel, 0, 60);
            }
        });
        register_number.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                buildingNumber = getDataFromField(register_number, register_address1ErrorLabel, 0, 10);
            }
        });
        register_city.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                city = getDataFromField(register_city, register_address2ErrorLabel, 0, 60);
            }
        });
        register_postcode.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                postcode = getDataFromField(register_postcode, register_address2ErrorLabel, 0, 10);
            }
        });
        register_button.addActionListener(e -> performRegister());
        forgotPasswordButton.addActionListener(e -> changePassword());
    }

    private void updateFontsAndColors() {
        Colors colors = new Colors();
        Fonts fonts = new Fonts();

        Font standardFont = Fonts.getStandardFont();
        Font headerFont = Fonts.getHeaderFont();
        Font logoFont = Fonts.getLogoFont();

        // Main elements
        logoLabel.setFont(logoFont);
        registerTab.setFont(standardFont);
        registerTab.setBackground(Colors.getDarkGrey());
        mainTabbedPane.setFont(standardFont);

        // Login page
        for (JLabel label : Arrays.asList(loginLabel, passwordLabel)) {
            label.setFont(standardFont);
            label.setForeground(Colors.getBrightTextColor());
        }
        for (JButton jButton : Arrays.asList(loginButton, forgotPasswordButton)) {
            jButton.setFont(standardFont);
        }

        //Register page
        for (JLabel jLabel : Arrays.asList(register_personalHeaderLabel, register_loginHeaderLabel)) {
            jLabel.setFont(headerFont);
            jLabel.setForeground(Colors.getBrightTextColor());
        }
        for (JLabel jLabel : Arrays.asList(register_loginLabel, register_passwordLabel, register_repeatPasswordLabel,
                register_nameLabel, register_surnameLabel, register_genderLabel, register_birthDateLabel,
                register_yearLabel, register_monthLabel, register_dayLabel, register_streetLabel, register_numberLabel,
                register_cityLabel, register_postcodeLabel, register_countryLabel)) {
            jLabel.setFont(standardFont);
            jLabel.setForeground(Colors.getBrightTextColor());
        }
        for (JRadioButton jRadioButton : Arrays.asList(maleRadioButton, femaleRadioButton)) {
            jRadioButton.setFont(standardFont);
            jRadioButton.setForeground(Colors.getBrightTextColor());
        }
        for (JComboBox<Integer> integerJComboBox : Arrays.asList(register_yearsComboBox, register_monthsComboBox, register_daysComboBox)) {
            integerJComboBox.setFont(standardFont);
            integerJComboBox.setForeground(Colors.getOrange());
            integerJComboBox.setBackground(Colors.getGrey());
        }
        register_countriesComboBox.setFont(standardFont);
        register_button.setFont(standardFont);

        String system_name = System.getProperty("os.name");
        if (!system_name.startsWith("Windows")) {
            mainTabbedPane.setForeground(Color.decode("#FF7F00"));
        }
    }

    private void fillYearComboBox(LocalDateTime now) {
        int currentYear = now.getYear();

        for (int i = currentYear; i >= currentYear - 120; --i)
            register_yearsComboBox.addItem(i);
    }

    private void fillMonthComboBox(LocalDateTime now, Integer chosenYear) {
        int monthCount = 12;

        if (now.getYear() == chosenYear)
            monthCount = now.getMonthValue();

        for (int i = 1; i <= monthCount; ++i)
            register_monthsComboBox.addItem(i);
    }

    private void fillDaysComboBox(LocalDateTime now, Integer chosenYear, Integer chosenMonth) {
        int dayCount = 31;

        switch (chosenMonth) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                break;
            case 2:
                if ((chosenYear % 4 == 0 && chosenYear % 100 != 0) || chosenYear % 400 == 0)
                    dayCount = 29;
                else
                    dayCount = 28;
                break;
            default:
                dayCount = 30;
        }

        if (now.getYear() == chosenYear && now.getMonthValue() == chosenMonth)
            dayCount = now.getDayOfMonth();

        for (int i = 1; i <= dayCount; ++i)
            register_daysComboBox.addItem(i);
    }

    private void fillCountriesComboBox() {
        for (Map.Entry<String, Integer> entry : connection.getCountries().entrySet())
            register_countriesComboBox.addItem(entry.getKey());
    }

    private void makeErrorLabelsInvisible() {
        login_mainErrorLabel.setVisible(false);
        register_loginErrorLabel.setVisible(false);
        register_passwordErrorLabel.setVisible(false);
        register_repeatPasswordErrorLabel.setVisible(false);
        register_nameErrorLabel.setVisible(false);
        register_surnameErrorLabel.setVisible(false);
        register_genderErrorLabel.setVisible(false);
        register_address1ErrorLabel.setVisible(false);
        register_address2ErrorLabel.setVisible(false);
        register_mainErrorLabel.setVisible(false);
    }

    private boolean areAllFieldsValid() {
        login = getDataFromField(register_login, register_loginErrorLabel, 8, 30);
        password = getPasswordFromField(register_password, register_passwordErrorLabel, 8, 30);
        repeatedPassword = getPasswordFromField(register_repeatPassword, register_repeatPasswordErrorLabel, 8, 30);
        name = getDataFromField(register_name, register_nameErrorLabel, 0, 60);
        surname = getDataFromField(register_surname, register_surnameErrorLabel, 0, 60);
        street = getDataFromField(register_street, register_address1ErrorLabel, 0, 60);
        buildingNumber = getDataFromField(register_number, register_address1ErrorLabel, 0, 10);
        city = getDataFromField(register_city, register_address2ErrorLabel, 0, 60);
        postcode = getDataFromField(register_postcode, register_address2ErrorLabel, 0, 10);
        validatePasswords();
        validateLogin();
        year = (Integer) register_yearsComboBox.getSelectedItem();
        month = (Integer) register_monthsComboBox.getSelectedItem();
        day = (Integer) register_daysComboBox.getSelectedItem();
        country = (String) register_countriesComboBox.getSelectedItem();
        gender = determineGender();
        if (login == null || password == null || repeatedPassword == null || name == null || surname == null || street == null ||
                buildingNumber == null || city == null || postcode == null || gender == null)
            isDataValid = false;
        return isDataValid;
    }

    private String getDataFromField(JTextField fieldToCheck, JLabel labelToModify, int minLength, int maxLength) {
        String inputData = fieldToCheck.getText();
        int input_length = inputData.length();
        boolean isValid;

        isValid = validateDataFromField(labelToModify, input_length, minLength, maxLength, "Pole");

        if (isValid) {
            isDataValid = true;
            return inputData;
        } else {
            isDataValid = false;
            return null;
        }
    }

    private String getPasswordFromField(JPasswordField passwordField, JLabel labelToModify, int minLength, int maxLength) {
        String inputData = String.valueOf(passwordField.getPassword());
        int inputLength = inputData.length();
        boolean is_valid;

        is_valid = validateDataFromField(labelToModify, inputLength, minLength, maxLength, "Hasło");

        if (is_valid) {
            isDataValid = true;
            return inputData;
        } else {
            isDataValid = false;
            return null;
        }
    }

    private boolean validateDataFromField(JLabel labelToModify, int length, int minLength, int maxLength, String dataName) {
        boolean isValid;
        if (length == 0) {
            isValid = false;
            labelToModify.setText(dataName + " nie może być puste.");
            labelToModify.setVisible(true);
        } else if (length < minLength || length > maxLength) {
            isValid = false;
            if (minLength == 0) {
                labelToModify.setText(dataName + " powinno mieć długość do " + maxLength + " znaków.");
            } else {
                labelToModify.setText(dataName + " powinno mieć długość pomiędzy " + minLength + ", a " + maxLength + " znaków.");
            }
            labelToModify.setVisible(true);
        } else {
            isValid = true;
            labelToModify.setVisible(false);
        }
        return isValid;
    }

    private void validateLogin() {
        if (login == null) {
            isDataValid = false;
            return;
        }
        if (connection.checkLogin(login)) {
            isDataValid = false;
            register_loginErrorLabel.setText("Podany login już istnieje w bazie.");
            register_loginErrorLabel.setVisible(true);
        } else {
            isDataValid = true;
            register_loginErrorLabel.setVisible(false);
        }
    }

    private void validatePasswords() {
        if (password == null || repeatedPassword == null) {
            isDataValid = false;
            return;
        }
        if (password.equals(repeatedPassword)) {
            register_repeatPasswordErrorLabel.setVisible(false);
            isDataValid = true;
        } else {
            register_repeatPasswordErrorLabel.setText("Hasła są różne.");
            register_repeatPasswordErrorLabel.setVisible(true);
            isDataValid = false;
        }
    }

    private String determineGender() {
        if (maleRadioButton.isSelected()) {
            register_genderErrorLabel.setVisible(false);
            return "M";
        } else if (femaleRadioButton.isSelected()) {
            register_genderErrorLabel.setVisible(false);
            return "F";
        } else {
            register_genderErrorLabel.setText("Należy wybrać jedną z opcji.");
            register_genderErrorLabel.setVisible(true);
            isDataValid = false;
            return null;
        }
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.setBackground(new Color(-14540254));
        mainPanel.setForeground(new Color(-14540254));
        mainPanel.setOpaque(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setBackground(new Color(-15166977));
        mainTabbedPane.setDoubleBuffered(false);
        mainTabbedPane.setForeground(new Color(-14540254));
        mainTabbedPane.setOpaque(false);
        mainPanel.add(mainTabbedPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 200), null, 0, false));
        mainTabbedPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-14540254));
        panel1.setDoubleBuffered(false);
        panel1.setForeground(new Color(-14540254));
        panel1.setOpaque(true);
        panel1.setPreferredSize(new Dimension(-1, -1));
        mainTabbedPane.addTab("Logowanie", panel1);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        loginField = new JTextField();
        loginField.setBackground(new Color(-8355712));
        loginField.setText("");
        panel1.add(loginField, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(-8355712));
        panel1.add(passwordField, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        loginLabel = new JLabel();
        loginLabel.setForeground(new Color(-1118482));
        loginLabel.setText("Login:");
        panel1.add(loginLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordLabel = new JLabel();
        passwordLabel.setForeground(new Color(-1118482));
        passwordLabel.setText("Hasło:");
        panel1.add(passwordLabel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 10, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 3, 10, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        loginButton = new JButton();
        loginButton.setText("Zaloguj się");
        panel1.add(loginButton, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), null, new Dimension(300, -1), 0, false));
        forgotPasswordButton = new JButton();
        forgotPasswordButton.setText("Zapomniałeś hasła?");
        panel1.add(forgotPasswordButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, -1), null, new Dimension(300, -1), 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel1.add(spacer5, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        login_mainErrorLabel = new JLabel();
        login_mainErrorLabel.setForeground(new Color(-65536));
        login_mainErrorLabel.setText("");
        login_mainErrorLabel.setVerifyInputWhenFocusTarget(true);
        login_mainErrorLabel.setVisible(true);
        panel1.add(login_mainErrorLabel, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel1.add(spacer6, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-14540254));
        scrollPane1.setForeground(new Color(-14540254));
        scrollPane1.setHorizontalScrollBarPolicy(30);
        scrollPane1.setOpaque(true);
        scrollPane1.setVerticalScrollBarPolicy(22);
        scrollPane1.setVisible(true);
        scrollPane1.setWheelScrollingEnabled(true);
        mainTabbedPane.addTab("Rejestracja", scrollPane1);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        registerTab = new JPanel();
        registerTab.setLayout(new GridLayoutManager(33, 8, new Insets(0, 0, 0, 0), -1, -1));
        registerTab.setBackground(new Color(-14540254));
        registerTab.setForeground(new Color(-14540254));
        registerTab.setInheritsPopupMenu(false);
        registerTab.setOpaque(true);
        scrollPane1.setViewportView(registerTab);
        register_login = new JTextField();
        register_login.setBackground(new Color(-8355712));
        register_login.setText("");
        registerTab.add(register_login, new GridConstraints(5, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_password = new JPasswordField();
        register_password.setBackground(new Color(-8355712));
        register_password.setText("");
        registerTab.add(register_password, new GridConstraints(8, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer8 = new Spacer();
        registerTab.add(spacer8, new GridConstraints(0, 7, 33, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        register_loginLabel = new JLabel();
        register_loginLabel.setForeground(new Color(-1118482));
        register_loginLabel.setText("Login");
        registerTab.add(register_loginLabel, new GridConstraints(3, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_passwordLabel = new JLabel();
        register_passwordLabel.setForeground(new Color(-1118482));
        register_passwordLabel.setText("Hasło");
        registerTab.add(register_passwordLabel, new GridConstraints(6, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        registerTab.add(spacer9, new GridConstraints(0, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        registerTab.add(spacer10, new GridConstraints(32, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        register_repeatPassword = new JPasswordField();
        register_repeatPassword.setBackground(new Color(-8355712));
        registerTab.add(register_repeatPassword, new GridConstraints(11, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_repeatPasswordLabel = new JLabel();
        register_repeatPasswordLabel.setForeground(new Color(-1118482));
        register_repeatPasswordLabel.setText("Powtórz hasło");
        registerTab.add(register_repeatPasswordLabel, new GridConstraints(9, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        registerTab.add(spacer11, new GridConstraints(1, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        register_name = new JTextField();
        register_name.setBackground(new Color(-8355712));
        registerTab.add(register_name, new GridConstraints(15, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_surname = new JTextField();
        register_surname.setBackground(new Color(-8355712));
        registerTab.add(register_surname, new GridConstraints(18, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_nameLabel = new JLabel();
        register_nameLabel.setForeground(new Color(-1118482));
        register_nameLabel.setText("Imię");
        registerTab.add(register_nameLabel, new GridConstraints(13, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_surnameLabel = new JLabel();
        register_surnameLabel.setForeground(new Color(-1118482));
        register_surnameLabel.setText("Nazwisko");
        registerTab.add(register_surnameLabel, new GridConstraints(16, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_birthDateLabel = new JLabel();
        register_birthDateLabel.setForeground(new Color(-1118482));
        register_birthDateLabel.setText("Data urodzenia");
        registerTab.add(register_birthDateLabel, new GridConstraints(22, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_yearsComboBox = new JComboBox();
        register_yearsComboBox.setBackground(new Color(-13421773));
        register_yearsComboBox.setEditable(false);
        register_yearsComboBox.setForeground(new Color(-33024));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        register_yearsComboBox.setModel(defaultComboBoxModel1);
        register_yearsComboBox.setToolTipText("");
        registerTab.add(register_yearsComboBox, new GridConstraints(23, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(78, 33), null, 0, false));
        register_monthsComboBox = new JComboBox();
        register_monthsComboBox.setBackground(new Color(-13421773));
        register_monthsComboBox.setEditable(false);
        register_monthsComboBox.setEnabled(false);
        register_monthsComboBox.setForeground(new Color(-33024));
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        register_monthsComboBox.setModel(defaultComboBoxModel2);
        registerTab.add(register_monthsComboBox, new GridConstraints(23, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(78, 33), null, 0, false));
        register_daysComboBox = new JComboBox();
        register_daysComboBox.setBackground(new Color(-13421773));
        register_daysComboBox.setEditable(false);
        register_daysComboBox.setEnabled(false);
        register_daysComboBox.setForeground(new Color(-33024));
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        register_daysComboBox.setModel(defaultComboBoxModel3);
        registerTab.add(register_daysComboBox, new GridConstraints(23, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(78, 33), null, 0, false));
        final Spacer spacer12 = new Spacer();
        registerTab.add(spacer12, new GridConstraints(0, 0, 33, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        register_yearLabel = new JLabel();
        register_yearLabel.setForeground(new Color(-1118482));
        register_yearLabel.setText("Rok");
        registerTab.add(register_yearLabel, new GridConstraints(23, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(24, 33), null, 0, false));
        register_monthLabel = new JLabel();
        register_monthLabel.setForeground(new Color(-1118482));
        register_monthLabel.setText("Miesiąc");
        registerTab.add(register_monthLabel, new GridConstraints(23, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 33), null, 0, false));
        register_dayLabel = new JLabel();
        register_dayLabel.setForeground(new Color(-1118482));
        register_dayLabel.setText("Dzień");
        registerTab.add(register_dayLabel, new GridConstraints(23, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, 33), null, 0, false));
        register_street = new JTextField();
        register_street.setBackground(new Color(-8355712));
        registerTab.add(register_street, new GridConstraints(26, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_streetLabel = new JLabel();
        register_streetLabel.setForeground(new Color(-1118482));
        register_streetLabel.setText("Ulica");
        registerTab.add(register_streetLabel, new GridConstraints(24, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_number = new JTextField();
        register_number.setBackground(new Color(-8355712));
        registerTab.add(register_number, new GridConstraints(26, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_numberLabel = new JLabel();
        register_numberLabel.setForeground(new Color(-1118482));
        register_numberLabel.setText("Numer budynku");
        registerTab.add(register_numberLabel, new GridConstraints(24, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_city = new JTextField();
        register_city.setBackground(new Color(-8355712));
        registerTab.add(register_city, new GridConstraints(29, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_loginHeaderLabel = new JLabel();
        Font register_loginHeaderLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, register_loginHeaderLabel.getFont());
        if (register_loginHeaderLabelFont != null) register_loginHeaderLabel.setFont(register_loginHeaderLabelFont);
        register_loginHeaderLabel.setForeground(new Color(-1118482));
        register_loginHeaderLabel.setText("Dane do logowania");
        registerTab.add(register_loginHeaderLabel, new GridConstraints(2, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_personalHeaderLabel = new JLabel();
        Font register_personalHeaderLabelFont = this.$$$getFont$$$(null, Font.BOLD, 16, register_personalHeaderLabel.getFont());
        if (register_personalHeaderLabelFont != null)
            register_personalHeaderLabel.setFont(register_personalHeaderLabelFont);
        register_personalHeaderLabel.setForeground(new Color(-1118482));
        register_personalHeaderLabel.setText("Dane osobiste");
        registerTab.add(register_personalHeaderLabel, new GridConstraints(12, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_cityLabel = new JLabel();
        register_cityLabel.setForeground(new Color(-1118482));
        register_cityLabel.setText("Miasto");
        registerTab.add(register_cityLabel, new GridConstraints(27, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_postcode = new JTextField();
        register_postcode.setBackground(new Color(-8355712));
        registerTab.add(register_postcode, new GridConstraints(29, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        register_postcodeLabel = new JLabel();
        register_postcodeLabel.setForeground(new Color(-1118482));
        register_postcodeLabel.setText("Kod pocztowy");
        registerTab.add(register_postcodeLabel, new GridConstraints(27, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_countriesComboBox = new JComboBox();
        register_countriesComboBox.setBackground(new Color(-13421773));
        register_countriesComboBox.setForeground(new Color(-33024));
        registerTab.add(register_countriesComboBox, new GridConstraints(29, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_countryLabel = new JLabel();
        register_countryLabel.setForeground(new Color(-1118482));
        register_countryLabel.setText("Kraj");
        registerTab.add(register_countryLabel, new GridConstraints(27, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_button = new JButton();
        register_button.setText("Zarejestruj się");
        registerTab.add(register_button, new GridConstraints(30, 4, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        maleRadioButton = new JRadioButton();
        maleRadioButton.setBackground(new Color(-14540254));
        maleRadioButton.setForeground(new Color(-1118482));
        maleRadioButton.setText("Mężczyzna");
        registerTab.add(maleRadioButton, new GridConstraints(21, 4, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        femaleRadioButton = new JRadioButton();
        femaleRadioButton.setBackground(new Color(-14540254));
        femaleRadioButton.setForeground(new Color(-1118482));
        femaleRadioButton.setText("Kobieta");
        femaleRadioButton.setVerifyInputWhenFocusTarget(true);
        registerTab.add(femaleRadioButton, new GridConstraints(21, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_genderLabel = new JLabel();
        register_genderLabel.setForeground(new Color(-1118482));
        register_genderLabel.setText("Płeć");
        registerTab.add(register_genderLabel, new GridConstraints(19, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_address2ErrorLabel = new JLabel();
        Font register_address2ErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_address2ErrorLabel.getFont());
        if (register_address2ErrorLabelFont != null)
            register_address2ErrorLabel.setFont(register_address2ErrorLabelFont);
        register_address2ErrorLabel.setForeground(new Color(-65536));
        register_address2ErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_address2ErrorLabel, new GridConstraints(28, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_mainErrorLabel = new JLabel();
        Font register_mainErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_mainErrorLabel.getFont());
        if (register_mainErrorLabelFont != null) register_mainErrorLabel.setFont(register_mainErrorLabelFont);
        register_mainErrorLabel.setForeground(new Color(-65536));
        register_mainErrorLabel.setHorizontalAlignment(0);
        register_mainErrorLabel.setHorizontalTextPosition(0);
        register_mainErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_mainErrorLabel, new GridConstraints(31, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_loginErrorLabel = new JLabel();
        Font register_loginErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_loginErrorLabel.getFont());
        if (register_loginErrorLabelFont != null) register_loginErrorLabel.setFont(register_loginErrorLabelFont);
        register_loginErrorLabel.setForeground(new Color(-65536));
        register_loginErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_loginErrorLabel, new GridConstraints(4, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_passwordErrorLabel = new JLabel();
        Font register_passwordErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_passwordErrorLabel.getFont());
        if (register_passwordErrorLabelFont != null)
            register_passwordErrorLabel.setFont(register_passwordErrorLabelFont);
        register_passwordErrorLabel.setForeground(new Color(-65536));
        register_passwordErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_passwordErrorLabel, new GridConstraints(7, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_repeatPasswordErrorLabel = new JLabel();
        Font register_repeatPasswordErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_repeatPasswordErrorLabel.getFont());
        if (register_repeatPasswordErrorLabelFont != null)
            register_repeatPasswordErrorLabel.setFont(register_repeatPasswordErrorLabelFont);
        register_repeatPasswordErrorLabel.setForeground(new Color(-65536));
        register_repeatPasswordErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_repeatPasswordErrorLabel, new GridConstraints(10, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_address1ErrorLabel = new JLabel();
        Font register_address1ErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_address1ErrorLabel.getFont());
        if (register_address1ErrorLabelFont != null)
            register_address1ErrorLabel.setFont(register_address1ErrorLabelFont);
        register_address1ErrorLabel.setForeground(new Color(-65536));
        register_address1ErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_address1ErrorLabel, new GridConstraints(25, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_nameErrorLabel = new JLabel();
        Font register_nameErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_nameErrorLabel.getFont());
        if (register_nameErrorLabelFont != null) register_nameErrorLabel.setFont(register_nameErrorLabelFont);
        register_nameErrorLabel.setForeground(new Color(-65536));
        register_nameErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_nameErrorLabel, new GridConstraints(14, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_surnameErrorLabel = new JLabel();
        Font register_surnameErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_surnameErrorLabel.getFont());
        if (register_surnameErrorLabelFont != null) register_surnameErrorLabel.setFont(register_surnameErrorLabelFont);
        register_surnameErrorLabel.setForeground(new Color(-65536));
        register_surnameErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_surnameErrorLabel, new GridConstraints(17, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_genderErrorLabel = new JLabel();
        Font register_genderErrorLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, register_genderErrorLabel.getFont());
        if (register_genderErrorLabelFont != null) register_genderErrorLabel.setFont(register_genderErrorLabelFont);
        register_genderErrorLabel.setForeground(new Color(-65536));
        register_genderErrorLabel.setText("Komunikat o błędzie");
        registerTab.add(register_genderErrorLabel, new GridConstraints(20, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoLabel = new JLabel();
        Font logoLabelFont = this.$$$getFont$$$("Radikal WUT", -1, 48, logoLabel.getFont());
        if (logoLabelFont != null) logoLabel.setFont(logoLabelFont);
        logoLabel.setForeground(new Color(-33024));
        logoLabel.setText("BLLK");
        mainPanel.add(logoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        register_loginLabel.setLabelFor(register_login);
        register_passwordLabel.setLabelFor(register_password);
        register_repeatPasswordLabel.setLabelFor(register_repeatPassword);
        register_nameLabel.setLabelFor(register_name);
        register_surnameLabel.setLabelFor(register_surname);
        register_birthDateLabel.setLabelFor(register_yearsComboBox);
        register_monthLabel.setLabelFor(register_monthsComboBox);
        register_dayLabel.setLabelFor(register_daysComboBox);
        register_streetLabel.setLabelFor(register_street);
        register_numberLabel.setLabelFor(register_number);
        register_cityLabel.setLabelFor(register_city);
        register_postcodeLabel.setLabelFor(register_postcode);
        register_loginErrorLabel.setLabelFor(register_login);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(femaleRadioButton);
        buttonGroup.add(maleRadioButton);
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
        return mainPanel;
    }
}
