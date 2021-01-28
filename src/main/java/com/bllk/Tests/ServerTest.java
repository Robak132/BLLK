package com.bllk.Tests;

import static org.junit.Assert.*;
import java.io.*;
import javax.servlet.http.HttpServletResponse;

import com.bllk.Servlet.Server;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ServerTest {
    private final Server server = new Server();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    /* Co jest testowane:
        - /updatepassword
        - /login/updatelogin
        - /login/transaction
        - /login/createcredit
        - /login/createinvestment
        - /login/createorupdatecontact
        - /createclient
    */

    /* ---------------------------------------------- /updatepassword ----------------------------------------------- */
    @Test
    public void newPasswordHashNull() throws IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login"       , "bilbo");
        request.addParameter("passwordhash", "");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newPasswordAssert() throws IOException {
        request.setRequestURI("/updatepassword");
        request.addParameter("login"       , "bilbo");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    /* --------------------------------------------- /login/updatelogin --------------------------------------------- */
    @Test
    public void newLoginNotASCII() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin"    , "zażółć_gęślą_jaźń");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newLoginNull() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin"    , "");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newLoginAssert() throws IOException {
        request.setRequestURI("/login/updatelogin");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("newlogin"    , "szokobons");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    /* --------------------------------------------- /login/transaction --------------------------------------------- */
    @Test
    public void newTransactionNameNull() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid"     , "0");
        request.addParameter("targetid"    , "1");
        request.addParameter("title"       , "");
        request.addParameter("amount"      , "1");
        request.addParameter("currencyid"  , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newTransactionNameTooLong() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid"     , "0");
        request.addParameter("targetid"    , "1");
        request.addParameter("title"       , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("amount"      , "1");
        request.addParameter("currencyid"  , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newTransactionAssert() throws IOException {
        request.setRequestURI("/login/transaction");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("payerid"     , "0");
        request.addParameter("targetid"    , "1");
        request.addParameter("title"       , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxy"); // 100
        request.addParameter("amount"      , "1");
        request.addParameter("currencyid"  , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    /* -------------------------------------------- /login/createcredit --------------------------------------------- */
    @Test
    public void newCreditAssert() throws IOException {
        request.setRequestURI("/login/createcredit");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxy"); // 100
        request.addParameter("value"       , "1");
        request.addParameter("interest"    , "1");
        request.addParameter("commission"  , "1");
        request.addParameter("months"      , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    @Test
    public void newCreditNameNull() throws IOException {
        request.setRequestURI("/login/createcredit");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "");
        request.addParameter("value"       , "1");
        request.addParameter("interest"    , "1");
        request.addParameter("commission"  , "1");
        request.addParameter("months"      , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newCreditNameTooLong() throws IOException {
        request.setRequestURI("/login/createcredit");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("value"       , "1");
        request.addParameter("interest"    , "1");
        request.addParameter("commission"  , "1");
        request.addParameter("months"      , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    /* ------------------------------------------ /login/createinvestment ------------------------------------------- */
    @Test
    public void newInvestmentAssert() throws IOException {
        request.setRequestURI("/login/createinvestment");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxy"); // 100
        request.addParameter("value"       , "1");
        request.addParameter("profrate"    , "1");
        request.addParameter("yearprofrate", "1");
        request.addParameter("capperiod"   , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    @Test
    public void newInvestmentNameNull() throws IOException {
        request.setRequestURI("/login/createinvestment");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "");
        request.addParameter("value"       , "1");
        request.addParameter("profrate"    , "1");
        request.addParameter("yearprofrate", "1");
        request.addParameter("capperiod"   , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void newInvestmentNameTooLong() throws IOException {
        request.setRequestURI("/login/createinvestment");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("value"       , "1");
        request.addParameter("profrate"    , "1");
        request.addParameter("yearprofrate", "1");
        request.addParameter("capperiod"   , "1");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    /* ---------------------------------------- /login/createorupdatecontact ---------------------------------------- */
    @Test
    public void contactNameAssert() throws IOException {
        request.setRequestURI("/login/createorupdatecontact");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxy"); // 100
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    @Test
    public void contactNameTooLong() throws IOException {
        request.setRequestURI("/login/createorupdatecontact");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void contactNameNull() throws IOException {
        request.setRequestURI("/login/createorupdatecontact");
        request.addParameter("login"       , "schogetten");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "");
        request.addParameter("accountid"   , "0");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    /* ----------------------------------------------- /createclient ------------------------------------------------ */
    @Test
    public void createClientAssert() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    @Test
    public void createClientLoginTooShort() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugul"); // 7
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientLoginTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "abcdefghi jklmnopqr stuvwxyza b"); // 31
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientPasswordHashNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientPasswordHashTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientNameNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientNameTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientNameNotASCII() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "zażółć gęślą jaźń");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google"); // 101
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientSurnameNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientSurameTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientStreetNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientStreetTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientNumNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientNumTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientCityNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "");
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientCityTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("postcode"    , "94043");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientPostcodeNull() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "");
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    public void createClientPostcodeTooLong() throws IOException {
        request.setRequestURI("/createclient");
        request.addParameter("login"       , "gulugulu");
        request.addParameter("passwordhash", "$2a$12$7ohvjhK6XYvHI/lj041hX.jcx9F5W1gVaAZVIl7hyUaEDqKwXNJtS");
        request.addParameter("name"        , "Google");
        request.addParameter("surname"     , "Wujek");
        request.addParameter("date"        , "1998-09-04");
        request.addParameter("gender"      , "M");
        request.addParameter("street"      , "Amphitheatre Pkwy");
        request.addParameter("num"         , "1600");
        request.addParameter("city"        , "Mountain View");
        request.addParameter("postcode"    , "abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwx abcdefghijklmnopqrstuvwxyz"); // 101
        request.addParameter("country"     , "USA");
        server.doPost(request, response);
        assertEquals(response.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }
 }