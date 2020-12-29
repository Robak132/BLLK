package com.bllk.Servlet;
import com.bllk.Apka.BankClients;
import com.bllk.Apka.Logins;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.util.List;
import java.util.Properties;

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
            configuration.setProperties(settings);
            configuration.addAnnotatedClass(Logins.class);
            configuration.addAnnotatedClass(BankClients.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate Java Config serviceRegistry created");
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BankClients get_client(int clientid) {
        BankClients client = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM BankClients WHERE id=" + clientid;
            Query query = session.createQuery(hql);
            client = (BankClients) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            factory.close();
            refresh();
        }
        return client;
    }
    public Logins get_login(String login, String password) {
        Logins result = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Logins WHERE login='" + login + "' AND password='" + password + "'";
            Query query = session.createQuery(hql);
            List list = query.list();

            if (list.size() >= 1)
                result = (Logins) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            factory.close();
            refresh();
        }
        return result;
    }
}