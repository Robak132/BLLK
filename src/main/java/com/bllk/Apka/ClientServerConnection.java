package com.bllk.Apka;

import com.bllk.Servlet.mapclasses.Account;
import com.bllk.Servlet.mapclasses.Client;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ClientServerConnection {
    private final String serverAddress = "http://localhost";
//    private final String serverAddress = "http://largehadroncollider.tplinkdns.com";
    private final String port = "8080";

    public boolean checkConnection() {
        String a = getData("");
        return (!a.equals("Connection refused: connect"));
    }

    public Map<String, String> getCurrencies() {
        JSONObject jsonObject = new JSONObject(getData("currencies"));
        Map<String, String> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
        return map;
    }
    public Map<String, Integer> getCountries() {
        JSONObject jsonObject = new JSONObject(getData("countries"));
        Map<String, Integer> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String) e.getValue())));
        return new TreeMap<>(map);
    }
    public Map<String, Integer> getContacts(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/contacts?login=%s&password=%s", login, hashed_password)));
        Map<String, Integer> map = jsonObject.toMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt((String) e.getValue())));
        return new TreeMap<>(map);
    }
    public Map<Integer, JSONObject> getTransactions(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/transactions?login=%s&password=%s", login, hashed_password)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getInvestments(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/investments?login=%s&password=%s", login, hashed_password)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getCredits(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/credits?login=%s&password=%s", login, hashed_password)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Map<Integer, JSONObject> getUserAccounts(String login, String hashed_password) {
        JSONObject jsonObject = new JSONObject(getData(String.format("login/accounts?login=%s&password=%s", login, hashed_password)));
        TreeMap<Integer, JSONObject> map = new TreeMap<>();
        for (Map.Entry<String, Object> pair : jsonObject.toMap().entrySet())
            map.put(Integer.parseInt(pair.getKey()), jsonObject.getJSONObject(pair.getKey()));
        return map;
    }
    public Client getClient(String login, String hashed_password) {
        JSONObject json_object = new JSONObject(getData(String.format("login?login=%s&password=%s", login, hashed_password)));
        return new Client(json_object.getInt("id"), json_object.getString("name"),
                json_object.getString("surname"), json_object.getString("birthdate"),
                json_object.getInt("addressid"), json_object.getInt("loginid"),
                json_object.getString("gender"));
    }
    public String getSalt(String login) {
        JSONObject json_object = new JSONObject(getData(String.format("getsalt/%s", login)));
        return json_object.getString("salt");
    }
    public Account getAccount(String login, String hashed_password, int accountid) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%s?login=%s&password=%s", accountid, login, hashed_password)));
        return new Account(json_object.getInt("id"), json_object.getLong("value"), json_object.getInt("currency"), json_object.getInt("ownerid"));
    }
    public Account getBasicAccount(int accountid) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%s", accountid)));
        return new Account(json_object.getInt("id"), json_object.getInt("currency"));
    }
    public long getTotalSavings(String login, String hashed_password, int currencyid) {
        JSONObject json_object = new JSONObject(getData(String.format("login/totalmoney?login=%s&password=%s&currency=%s", login, hashed_password, currencyid)));
        return json_object.getLong("value");
    }
    public long getTotalCredits(String login, String hashed_password, int currencyid) {
        JSONObject json_object = new JSONObject(getData(String.format("login/totalcredits?login=%s&password=%s&currency=%s", login, hashed_password, currencyid)));
        return json_object.getLong("value");
    }

    public boolean checkLogin(String login) {
        JSONObject json_object = new JSONObject(getData(String.format("checklogin/%s", login)));
        String result = json_object.getString("bool");
        if (result.equals("true"))
            return true;
        else
            return false;
    }
    public boolean checkAccount(int accoundid) {
        JSONObject json_object = new JSONObject(getData(String.format("account/%d", accoundid)));
        try {
            int id = json_object.getInt("id");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void makeTransfer(String login, String hashed_password, int payer_id, int target_id, String title, long amount, int currencyid) {
        String post_data = "login=" + login;
        post_data += "&password=" + hashed_password;
        post_data += "&payerid=" + payer_id;
        post_data += "&targetid=" + target_id;
        post_data += "&title=" + title;
        post_data += "&amount=" + amount;
        post_data += "&currencyid=" + currencyid;

        postData("login/transaction", post_data);
    }
    public void createAccount(String login, String hashed_password, int currencyid) {
        String post_data = "login=" + login;
        post_data += "&password=" + hashed_password;
        post_data += "&currencyid=" + currencyid;

        postData("login/createaccount", post_data);
    }
    public void createClient(String name, String surname, String date, String gender, String street, String num, String city, String postcode, String country, String login, String hashed_password) {
        String post_data = "name=" + name;
        post_data += "&surname=" + surname;
        post_data += "&date=" + date;
        post_data += "&gender=" + gender;
        post_data += "&street=" + street;
        post_data += "&num=" + num;
        post_data += "&city=" + city;
        post_data += "&postcode=" + postcode;
        post_data += "&country=" + country;
        post_data += "&login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("createclient", post_data);
    }
    public void createInvestment(String login, String hashed_password, String name, long value, double profrate, double yearprofrate, int capperoid, int accountid) {
        String post_data = "login=" + login;
        post_data += "&passwordhash=" + hashed_password;
        post_data += "&name=" + name;
        post_data += "&value=" + value;
        post_data += "&profrate=" + profrate;
        post_data += "&yearprofrate=" + yearprofrate;
        post_data += "&capperoid=" + capperoid;
        post_data += "&accountid=" + accountid;

        postData("login/createinvestment", post_data);
    }
    public void createCredit(String login, String hashed_password, String name, long value, double interest, double commission, int months, int accountid) {
        String post_data = "login=" + login;
        post_data += "&passwordhash=" + hashed_password;
        post_data += "&name=" + name;
        post_data += "&value=" + value;
        post_data += "&interest=" + interest;
        post_data += "&commission=" + commission;
        post_data += "&months=" + months;
        post_data += "&accountid=" + accountid;

        postData("login/createcredit", post_data);
    }
    public void createOrUpdateContact(String login, String hashed_password, String name, int accountid) {
        String post_data = "name=" + name;
        post_data += "&accountid=" + accountid;
        post_data += "&login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("login/createorupdatecontact", post_data);
    }

    public void updatePassword(String login, String hashed_password) {
        String post_data = "login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("updatepassword", post_data);
    }
    public boolean updateLogin(String login, String hashed_password, String newlogin) {
        String post_data = "login=" + login;
        post_data += "&passwordhash=" + hashed_password;
        post_data += "&newlogin=" + newlogin;

        return postData("login/updatelogin", post_data);
    }
    public void updateCredit(String login, String hashed_password, int creditid, int accountid) {
        String post_data = "creditid=" + creditid;
        post_data += "&accountid=" + accountid;
        post_data += "&login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("login/payinstallment", post_data);
    }

    public void removeContact(String login, String hashed_password, int accountid) {
        String post_data = "accountid=" + accountid;
        post_data += "&login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("login/removecontact", post_data);
    }
    public void removeInvestment(String login, String hashed_password, int investmentid, int accountid) {
        String post_data = "investmentid=" + investmentid;
        post_data += "&accountid=" + accountid;
        post_data += "&login=" + login;
        post_data += "&passwordhash=" + hashed_password;

        postData("login/removeinvestment", post_data);
    }
    public void removeAccount(String login, String hashed_password, int accountid) {
        String post_data = "login=" + login;
        post_data += "&passwordhash=" + hashed_password;
        post_data += "&accountid=" + accountid;

        postData("login/removeaccount", post_data);
    }

    public String getData(String url) {
        try {
            HttpURLConnection http_connection = (HttpURLConnection) new URL(serverAddress + ":" + port + "/" + url).openConnection();
            System.out.println("GET: " + http_connection.getURL());
            http_connection.setRequestMethod("GET");

            int response_code = http_connection.getResponseCode();
            if (response_code == 200) {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(http_connection.getInputStream());
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
    public boolean postData(String url, String post_data) {
        try {
            HttpURLConnection http_connection = (HttpURLConnection) new URL(serverAddress + ":" + port + "/" + url).openConnection();
            System.out.println("POST: " + http_connection.getURL());
            http_connection.setRequestMethod("POST");

            http_connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(http_connection.getOutputStream());
            writer.write(post_data);
            writer.flush();

            int responseCode = http_connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("POST was successful.");
                return true;
            }
            else
                throw new Exception("Something went wrong.");
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
