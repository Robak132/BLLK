package com.bllk.Servlet;
import com.bllk.Mapclasses.*;
import com.bllk.Mapclasses.Currency;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {
    private static SessionFactory factory;

    public Database() {
        refresh();
    }
    private void refresh() {
        try {
            Configuration configuration = new Configuration();

            Properties settings = new Properties();
            settings.put(Environment.DRIVER, "oracle.jdbc.OracleDriver");
            settings.put(Environment.URL, "***REMOVED***");
            settings.put(Environment.USER, "***REMOVED***");
            settings.put(Environment.PASS, "***REMOVED***");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.Oracle8iDialect");
            settings.put(Environment.SHOW_SQL, "true");
            settings.put("hibernate.connection.defaultNChar", "true");
            settings.put("hibernate.connection.useUnicode", "true");
            settings.put("hibernate.connection.characterEncoding", "utf8");
            settings.put("hibernate.connection.CharSet", "utf8");
            configuration.setProperties(settings);

            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(Address.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Contact.class);
            configuration.addAnnotatedClass(Country.class);
            configuration.addAnnotatedClass(Credit.class);
            configuration.addAnnotatedClass(Currency.class);
            configuration.addAnnotatedClass(Investment.class);
            configuration.addAnnotatedClass(Login.class);
            configuration.addAnnotatedClass(TransactionRecord.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client getClient(int loginID) {
        Client client = null;

        try {
            Session session = factory.openSession();

            Query query = session.createQuery("FROM Client WHERE login_id=:loginid");
            query.setParameter("loginid", loginID);
            List list = query.list();

            if (list.size() >= 1)
                client = (Client) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return client;
    }
    public String getSalt(String userLogin) {
        String salt = null;

        try {
            Session session = factory.openSession();

            Query query = session.createQuery("FROM Login WHERE login=:login");
            query.setParameter("login", userLogin);
            if (query.list().size() >= 1) {
                Login login = (Login) query.list().get(0);
                String hash = login.getPasswordHash();
                salt = hash.substring(0, 29);
            }

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return salt;
    }
    public boolean checkLogin(String userLogin) {
        boolean doesExist = false;

        try {
            Session session = factory.openSession();

            String hql = "FROM Login WHERE login='" + userLogin + "'";
            Query query = session.createQuery(hql);
            if (query.list().size() >= 1)
                doesExist = true;

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return doesExist;
    }
    public List getCountries() {
        List countries = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Country ORDER BY name";
            Query query = session.createQuery(hql);
            countries = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return countries;
    }
    public List getCurrencies() {
        List currencies = null;

        try {
            Session session = factory.openSession();

            Query query = session.createQuery("FROM Currency");
            currencies = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return currencies;
    }
    public List getAccounts(String login, String hashedPassword) {
        List accounts = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT A FROM Account A, Client C, Login L WHERE C.id=A.owner_id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashedPassword);
            accounts = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return accounts;
    }
    public List getContacts(String login, String hashedPassword) {
        List accounts = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT CN FROM Contact CN, Client C, Login L WHERE C.id=CN.ownerid AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashedPassword);
            accounts = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return accounts;
    }
    public List getTransactions(String login, String hashedPassword) {
        List transactions = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT DISTINCT T FROM TransactionRecord T, Account A, Client C, Login L WHERE (A.id=T.receiverID OR A.id=T.senderID) AND A.owner_id=C.id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashedPassword);

            if (query.list().size() >= 1)
                transactions = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return transactions;
    }
    public List getInvestments(String login, String hashedPassword) {
        List investments = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT I FROM Investment I, Client C, Login L WHERE I.ownerid=C.id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashedPassword);

            if (query.list().size() >= 1)
                investments = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return investments;
    }
    public List getCredits(String login, String hashedPassword) {
        List credits = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT Cr FROM Credit Cr, Client C, Login L WHERE Cr.ownerid=C.id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashedPassword);

            if (query.list().size() >= 1)
                credits = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return credits;
    }
    public Login getLogin(String login, String hashedPassword) {
        Login result = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Login WHERE login='" + login + "' AND passwordhash='" + hashedPassword + "'";
            Query query = session.createQuery(hql);
            List list = query.list();

            if (list.size() >= 1)
                result = (Login) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return result;
    }
    public Account getAccount(int accountID) {
        Account account = null;
        try {
            Session session = factory.openSession();

            Query query = session.createQuery("SELECT A FROM Account A where A.id=:param");
            query.setParameter("param", accountID);

            if (query.list().size() >= 1)
                account = (Account) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return account;
    }
    public Long getTotalSavings(String login, String hashedPassword, int currencyID) {
        Long savings = null;
        try {
            Session session = factory.openSession();
            Query query = session.createSQLQuery("SELECT calculate_total_savings(CLIENT_ID, :currency) FROM CLIENTS C\n" +
                    "JOIN LOGINS L ON (L.LOGIN_ID=C.LOGIN_ID)\n" +
                    "WHERE L.LOGIN=:login AND L.PASSWORD_HASH=:password");
            query.setParameter("currency", currencyID);
            query.setParameter("login", login);
            query.setParameter("password", hashedPassword);

            if (query.list().size() >= 1)
                savings = ((BigDecimal) query.list().get(0)).longValue();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return savings;
    }
    public Long getTotalCredits(String login, String hashedPassword, int currencyID) {
        Long credits = null;
        try {
            Session session = factory.openSession();
            Query query = session.createSQLQuery("SELECT calculate_total_credits(CLIENT_ID, :currency) FROM CLIENTS C\n" +
                    "JOIN LOGINS L ON (L.LOGIN_ID=C.LOGIN_ID)\n" +
                    "WHERE L.LOGIN=:login AND L.PASSWORD_HASH=:password");
            query.setParameter("currency", currencyID);
            query.setParameter("login", login);
            query.setParameter("password", hashedPassword);
            if (query.list().size() >= 1)
                credits = ((BigDecimal) query.list().get(0)).longValue();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return credits;
    }

    public void updateRemainingCredit(int ownerID, int creditID, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Credit WHERE ownerid=:ownerid AND id=:creditid");
            query.setParameter("ownerid", ownerID);
            query.setParameter("creditid", creditID);

            if (query.list().size() >= 1) {
                Credit credit = (Credit) query.list().get(0);

                query = session.createQuery("FROM Account WHERE id=:accountid");
                query.setParameter("accountid", accountID);

                if (query.list().size() >= 1) {
                    Account account = (Account) query.list().get(0);
                    account.setValue(account.getValue() - credit.getMonthly());
                    session.update(account);
                    long newRemaining = credit.getRemaining() - credit.getMonthly();
                    if (newRemaining <= 0) {
                        session.delete(credit);
                    } else {
                        credit.setRemaining(newRemaining);
                        session.update(credit);
                    }
                }
            }
            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void updatePassword(String _login, String _hashedPassword) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Login WHERE login=:login");
            query.setParameter("login", _login);

            if (query.list().size() >= 1) {
                Login login = (Login) query.list().get(0);
                login.setPasswordHash(_hashedPassword);
                session.update(login);
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void updateLogin(String _login, String _hashedPassword, String _newLogin) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Login WHERE login=:login AND passwordhash=:password");
            query.setParameter("login", _login);
            query.setParameter("password", _hashedPassword);

            if (query.list().size() >= 1) {
                Login login = (Login) query.list().get(0);
                login.setLogin(_newLogin);
                session.update(login);
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }

    public void makeTransfer(int payerID, int targetID, long amount, String title, int currencyID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query q = session.createQuery("FROM Account WHERE id=:param");
            q.setParameter("param", payerID);
            Account payer = (Account)q.list().get(0);

            Long i = payer.getValue();
            payer.setValue(i-amount);
            session.update(payer);

            q = session.createQuery("from Account where id=:param1");
            q.setParameter("param1", targetID);
            Account target = (Account) q.list().get(0);
            i = target.getValue();

            q = session.createSQLQuery("SELECT C.VALUE_IN_USD*(1/C2.VALUE_IN_USD) FROM CURRENCIES C, CURRENCIES C2 WHERE C.CURRENCY_ID=:param AND C2.CURRENCY_ID=:param2");
            q.setParameter("param", payer.getCurrencyID());
            q.setParameter("param2", target.getCurrencyID());
            float modifier = ((BigDecimal) q.list().get(0)).floatValue();
            long new_amount = (long) (amount * modifier);

            target.setValue(i + new_amount);
            session.update(target);

            int id = ((BigDecimal) session.createSQLQuery("SELECT MAX(TRANSACTION_ID) FROM TRANSACTIONS").list().get(0)).intValue() + 1;
            TransactionRecord transactionRecord = new TransactionRecord(id, payerID, targetID, title, amount, currencyID);
            session.save(transactionRecord);

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void addAccount(int currencyID, int ownerID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            int id = ((BigDecimal) session.createSQLQuery("SELECT MAX(ACCOUNT_ID) FROM ACCOUNTS").list().get(0)).intValue() + 1;
            Account account = new Account(id, 0L, currencyID, ownerID);
            session.save(account);

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void addOrUpdateContact(int ownerID, int accountID, String name) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Contact WHERE ownerid=:ownerid AND targetid=:accountid");
            query.setParameter("ownerid", ownerID);
            query.setParameter("accountid", accountID);

            if (query.list().size() >= 1) {
                Contact contact = (Contact) query.list().get(0);
                contact.setName(name);
                session.update(contact);
            }
            else {
                int id = 1;
                query = session.createSQLQuery("SELECT MAX(CONTACT_ID) FROM CONTACTS");
                BigDecimal idbig = ((BigDecimal) query.list().get(0));
                if (idbig != null)
                    id = idbig.intValue() + 1;

                Contact account = new Contact(id, ownerID, accountID, name);
                session.save(account);
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void addInvestment(int ownerID, String name, long value, double profitRate, double yearProfitRate, int capitalisationPeriod, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            int id = 1;
            Query query = session.createQuery("FROM Account WHERE id=:accountid");
            query.setParameter("accountid", accountID);

            if (query.list().size() >= 1) {
                Account account = (Account) query.list().get(0);
                account.setValue(account.getValue()-value);
                session.update(account);

                query = session.createSQLQuery("SELECT MAX(INVESTMENT_ID) FROM INVESTMENTS");
                BigDecimal idbig = ((BigDecimal) query.list().get(0));
                if (idbig != null)
                    id = idbig.intValue() + 1;

                Investment investment = new Investment(id, name, ownerID, value, profitRate, yearProfitRate, capitalisationPeriod, account.getCurrencyID());
                session.save(investment);
            }
            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void addCredit(Integer ownerID, String name, long value, double interest, double commission, long months, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            int id = 1;
            Query query = session.createQuery("FROM Account WHERE id=:accountid");
            query.setParameter("accountid", accountID);

            if (query.list().size() >= 1) {
                Account account = (Account) query.list().get(0);
                account.setValue(account.getValue()+value);
                session.update(account);

                int currencyID = account.getCurrencyID();

                query = session.createSQLQuery("SELECT MAX(CREDIT_ID) FROM CREDITS");
                BigDecimal idbig = ((BigDecimal) query.list().get(0));
                if (idbig != null)
                    id = idbig.intValue() + 1;

                query = session.createSQLQuery("SELECT add_months(SYSDATE, :param) FROM DUAL");
                Date endDate = (Date) query.setParameter("param", months).list().get(0);

                Credit credit = new Credit(id, ownerID, name, value, currencyID, interest, commission, endDate);
                session.save(credit);
            }
            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void addClient(String _name, String _surname, String _date, String _gender, String _street, String _num, String _city,
                          String _postcode, String _countryName, String _login, String _password) {

        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            StoredProcedureQuery query = session.createStoredProcedureQuery("ADD_CLIENT");
            query.registerStoredProcedureParameter("p_name",          String.class, ParameterMode.IN).setParameter("p_name",          _name        );
            query.registerStoredProcedureParameter("p_surname",       String.class, ParameterMode.IN).setParameter("p_surname",       _surname     );
            query.registerStoredProcedureParameter("p_birth_date",      Date.class, ParameterMode.IN).setParameter("p_birth_date",    formatter.parse(_date));
            query.registerStoredProcedureParameter("p_gender",        String.class, ParameterMode.IN).setParameter("p_gender",        _gender      );
            query.registerStoredProcedureParameter("p_street",        String.class, ParameterMode.IN).setParameter("p_street",        _street      );
            query.registerStoredProcedureParameter("p_num",           String.class, ParameterMode.IN).setParameter("p_num",           _num         );
            query.registerStoredProcedureParameter("p_city",          String.class, ParameterMode.IN).setParameter("p_city",          _city        );
            query.registerStoredProcedureParameter("p_postcode",      String.class, ParameterMode.IN).setParameter("p_postcode",      _postcode    );
            query.registerStoredProcedureParameter("p_country_name",  String.class, ParameterMode.IN).setParameter("p_country_name",  _countryName);
            query.registerStoredProcedureParameter("p_login",         String.class, ParameterMode.IN).setParameter("p_login",         _login       );
            query.registerStoredProcedureParameter("p_password_hash", String.class, ParameterMode.IN).setParameter("p_password_hash", _password    );
            query.execute();

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }

    public void removeContact(int ownerID, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Contact WHERE ownerid=:ownerid AND targetid=:accountid");
            query.setParameter("ownerid", ownerID);
            query.setParameter("accountid", accountID);

            if (query.list().size() >= 1) {
                Contact contact = (Contact) query.list().get(0);
                session.delete(contact);
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void removeInvestment(int ownerID, int investmentID, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("FROM Investment WHERE ownerid=:ownerid AND id=:investmentid");
            query.setParameter("ownerid", ownerID);
            query.setParameter("investmentid", investmentID);

            if (query.list().size() >= 1) {
                Investment investment = (Investment) query.list().get(0);

                query = session.createQuery("FROM Account WHERE id=:accountid");
                query.setParameter("accountid", accountID);

                if (query.list().size() >= 1) {
                    Account account = (Account) query.list().get(0);
                    account.setValue(account.getValue() + investment.getValue());
                    session.update(account);
                    session.delete(investment);
                }
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public void removeAccount(String login, String hashedPassword, int accountID) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query query = session.createQuery("SELECT a FROM Account a, Login l, Client c WHERE l.id=c.login_id AND l.login=:login AND l.passwordhash=:password AND a.id=:accountid");
            query.setParameter("login", login);
            query.setParameter("password", hashedPassword);
            query.setParameter("accountid", accountID);

            if (query.list().size() >= 1) {
                Account account = (Account) query.list().get(0);
                session.delete(account);
            }

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
}
