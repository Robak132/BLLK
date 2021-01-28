package com.bllk.Servlet;

import com.bllk.Mapclasses.*;
import com.google.common.base.CharMatcher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletConfig;
import javax.servlet.http.*;

import java.util.List;

public class Server extends HttpServlet {
    private Database data;

    public void init(ServletConfig config) {
        data = new Database();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String requestUrl = request.getRequestURI();
        String[] attributes = requestUrl.substring(1).split("/");
        Client client;
        StringBuilder json;

        switch (attributes.length) {
            case 1:
                switch (attributes[0]) {
                    case "countries":
                        List countries = data.getCountries();
                        if (countries != null) {
                            json = new StringBuilder("{\n");
                            for (Object country : countries) {
                                json.append("\"");
                                json.append(((Country) country).getName());
                                json.append("\": \"");
                                json.append(((Country) country).getId());
                                json.append("\",\n");
                            }
                            json.append("}");
                            response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                        } else
                            response.getOutputStream().println("{}");
                        break;
                    case "currencies":
                        List currencies = data.getCurrencies();
                        if (currencies != null) {
                            json = new StringBuilder("{\n");
                            for (Object currency : currencies) {
                                json.append("\"");
                                json.append(((Currency) currency).getID());
                                json.append("\": \"");
                                json.append(((Currency) currency).getShortcut());
                                json.append("\",\n");
                            }
                            json.append("}");
                            response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                        } else
                            response.getOutputStream().println("{}");
                        break;
                    case "login":
                        String login = request.getParameter("login");
                        String password = request.getParameter("password");

                        Login loginClass = data.getLogin(login, password);
                        if (login != null) {
                            client = data.getClient(loginClass.getID());
                            json = new StringBuilder("{\n");
                            json.append("\"id\": \"").append(client.getID()).append("\",\n");
                            json.append("\"name\": \"").append(client.getName()).append("\",\n");
                            json.append("\"surname\": \"").append(client.getSurname()).append("\",\n");
                            json.append("\"birthdate\": \"").append(client.getBirthDate().toString()).append("\",\n");
                            json.append("\"addressid\": \"").append(client.getAddressID()).append("\",\n");
                            json.append("\"loginid\": \"").append(client.getLoginID()).append("\",\n");
                            json.append("\"gender\": \"").append(client.getGender()).append("\"\n");
                            json.append("}");
                            response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                        } else {
                            response.getOutputStream().println("{}");
                        }
                        break;
                }
            break;
            case 2:
                if (attributes[0].equals("account")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int accountID = Integer.parseInt(attributes[1]);

                    if (login == null || password == null) {
                        Account account = data.getAccount(accountID);
                        if (account != null) {
                            json = new StringBuilder("{\n");
                            json.append("\"id\": \"").append(account.getID()).append("\",\n");
                            json.append("\"currency\": \"").append(account.getCurrencyID()).append("\"\n");
                            json.append("}");
                            response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                        }
                        else
                            response.getOutputStream().println("{}");
                    }
                    else {
                        Login log = data.getLogin(login, password);
                        if (log != null) {
                            Account account = data.getAccount(accountID);
                            if (account != null) {
                                json = new StringBuilder("{\n");
                                json.append("\"id\": \"").append(account.getID()).append("\",\n");
                                json.append("\"value\": \"").append(account.getValue()).append("\",\n");
                                json.append("\"currency\": \"").append(account.getCurrencyID()).append("\",\n");
                                json.append("\"ownerid\": \"").append(account.getOwnerID()).append("\"\n");
                                json.append("}");
                                response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                            }
                            else
                                response.getOutputStream().println("{}");
                        }
                        else
                            response.getOutputStream().println("{}");
                    }
                }
                else if (attributes[0].equals("getsalt")) {
                    String salt = data.getSalt(attributes[1]);
                    if (salt != null)
                        response.getOutputStream().println("{\n\"salt\": \"" + salt + "\"\n}");
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("checklogin")) {
                    boolean does_exist = data.checkLogin(attributes[1]);
                    if (does_exist)
                        response.getOutputStream().println("{\n\"bool\": \"true\"\n}");
                    else
                        response.getOutputStream().println("{\n\"bool\": \"false\"\n}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("accounts")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List accounts = data.getAccounts(login, password);
                    if (accounts != null) {
                        json = new StringBuilder("{\n");
                        int i = 1;
                        for (Object account: accounts) {
                            json.append("\"").append(((Account) account).getID()).append("\": {\n");
                            json.append("\t\"value\": \"").append(((Account) account).getValue()).append("\",\n");
                            json.append("\t\"currencyid\": \"").append((((Account) account)).getCurrencyID()).append("\",\n");
                            if (i == accounts.size())
                                json.append("}\n");
                            else
                                json.append("},\n");
                            i++;
                        }
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("contacts")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List contacts = data.getContacts(login, password);
                    if (contacts != null) {
                        int i = 1;
                        json = new StringBuilder("{\n");
                        for (Object contact: contacts) {
                            json.append("\"").append(((Contact) contact).getName()).append("\": \"").append(((Contact) contact).getTargetID()).append("\"");
                            if (i == contacts.size())
                                json.append("\n");
                            else
                                json.append(",\n");
                            i++;
                        }
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("investments")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List investments = data.getInvestments(login, password);
                    if (investments != null) {
                        int i = 1;
                        json = new StringBuilder("{\n");
                        for (Object inv: investments) {
                            Investment investment = (Investment) inv;
                            json.append("\"").append(investment.getID()).append("\": {\n");
                            json.append("\t\"ownerid\": \"").append(investment.getOwnerID()).append("\",\n");
                            json.append("\t\"name\": \"").append(investment.getName()).append("\",\n");
                            json.append("\t\"value\": \"").append(investment.getValue()).append("\",\n");
                            json.append("\t\"profit\": \"").append(investment.getProfit()).append("\",\n");
                            json.append("\t\"yearprofit\": \"").append(investment.getYearProfit()).append("\",\n");
                            json.append("\t\"capperiod\": \"").append(investment.getCapPeriod()).append("\",\n");
                            json.append("\t\"datecreated\": \"").append(investment.getDateCreatedFormatted()).append("\",\n");
                            if (investment.getDateEnd() != null)
                                json.append("\t\"dateended\": \"").append(investment.getDateEndedFormatted()).append("\",\n");
                            json.append("\t\"currencyid\": \"").append(investment.getCurrencyID()).append("\"\n");
                            if (i == investments.size())
                                json.append("}\n");
                            else
                                json.append("},\n");
                            i++;
                        }
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("credits")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List credits = data.getCredits(login, password);
                    if (credits != null) {
                        int i = 1;
                        json = new StringBuilder("{\n");
                        for (Object inv: credits) {
                            Credit credit = (Credit) inv;
                            json.append("\"").append(credit.getID()).append("\": {\n");
                            json.append("\t\"ownerid\": \"").append(credit.getOwnerid()).append("\",\n");
                            json.append("\t\"name\": \"").append(credit.getName()).append("\",\n");
                            json.append("\t\"value\": \"").append(credit.getValue()).append("\",\n");
                            json.append("\t\"currencyid\": \"").append(credit.getCurrencyID()).append("\",\n");
                            json.append("\t\"interest\": \"").append(credit.getInterest()).append("\",\n");
                            json.append("\t\"commission\": \"").append(credit.getCommission()).append("\",\n");
                            json.append("\t\"rrso\": \"").append(credit.getRrso()).append("\",\n");
                            json.append("\t\"datecreated\": \"").append(credit.getDateCreatedFormatted()).append("\",\n");
                            json.append("\t\"dateended\": \"").append(credit.getDateEndedFormatted()).append("\",\n");
                            json.append("\t\"monthly\": \"").append(credit.getMonthly()).append("\",\n");
                            json.append("\t\"monthsremaining\": \"").append((int) Math.round(credit.getRemaining() / (double) credit.getMonthly())).append("\",\n");
                            json.append("\t\"remaining\": \"").append(credit.getRemaining()).append("\"\n");
                            if (i == credits.size())
                                json.append("}\n");
                            else
                                json.append("},\n");
                            i++;
                        }
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("totalmoney")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int currency = Integer.parseInt(request.getParameter("currency"));

                    Long savings = data.getTotalSavings(login, password, currency);
                    if (savings != null) {
                        json = new StringBuilder("{\n");
                        json.append("\"value\": \"").append(savings).append("\"\n");
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("totalcredits")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int currency = Integer.parseInt(request.getParameter("currency"));

                    Long credits = data.getTotalCredits(login, password, currency);
                    if (credits != null) {
                        json = new StringBuilder("{\n");
                        json.append("\"value\": \"").append(credits).append("\"\n");
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (attributes[0].equals("login") && attributes[1].equals("transactions")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");

                    List transactions = data.getTransactions(login, password);
                    if (transactions != null) {
                        json = new StringBuilder("{\n");
                        int i = 1;
                        for (Object transaction: transactions) {
                            json.append("\"").append(((TransactionRecord) transaction).getID()).append("\": {\n");
                            json.append("\t\"senderid\": \"").append(((TransactionRecord) transaction).getSenderID()).append("\",\n");
                            json.append("\t\"receiverid\": \"").append(((TransactionRecord) transaction).getReceiverID()).append("\",\n");
                            json.append("\t\"date\": \"").append(((TransactionRecord) transaction).getDateFormatted()).append("\",\n");
                            json.append("\t\"value\": \"").append(((TransactionRecord) transaction).getValue()).append("\",\n");
                            json.append("\t\"title\": \"").append(((TransactionRecord) transaction).getTitle()).append("\",\n");
                            json.append("\t\"currencyid\": \"").append(((TransactionRecord) transaction).getCurrencyID()).append("\"\n");
                            if (i == transactions.size())
                                json.append("}\n");
                            else
                                json.append("},\n");
                            i++;
                        }
                        json.append("}");
                        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
            break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String requestUrl = request.getRequestURI();
        String[] attributes = requestUrl.substring(1).split("/");
        switch (attributes.length) {
            case 1:
                if (attributes[0].equals("createclient")) {
                    try {
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String date = request.getParameter("date");
                        String gender = request.getParameter("gender");
                        String street = request.getParameter("street");
                        String num = request.getParameter("num");
                        String city = request.getParameter("city");
                        String postcode = request.getParameter("postcode");
                        String countryName = request.getParameter("country");
                        String login = request.getParameter("login");
                        String passwordHash = request.getParameter("passwordhash");

                        if ( !CharMatcher.ascii().matchesAllOf(login) ||
                             login.length() < 8 || login.length()    > 30  ||
                             name.isEmpty()     || name.length()     > 100 ||
                             surname.isEmpty()  || surname.length()  > 100 ||
                             street.isEmpty()   || street.length()   > 100 ||
                             num.isEmpty()      || num.length()      > 100 ||
                             city.isEmpty()     || city.length()     > 100 ||
                             postcode.isEmpty() || postcode.length() > 100 ||
                             passwordHash.length() == 0 || passwordHash.length() > 100 )
                            throw new Exception("Bad request");

                        data.addClient(name, surname, date, gender, street, num, city, postcode, countryName, login, passwordHash);
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                else if (attributes[0].equals("updatepassword")) {
                    try {
                        String login = request.getParameter("login");
                        String newPasswordHash = request.getParameter("passwordhash");
                        if (login.isEmpty() || newPasswordHash.isEmpty())
                            throw new Exception("Bad request");
                        response.setStatus(HttpServletResponse.SC_OK);
                        data.updatePassword(login, newPasswordHash);
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            break;
            case 2:
                if (attributes[0].equals("login") && attributes[1].equals("transaction")) {
                    try {
                        String title = request.getParameter("title");
                        if (title.isEmpty() || title.length() > 100)
                            throw new Exception("Bad request");
                        String login = request.getParameter("login");
                        String passwordhash = request.getParameter("passwordhash");
                        Login log = data.getLogin(login, passwordhash);
                        if (log != null) {
                            int payerID = Integer.parseInt(request.getParameter("payerid"));
                            int targetID = Integer.parseInt(request.getParameter("targetid"));
                            long amount = Long.parseLong(request.getParameter("amount"));
                            int currency = Integer.parseInt(request.getParameter("currencyid"));
                            data.makeTransfer(payerID, targetID, amount, title, currency);
                        }
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("createaccount")) {
                    String login = request.getParameter("login");
                    String passwordhash = request.getParameter("passwordhash");
                    int currencyID = Integer.parseInt(request.getParameter("currencyid"));
                    Login log = data.getLogin(login, passwordhash);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.addAccount(currencyID, client.getID());
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("createorupdatecontact")) {
                    try {
                        String login = request.getParameter("login");
                        String password = request.getParameter("passwordhash");
                        String name = request.getParameter("name");
                        int accountID = Integer.parseInt(request.getParameter("accountid"));

                        if (name.isEmpty() || name.length() > 100)
                            throw new Exception("Bad request");
                        Login log = data.getLogin(login, password);
                        if (log != null) {
                            Client client = data.getClient(log.getID());
                            data.addOrUpdateContact(client.getID(), accountID, name);
                        }
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("createinvestment")) {
                    try {
                        String login = request.getParameter("login");
                        String password = request.getParameter("passwordhash");
                        String name = request.getParameter("name");
                        if (name.isEmpty() || name.length() > 100)
                            throw new Exception("Bad request");
                        long value = Long.parseLong(request.getParameter("value"));
                        double profitRate = Double.parseDouble(request.getParameter("profrate"));
                        double yearProfitRate = Double.parseDouble(request.getParameter("yearprofrate"));
                        int capitalisationPeriodID = Integer.parseInt(request.getParameter("capperiod"));
                        int accountID = Integer.parseInt(request.getParameter("accountid"));
                        Login log = data.getLogin(login, password);
                        if (log != null) {
                            Client client = data.getClient(log.getID());
                            System.out.println("Read");
                            data.addInvestment(client.getID(), name, value, profitRate, yearProfitRate, capitalisationPeriodID, accountID);
                        }
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("createcredit")) {
                    try {
                        String login = request.getParameter("login");
                        String password = request.getParameter("passwordhash");
                        String name = request.getParameter("name");
                        if (name.isEmpty() || name.length() > 100)
                            throw new Exception("Bad request");
                        long value = Long.parseLong(request.getParameter("value"));
                        double interest = Double.parseDouble(request.getParameter("interest"));
                        double commission = Double.parseDouble(request.getParameter("commission"));
                        int months = Integer.parseInt(request.getParameter("months"));
                        int accountID = Integer.parseInt(request.getParameter("accountid"));
                        Login log = data.getLogin(login, password);
                        if (log != null) {
                            Client client = data.getClient(log.getID());
                            data.addCredit(client.getID(), name, value, interest, commission, months, accountID);
                        }
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("removecontact")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int accountID = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.removeContact(client.getID(), accountID);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("removeinvestment")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int investmentID = Integer.parseInt(request.getParameter("investmentid"));
                    int accountID = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.removeInvestment(client.getID(), investmentID, accountID);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("removeaccount")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int accountID = Integer.parseInt(request.getParameter("accountid"));

                    data.removeAccount(login, password, accountID);
                }
                if (attributes[0].equals("login") && attributes[1].equals("payinstallment")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int creditID = Integer.parseInt(request.getParameter("creditid"));
                    int accountID = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.updateRemainingCredit(client.getID(), creditID, accountID);
                    }
                }
                if (attributes[0].equals("login") && attributes[1].equals("updatelogin")) {
                    try {
                        String login = request.getParameter("login");
                        String passwordhash = request.getParameter("passwordhash");
                        String newLogin = request.getParameter("newlogin");
                        if (!login.isEmpty() &&
                                !passwordhash.isEmpty() &&
                                !newLogin.isEmpty() &&
                                CharMatcher.ascii().matchesAllOf(newLogin)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            data.updateLogin(login, passwordhash, newLogin);
                        } else
                            throw new Exception("Bad request");
                    } catch (NullPointerException ex) {
                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    } catch (Exception ex) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                break;
        }
    }
}