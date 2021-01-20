package com.bllk.Apka;

import com.bllk.Apka.resourceHandlers.Colors;
import com.bllk.Apka.resourceHandlers.Fonts;
import com.bllk.Servlet.mapclasses.Client;
import com.bllk.Servlet.mapclasses.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private JScrollPane registerScrollPanel;

    private String login, password, repeatedPassword;
    private String name, surname, gender;
    private String street, buildingNumber, city, postcode, country;
    private Integer year, month, day;
    private boolean isDataValid;

    static final Integer passwordMinimumLength = 8, passwordMaximumLength = 30;

    public static void main(String[] args) {
        frame = new JFrame("BLLK");
        connection = new ClientServerConnection();
        if (!connection.checkConnection()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Server is not responding.\nThe app is going to shut down.",
                    "Cannot connect to the server",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
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
        }
        catch (Exception ex) {
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
        new Fonts();
        new Colors();
        updateFontsAndColors();
        makeErrorLabelsInvisible();

        loginButton.addActionListener(e -> performLogin());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        loginField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        mainTabbedPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (mainTabbedPane.getSelectedIndex() == 1) {
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

        switch(chosenMonth) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
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
        for (Map.Entry<String,Integer> entry : connection.getCountries().entrySet())
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
}
