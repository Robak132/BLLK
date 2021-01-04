package com.bllk.Servlet;

import com.bllk.Servlet.mapclasses.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.List;

public class Server extends HttpServlet {
    private Database data;

    public void init(ServletConfig config) throws ServletException {
        data = new Database();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUrl = request.getRequestURI();
        String[] atributes = requestUrl.substring(1).split("/");
//        System.out.println(requestUrl);
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
                        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));;
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
                        json += "\"loginid\": \"" + client.getLoginID() + "\"\n";
                        json += "}";
                        response.getOutputStream().println(json);
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
                            response.getOutputStream().println(json);
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
                                response.getOutputStream().println(json);
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
                else if (atributes[0].equals("login") && atributes[1].equals("accounts")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    List accounts = data.getAccounts(login, password);
                    if (accounts.size() != 0) {
                        json = "{\n";
                        for (Object account : accounts)
                            json += "\"" + ((Account) account).getID() + "\": \"" + ((Account) account).getValue() + "\",\n";
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

                    Integer savings = data.getTotalSavings(login, password, currency);
                    if (savings != null) {
                        json = "{\n";
                        json += "\"value\": \"" + savings + "\",\n";
                        json += "}";
                        response.getOutputStream().println(json);
                    }
                    else
                        response.getOutputStream().println("{}");
                }
                else if (atributes[0].equals("login") && atributes[1].equals("transactions")) {
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");

                    List transactions = data.getTransactionsIN(login, password);
                    if (transactions != null) {
                        json = "{\n";
                        int iter = 1;
                        for (Object transaction: transactions) {
                            json += "\"" + iter + "\": {\n";
                            json += "\t\"senderid\": \"" + ((TransactionRecord) transaction).getSenderID() + "\",\n";
                            json += "\t\"receiverid\": \"" + ((TransactionRecord) transaction).getReceiverID() + "\",\n";
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
            case 3:
                if (atributes[0].equals("login")) {
                    Login login = data.getLogin(atributes[1], atributes[2]);
                    if (login != null) {
                        client = data.getClient(login.getID());
                        json = "{\n";
                        json += "\"id\": \"" + client.getID() + "\",\n";
                        json += "\"name\": \"" + client.getName() + "\",\n";
                        json += "\"surname\": \"" + client.getSurname() + "\"\n";
                        json += "}";
                        response.getOutputStream().println(json);
                    } else {
                        response.getOutputStream().println("{}");
                    }
                }
            break;
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String[] atributes = requestUrl.substring(1).split("/");
        switch (atributes.length) {
            case 1:
                if (atributes[0].equals("createclient")) {
                    try {
                        String name = request.getParameter("name");
                        String surname = request.getParameter("surname");
                        String date = request.getParameter("date");
                        String street = request.getParameter("street");
                        String num = request.getParameter("num");
                        String city = request.getParameter("city");
                        String postal_code = request.getParameter("postal_code");
                        String country_name = request.getParameter("country");
                        String login = request.getParameter("login");
                        String password_hash = request.getParameter("passwordhash");

                        data.addClient(name, surname, date, street, num, city, postal_code, country_name, login, password_hash);
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
                break;
        }
    }
}