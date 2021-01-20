package com.bllk.Servlet;

import com.bllk.Servlet.mapclasses.*;

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
        String[] atributes = requestUrl.substring(1).split("/");
        Client client;
        String json;

        switch (atributes.length) {
            case 1:
                if (atributes[0].equals("countries")) {
                    List countries = data.getCountries();
                    if (countries != null) {
                        json = "{\n";
                        for (Object country: countries)
                            json += "\"" + ((Country) country).getName() + "\": \"" + ((Country) country).getId() + "\",\n";
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    } else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("currencies")) {
                    List currencies = data.getCurrencies();
                    if (currencies != null) {
                        json = "{\n";
                        for (Object currency: currencies)
                            json += "\"" + ((Currency) currency).getID() + "\": \"" + ((Currency) currency).getShortcut() + "\",\n";
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");

                    Login loginclass = data.getLogin(login, password);
                    if (login != null) {
                        client = data.getClient(loginclass.getID());
                        json = "{\n";
                        json += "\"id\": \"" + client.getID() + "\",\n";
                        json += "\"name\": \"" + client.getName() + "\",\n";
                        json += "\"surname\": \"" + client.getSurname() + "\",\n";
                        json += "\"birthdate\": \"" + client.getBirthDate().toString() + "\",\n";
                        json += "\"addressid\": \"" + client.getAddressID() + "\",\n";
                        json += "\"loginid\": \"" + client.getLoginID() + "\",\n";
                        json += "\"gender\": \"" + client.getGender() + "\"\n";
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    } else {
                        response.getOutputStream().println("{}");
                    }
                }
            break;
            case 2:
                if (atributes[0].equals("account")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int accountid = Integer.parseInt(atributes[1]);

                    if (login == null || password == null) {
                        Account account = data.getAccount(accountid);
                        if (account != null) {
                            json = "{\n";
                            json += "\"id\": \"" + account.getID() + "\",\n";
                            json += "\"currency\": \"" + account.getCurrencyID() + "\"\n";
                            json += "}";
                            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                        }
                        else
                            response.getOutputStream().println("{}");
                    }
                    else {
                        Login log = data.getLogin(login, password);
                        if (log != null) {
                            Account account = data.getAccount(accountid);
                            if (account != null) {
                                json = "{\n";
                                json += "\"id\": \"" + account.getID() + "\",\n";
                                json += "\"value\": \"" + account.getValue() + "\",\n";
                                json += "\"currency\": \"" + account.getCurrencyID() + "\",\n";
                                json += "\"ownerid\": \"" + account.getOwnerID() + "\"\n";
                                json += "}";
                                response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                            }
                            else
                                response.getOutputStream().println("{}");
                        }
                        else
                            response.getOutputStream().println("{}");
                    }
                }
                else if (atributes[0].equals("getsalt")) {
                    String salt = data.getSalt(atributes[1]);
                    if (salt != null)
                        response.getOutputStream().println("{\n\"salt\": \"" + salt + "\"\n}");
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("checklogin")) {
                    boolean does_exist = data.checkLogin(atributes[1]);
                    if (does_exist)
                        response.getOutputStream().println("{\n\"bool\": \"true\"\n}");
                    else
                        response.getOutputStream().println("{\n\"bool\": \"false\"\n}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("accounts")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List accounts = data.getAccounts(login, password);
                    if (accounts != null) {
                        json = "{\n";
                        int iter = 1;
                        for (Object account: accounts) {
                            json += "\"" + ((Account) account).getID() + "\": {\n";
                            json += "\t\"value\": \"" + ((Account) account).getValue() + "\",\n";
                            json += "\t\"currencyid\": \"" + (((Account) account)).getCurrencyID() + "\",\n";
                            if (iter == accounts.size())
                                json += "}\n";
                            else
                                json += "},\n";
                            iter++;
                        }
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("contacts")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List contacts = data.getContacts(login, password);
                    if (contacts != null) {
                        int iter = 1;
                        json = "{\n";
                        for (Object contact: contacts) {
                            json += "\"" + ((Contact) contact).getName() + "\": \"" + ((Contact) contact).getTargetID() + "\"";
                            if (iter == contacts.size())
                                json += "\n";
                            else
                                json += ",\n";
                            iter++;
                        }
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("investments")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List investments = data.getInvestments(login, password);
                    if (investments != null) {
                        int iter = 1;
                        json = "{\n";
                        for (Object inv: investments) {
                            Investment investment = (Investment) inv;
                            json += "\"" + investment.getID() + "\": {\n";
                            json += "\t\"ownerid\": \"" + investment.getOwnerID() + "\",\n";
                            json += "\t\"name\": \"" + investment.getName() + "\",\n";
                            json += "\t\"value\": \"" + investment.getValue() + "\",\n";
                            json += "\t\"profit\": \"" + investment.getProfit() + "\",\n";
                            json += "\t\"yearprofit\": \"" + investment.getYearProfit() + "\",\n";
                            json += "\t\"capperiod\": \"" + investment.getCapPeriod() + "\",\n";
                            json += "\t\"datecreated\": \"" + investment.getDateCreatedFormatted() + "\",\n";
                            if (investment.getDateEnd() != null)
                                json += "\t\"dateended\": \"" + investment.getDateEndedFormatted() + "\",\n";
                            json += "\t\"currencyid\": \"" + investment.getCurrencyID() + "\"\n";
                            if (iter == investments.size())
                                json += "}\n";
                            else
                                json += "},\n";
                            iter++;
                        }
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("credits")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List credits = data.getCredits(login, password);
                    if (credits != null) {
                        int iter = 1;
                        json = "{\n";
                        for (Object inv: credits) {
                            Credit credit = (Credit) inv;
                            json += "\"" + credit.getID() + "\": {\n";
                            json += "\t\"ownerid\": \"" + credit.getOwnerid() + "\",\n";
                            json += "\t\"name\": \"" + credit.getName() + "\",\n";
                            json += "\t\"value\": \"" + credit.getValue() + "\",\n";
                            json += "\t\"currencyid\": \"" + credit.getCurrencyID() + "\",\n";
                            json += "\t\"interest\": \"" + credit.getInterest() + "\",\n";
                            json += "\t\"commission\": \"" + credit.getCommission() + "\",\n";
                            json += "\t\"rrso\": \"" + credit.getRrso() + "\",\n";
                            json += "\t\"datecreated\": \"" + credit.getDateCreatedFormatted() + "\",\n";
                            json += "\t\"dateended\": \"" + credit.getDateEndedFormatted() + "\",\n";
                            json += "\t\"monthly\": \"" + credit.getMonthly() + "\",\n";
                            json += "\t\"monthsremaining\": \"" + (int) Math.round(credit.getRemaining()/(double)credit.getMonthly()) + "\",\n";
                            json += "\t\"remaining\": \"" + credit.getRemaining() + "\"\n";
                            if (iter == credits.size())
                                json += "}\n";
                            else
                                json += "},\n";
                            iter++;
                        }
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("totalmoney")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int currency = Integer.parseInt(request.getParameter("currency"));

                    Long savings = data.getTotalSavings(login, password, currency);
                    if (savings != null) {
                        json = "{\n";
                        json += "\"value\": \"" + savings + "\"\n";
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("totalcredits")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int currency = Integer.parseInt(request.getParameter("currency"));

                    Long credits = data.getTotalCredits(login, password, currency);
                    if (credits != null) {
                        json = "{\n";
                        json += "\"value\": \"" + credits + "\"\n";
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("transactions")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");

                    List transactions = data.getTransactions(login, password);
                    if (transactions != null) {
                        json = "{\n";
                        int iter = 1;
                        for (Object transaction: transactions) {
                            json += "\"" + ((TransactionRecord) transaction).getID() + "\": {\n";
                            json += "\t\"senderid\": \"" + ((TransactionRecord) transaction).getSenderID() + "\",\n";
                            json += "\t\"receiverid\": \"" + ((TransactionRecord) transaction).getReceiverID() + "\",\n";
                            json += "\t\"date\": \"" + ((TransactionRecord) transaction).getDateFormatted() + "\",\n";
                            json += "\t\"value\": \"" + ((TransactionRecord) transaction).getValue() + "\",\n";
                            json += "\t\"title\": \"" + ((TransactionRecord) transaction).getTitle() + "\",\n";
                            json += "\t\"currencyid\": \"" + ((TransactionRecord) transaction).getCurrencyID() + "\"\n";
                            if (iter == transactions.size())
                                json += "}\n";
                            else
                                json += "},\n";
                            iter++;
                        }
                        json += "}";
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
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
        String[] atributes = requestUrl.substring(1).split("/");
        switch (atributes.length) {
            case 1:
                if (atributes[0].equals("createclient")) {
                    try {
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String date = request.getParameter("date");
                        String gender = request.getParameter("gender");
                        String street = request.getParameter("street");
                        String num = request.getParameter("num");
                        String city = request.getParameter("city");
                        String postcode = request.getParameter("postcode");
                        String country_name = request.getParameter("country");
                        String login = request.getParameter("login");
                        String password_hash = request.getParameter("passwordhash");

                        data.addClient(name, surname, date, gender, street, num, city, postcode, country_name, login, password_hash);
                    }
                    catch (Exception ignored) {}
                }
                else if (atributes[0].equals("updatepassword")) {
                    try {
                        String login = request.getParameter("login");
                        String new_password_hash = request.getParameter("passwordhash");
                        data.updatePassword(login, new_password_hash);
                    }
                    catch (Exception ignored) {}
                }
            break;
            case 2:
                if (atributes[0].equals("login") && atributes[1].equals("transaction")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        int payerid = Integer.parseInt(request.getParameter("payerid"));
                        int targetid = Integer.parseInt(request.getParameter("targetid"));
                        String title = request.getParameter("title");
                        int amount = Integer.parseInt(request.getParameter("amount"));
                        int currency = Integer.parseInt(request.getParameter("currencyid"));
                        data.makeTransfer(payerid, targetid, amount, title, currency);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("createaccount")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    int currencyid = Integer.parseInt(request.getParameter("currencyid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.addAccount(currencyid, client.getID());
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("createorupdatecontact")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    String name = request.getParameter("name");
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.addOrUpdateContact(client.getID(), accountid, name);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("createinvestment")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    String name = request.getParameter("name");
                    int value = Integer.parseInt(request.getParameter("value"));
                    double profrate = Double.parseDouble(request.getParameter("profrate"));
                    double yearprofrate = Double.parseDouble(request.getParameter("yearprofrate"));
                    int capperoid = Integer.parseInt(request.getParameter("capperoid"));
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        System.out.println("readed");
                        data.addInvestment(client.getID(), name, value, profrate, yearprofrate, capperoid, accountid);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("createcredit")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    String name = request.getParameter("name");
                    int value = Integer.parseInt(request.getParameter("value"));
                    double interest = Double.parseDouble(request.getParameter("interest"));
                    double commission = Double.parseDouble(request.getParameter("commission"));
                    int months = Integer.parseInt(request.getParameter("months"));
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        //System.out.println("logged in");
                        data.addCredit(client.getID(), name, value, interest, commission, months, accountid);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("removecontact")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.removeContact(client.getID(), accountid);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("removeinvestment")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int investmentid = Integer.parseInt(request.getParameter("investmentid"));
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.removeInvestment(client.getID(), investmentid, accountid);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("removeaccount")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int accountid = Integer.parseInt(request.getParameter("accountid"));

                    data.removeAccount(login, password, accountid);
                }
                if (atributes[0].equals("login") && atributes[1].equals("payinstallment")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    int creditid = Integer.parseInt(request.getParameter("creditid"));
                    int accountid = Integer.parseInt(request.getParameter("accountid"));
                    Login log = data.getLogin(login, password);
                    if (log != null) {
                        Client client = data.getClient(log.getID());
                        data.updateRemainingCredit(client.getID(), creditid, accountid);
                    }
                }
                if (atributes[0].equals("login") && atributes[1].equals("updatelogin")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("passwordhash");
                    String newlogin = request.getParameter("newlogin");
                    data.updateLogin(login, password, newlogin);
                }
                break;
        }
    }
}