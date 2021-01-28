package com.bllk.Apka;

import com.bllk.Mapclasses.Account;
import com.bllk.Mapclasses.Client;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ClientServerConnection {
//    private final String serverAddress = "http://localhost";
    private final String serverAddress = "http://178.218.234.129";
    private final String port = "8080";

    public boolean checkConnection() {
        String a = getData("");
        return (!a.equals("Connection refused: connect"));
    }

    public Map<String, String> getCurrencies() {
        JSONObject jsonObject = new JSONObject(getData("currencies"));
        return jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
    }
    public Map<String, Integer> getCountries() {
        JSONObject jsonObject = new JSONObject(getData("countries"));
        Map<String, Integer> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String) e.getValue())));
        return new TreeMap<>(map);
    }
    public Map<String, Integer> getContacts(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/contacts?login=%s&password=%s", login, hashedPassword)));
        Map<String, Integer> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String) e.getValue())));
        return new TreeMap<>(map);
    }
    public Map<Integer, JSONObject> getTransactions(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/transactions?login=%s&password=%s", login, hashedPassword)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getInvestments(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/investments?login=%s&password=%s", login, hashedPassword)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getCredits(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/credits?login=%s&password=%s", login, hashedPassword)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getUserAccounts(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/accounts?login=%s&password=%s", login, hashedPassword)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Client getClient(String login, String hashedPassword) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login?login=%s&password=%s", login, hashedPassword)));
        return new Client(jsonObject.getInt("id"), jsonObject.getString("name"),
                jsonObject.getString("surname"), jsonObject.getString("birthdate"),
                jsonObject.getInt("addressid"), jsonObject.getInt("loginid"),
                jsonObject.getString("gender"));
    }
    public String getSalt(String login) {
        JSONObject jsonObject = new JSONObject(getData(String.format("getsalt/%s", login)));
        return jsonObject.getString("salt");
    }
    public Account getAccount(String login, String hashedPassword, int accountID) {
        JSONObject jsonObject = new JSONObject(getData(String.format("account/%s?login=%s&password=%s", accountID, login, hashedPassword)));
        return new Account(jsonObject.getInt("id"), jsonObject.getLong("value"), jsonObject.getInt("currency"), jsonObject.getInt("ownerid"));
    }
    public Account getBasicAccount(int accountID) {
        JSONObject jsonObject = new JSONObject(getData(String.format("account/%s", accountID)));
        return new Account(jsonObject.getInt("id"), jsonObject.getInt("currency"));
    }
    public long getTotalSavings(String login, String hashedPassword, int currencyID) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/totalmoney?login=%s&password=%s&currency=%s", login, hashedPassword, currencyID)));
        return jsonObject.getLong("value");
    }
    public long getTotalCredits(String login, String hashedPassword, int currencyID) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/totalcredits?login=%s&password=%s&currency=%s", login, hashedPassword, currencyID)));
        return jsonObject.getLong("value");
    }

    public boolean checkLogin(String login) {
        JSONObject jsonObject = new JSONObject(getData(String.format("checklogin/%s", login)));
        String result = jsonObject.getString("bool");
        return result.equals("true");
    }
    public boolean checkAccountExistence(int accountID) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%d", accountID)));
        try {
            int id = json_object.getInt("id");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void makeTransfer(String login, String hashedPassword, int payerID, int targetID, String title, long amount, int currencyID) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&payerid=" + payerID;
        postData += "&targetid=" + targetID;
        postData += "&title=" + title;
        postData += "&amount=" + amount;
        postData += "&currencyid=" + currencyID;

        postData("login/transaction", postData);
    }
    public void createAccount(String login, String hashedPassword, int currencyID) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&currencyid=" + currencyID;

        postData("login/createaccount", postData);
    }
    public void createClient(String name, String surname, String date, String gender, String street, String number,
                             String city, String postcode, String country, String login, String hashedPassword) {
        String postData = "name=" + name;
        postData += "&surname=" + surname;
        postData += "&date=" + date;
        postData += "&gender=" + gender;
        postData += "&street=" + street;
        postData += "&num=" + number;
        postData += "&city=" + city;
        postData += "&postcode=" + postcode;
        postData += "&country=" + country;
        postData += "&login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("createclient", postData);
    }
    public void createInvestment(String login, String hashedPassword, String name, long value, double profitRate, double yearProfitRate, int capitalisationPeriodID, int accountID) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&name=" + name;
        postData += "&value=" + value;
        postData += "&profrate=" + profitRate;
        postData += "&yearprofrate=" + yearProfitRate;
        postData += "&capperiod=" + capitalisationPeriodID;
        postData += "&accountid=" + accountID;

        postData("login/createinvestment", postData);
    }
    public void createCredit(String login, String hashedPassword, String name, long value, double interest, double commission, int months, int accountID) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&name=" + name;
        postData += "&value=" + value;
        postData += "&interest=" + interest;
        postData += "&commission=" + commission;
        postData += "&months=" + months;
        postData += "&accountid=" + accountID;

        postData("login/createcredit", postData);
    }
    public void createOrUpdateContact(String login, String hashedPassword, String name, int accountID) {
        String postData = "name=" + name;
        postData += "&accountid=" + accountID;
        postData += "&login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("login/createorupdatecontact", postData);
    }

    public void updatePassword(String login, String hashedPassword) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("updatepassword", postData);
    }
    public boolean updateLogin(String login, String hashedPassword, String newLogin) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&newlogin=" + newLogin;

        return postData("login/updatelogin", postData);
    }
    public void updateCredit(String login, String hashedPassword, int creditID, int accountID) {
        String postData = "creditid=" + creditID;
        postData += "&accountid=" + accountID;
        postData += "&login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("login/payinstallment", postData);
    }

    public void removeContact(String login, String hashedPassword, int accountID) {
        String postData = "accountid=" + accountID;
        postData += "&login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("login/removecontact", postData);
    }
    public void removeInvestment(String login, String hashedPassword, int investmentID, int accountID) {
        String postData = "investmentid=" + investmentID;
        postData += "&accountid=" + accountID;
        postData += "&login=" + login;
        postData += "&passwordhash=" + hashedPassword;

        postData("login/removeinvestment", postData);
    }
    public void removeAccount(String login, String hashedPassword, int accountID) {
        String postData = "login=" + login;
        postData += "&passwordhash=" + hashedPassword;
        postData += "&accountid=" + accountID;

        postData("login/removeaccount", postData);
    }

    public String getData(String url) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(serverAddress + ":" + port + "/" + url).openConnection();
            System.out.println("GET: " + httpConnection.getURL());
            httpConnection.setRequestMethod("GET");

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(httpConnection.getInputStream());
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                    response.append("\n");
                }
                scanner.close();
                return response.toString();
            } else throw new Exception("Invalid response code");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();
        }
    }
    public boolean postData(String url, String postData) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(serverAddress + ":" + port + "/" + url).openConnection();
            System.out.println("POST: " + httpConnection.getURL());
            httpConnection.setRequestMethod("POST");

            httpConnection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
            writer.write(postData);
            writer.flush();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("POST was successful.");
                return true;
            }
            else
                throw new Exception("POST failed: " + responseCode);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
