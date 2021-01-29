# Opis aplikacji

### Główne funkcje gotowego produktu


Przygotowana przez nas aplikacja pozwala użytkownikowi na:
 - Rejestrację nowego użytkownika,
 - Bezpieczne logowanie do aplikacji,
 - Stworzenie własnego konta o określonej walucie, zmianę jego nazwy i zamknięcie go,
 - Wykonanie przelewu na inny rachunek, także z możliwością przewalutowania,
 - Wykonanie przelewu na własny rachunek,
 - Sprawdzenie historii wykonywanych przelewów i przewalutowań,
 - Stworzenie listy kontaktów do szybkiego wykonywania przelewów, możliwość zmiany ich nazwy oraz usunięcia,
 - Wzięcie kredytu i możliwość spłacania rat,
 - Założenie lokaty i możliwość wypłacenia z niej środków,
 - Zmianę loginu i hasła swojego konta użytkownika.

### Struktura aplikacji i wykorzystane technologie

#### Nasza aplikacja została podzielona na 3 warstwy: 
 - Warstwa prezentacji - obejmuje aplikację kliencką obsługującą wymienione wyżej funkcjonalności. Komunikuje się z warstwą 2. za pomocą stworzonego przez nas API — przesyła zapytanie w formie URL, a następnie odczytuje odpowiedź w formacie JSON, interpretuje ją i wyświetla użytkownikowi.
 - Warstwa logiki biznesowej - obejmuje servlet pośredniczący pomiędzy bazą danych oraz aplikacją końcową, przetwarza i obrabia dane, udostępnia API naszego systemu. Łączy się z niższą warstwą przy pomocy technologii Hibernate.
 - Warstwa danych - złożona z kilkunastu tabel baza danych ma za zadanie przechowywanie informacji dotyczących klientów, kont, przelewów, itp.



# Podsumowanie etapów realizacji projektu

- [x] Etap I (4.12.2020)
 
Etap ten zakładał wybranie tematu projektu, technologii służących do jego realizacji oraz stworzenie planu realizacji zadania z podziałem na etapy. Ważnym aspektem było także  uruchomienie pierwszych programów lub skryptów oraz połączenie się z bazą danych.

 - [x] Etap II (10.12.2020)
 
Na tym etapie:

 - zostanie stworzona większość kluczowych tabel w bazie danych wraz z relacjami pomiędzy nimi *- stworzono 8 tabel w bazie danych*,
 - zostanie stworzona uproszczona wersja interfejsu użytkownika pozwalająca korzystać z kluczowych funkcji programu *- stworzono podstawową aplikację w swingu, dodano możliwość logowania i wysyłania przelewów*,
 - powstaną szkielety większości potrzebnych klas wraz z częścią metod realizujących swoje zadania

- [x] Etap III (15.01.2020)

Na tym etapie:

 - zostanie zaimplementowana zdecydowana większość klas potrzebnych w implementacji projektu *- powstały klasy odpowiedzialne za personalizowane panele, dwa główne obszary aplikacji*,
 - interfejs użytkownika zostanie dopracowany pod względem funkcjonalnym oraz graficznym *- zaimplementowaliśmy klasy odpowiedzialne za ujednolicenie kolorów elementów graficznych i tekstu, a także za fonty w używanych napisach. Dodatkowo zastosowane zostały GridBagLayout, BoxLayout oraz FlowLayout*,
 - stworzony zostanie mechanizm obsługi błędów, w szczególności kontroli danych wprowadzanych przez użytkowników *- wszystkie pola, do których użytkownik może wprowadzać dane zostały obudowane koniecznymi do zachowania integralności i niezawodności warunkami (m.in. zawieranie polskich znaków, odpowiednia długość napisów, itp.)*.

 - [x] Etap IV (29.01.2020)
 
Na tym etapie:

 - dopracowane zostaną istniejące klasy wraz z ich metodami *- dopracowaliśmy kilka drobnych elementów graficznych, a także rozbudowaliśmy mechanizmy odpowiedzialne za wielowątkowość aplikacji klienckiej i walidację danych*,
 - zostaną zaimplementowane testy jednostkowe dla każdej z kluczowych metod *— testowane są głównie miejsca, w których użytkownik może wprowadzić nieprawidłowe dane*.


# Opis napotkanych problemów
### Układ graficzny aplikacji

Gdy rozwój funkcjonalności był jeszcze w powijakach, nie skupialiśmy się na tworzeniu interfejsu użytkownika. Jednakże, wraz z implementacją nowych funkcji, nieuporządkowany układ graficzny aplikacji stał się uciążliwy.
Rozwiązaniem problemu było zastosowanie innych niż FlowLayout układów dostępnych w Swingu. W większości przypadków wystarczył BoxLayout, jednak dla bardziej skomplikowanych komponentów (np. panel reprezentujący rekord historii transakcji, panel reprezentujący konta użytkownika) użyliśmy bardziej rozbudowanego GridBagLayout.

### Interfejs programowania aplikacji (API)

Początkowo aplikacja bezpośrednio łączyła się z bazą danych, jednak nie było to najbardziej bezpieczne i optymalne rozwiązanie. Za namową prowadzącego projekt postanowiliśmy wdrożyć servlet działający w warstwie logiki biznesowej. Nie mając wcześniej kontaktu z podobnymi rozwiązaniami prace nad projektem ustały na kilka dni.
Rozwiązanie przyszło wraz z wiedzą na temat REST API. Po poznaniu założeń tego interfejsu przystąpiliśmy do dalszej realizacji zadań. Używając Apache Tomcat stworzyliśmy Servlet, który umożliwia aplikacji klienckiej komunikację poprzez API.

### Polski alfabet

Jednym z bardziej uciążliwych błędów było zapewnienie zgodności aplikacji z językiem polskim. Problem występował na dwóch płaszczyznach - kodowania systemu operacyjnego (system Windows używa Windows-1250, macOS UTF-8) oraz kodowania bazy danych.
Aby pozbyć się tych problemów należało wprowadzić konwerter w warstwie logiki biznesowej, który przesyłał do aplikacji klienta znaki kodowane przy pomocy UTF-8. Ponieważ programy Javy uruchamiane są używając domyślnego kodowania systemu, należało dodać odpowiednie argumenty rozruchowe aplikacji w systemie Windows, aby również tutaj korzystano z UTF-8.

### Zły dobór typu danych

Już na początku tworzenia bazy danych zdecydowaliśmy się, aby przechowywać ilość pieniędzy w danej walucie przy pomocy typu całkowitego, aby z czasem dane te nie traciły na precyzji. W tym celu zdecydowaliśmy się zmienić jednostkę podstawową waluty na jej 1/100 część, tj. np. złotówki na grosze. Szybko jednak okazało się, że typ int nie był wystarczający, ponieważ większe kwoty powodowały przekroczenie jego zakresu.
Rozwiązaniem było przejście na typ long, co przysporzyło nam żmudnej pracy przy zmianie deklaracji zmiennych, konstruktorów klas, a także aktualizowaniu sporej części metod.


# Opis implementacji wybranej funkcji
### Client

Implementacja większości naszych funkcji rozpoczyna się w warstwie klienta, w tym przypadku rozważymy funkcję tworzącą nowe konto dla klienta. W tym celu tworzymy okno dialogowe za pomocą SWING-a wykorzystujące dane pobrane z mapy currencies, która jest pobierana z bazy danych przy aktualizacji.

```Java
void createAccountDialog() {
	JComboBox<String> currenciesComboBox = new JComboBox<>();

	for (Map.Entry<String, String> currency: currencies.entrySet()) {
		if (!userCurrencies.contains(Integer.parseInt(currency.getKey()))) {
			currenciesComboBox.addItem(currency.getValue());
		}
	}

	Object[] message = {
			"Wybierz walutę", currenciesComboBox
	};

	int option = JOptionPane.showConfirmDialog(null, message, "Dodawanie nowego konta",
                                             JOptionPane.OK_CANCEL_OPTION);
```

Po stworzeniu okna, pobieramy wybraną wartość z listy wybieranej i przekazujemy go do funkcji connection.createAccount(), która prześle dane do serwera wykorzystując REST.

```Java
	if (option == JOptionPane.OK_OPTION) {
		for (Map.Entry<String, String> entry : currencies.entrySet()) {
			if (Objects.equals(currenciesComboBox.getSelectedItem(), entry.getValue())) {
				int currencyID = Integer.parseInt(entry.getKey());
				connection.createAccount(login.getLogin(), login.getPasswordHash(), currencyID);
			}
		}
	}
  ```

Na końcu odświeżamy okno wyboru kontaktów uruchamiając ją w nowym wątku, by operacja wykonała się w tle, nie zaburzając funkcjonowania aplikacji.

```Java
	new Thread(this::updateAccounts).start();
}
```

### Serwer

Serwer oczekuje na żądania klientów. W przypadku wystąpienia takiego zdarzenia pobiera parametry i konwertuje je na odpowiednie typy danych. Warto zauważyć, że każda operacja wymaga podania loginu i hasha hasła użytkownika, by zapobiec nieautoryzowanym operacjom. Gdy login zostanie zweryfikowany serwer odpytuje bazę o dane użytkownika i wykonuje bezpośrednią operację dodania nowego konta.

```Java
String login = request.getParameter("login");
String passwordhash = request.getParameter("passwordhash");
int currencyID = Integer.parseInt(request.getParameter("currencyid"));
Login log = data.getLogin(login, passwordhash);
if (log != null) {
	Client client = data.getClient(log.getID());
	data.addAccount(currencyID, client.getID());
}
```

### Baza danych

Procedura otwarcia nowego konta jest realizowana przez biblioteki JPA (Hibernate) i rozpoczyna się od otwarcia nowej sesji i transakcji. Później baza danych odpytywana jest o dostępny nowy identyfikator konta. Na końcu tworzona jest nowa klasa Account z parametrami: walutą podaną w żądaniu, wartością 0 i identyfikatorem klienta. Klasa ta jest następnie zapisywana do bazy. Transakcja zostaje zaakceptowana, a sesja zamknięta. W przypadku wystąpienia jakichkolwiek błędów zagrażającym stabilności bazy, połączenie z nią zostaje zrestartowane, by zapewnić ciągłość funkcjonowania

```Java
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
```


# Podział pracy na osoby

### [@pawelmuller](https://github.com/pawelmuller)

 - Strona logowania i rejestracji użytkowników, haszowanie haseł,
 - Sekcja kont (zakładanie, zmiana nazwy, zamykanie),
 - Tabela historii przelewów,
 - Klasy odpowiedzialne za kolory i fonty,
 - Układ graficzny i kolorystyka aplikacji.
 
### [@Robak132](https://github.com/Robak132)

 - Serwer warstwy logiki biznesowej,
 - Połączenie serwera z bazą danych przy pomocy JPA (Hibernate),
 - Wykonywanie przelewów (także z przewalutowaniem),
 - Możliwość dodawania, usuwania i zmiany nazwy kontaktów,
 - Sekcja lokat (tworzenie, zamykanie),
 - Zmiana loginu i hasła w ustawieniach aplikacji.
 
### [@Kszesiek](https://github.com/Kszesiek)

 - Stworzenie tabeli i relacji w bazie danych,
 - Stworzenie funkcji, procedur i wyzwalaczy w bazie danych,
 - Sekcja kredytów (tworzenie, spłacanie),
 - Testy jednostkowe.
 
# Opis sposobu testowania systemu
### Testy jednostkowe

Skupiliśmy się na testowaniu serwera, ponieważ to właśnie serwer odpowiada za to, aby zapobiec wprowadzaniu do bazy błędnych danych. Sprawdzane są głównie te zmienne, które są wprowadzane przez użytkownika w aplikacji klienckiej ręcznie, a nie poprzez np. slidery czy listę wartości do wyboru, gdzie to programista ma całkowitą kontrolę nad tym, jakie wartości mogą zostać wybrane.

Można wyróżnić dwa główne rodzaje testów:
- testowanie wartości tekstowych przesyłanych do bazy
- testowanie wartości liczbowych przysyłanych do bazy

W przypadku wartości tekstowych sprawdzamy, czy napis nie jest pusty i nie przekracza maksymalnej długości. W przypadku loginu sprawdzamy również, czy nie zawiera znaków spoza zakresu znaków ASCII.

Jeśli chodzi o sprawdzanie wartości liczbowych, weryfikowane jest, czy przesyłana wartość jest liczbą (konieczność ta wynika z faktu przesyłania danych jako JSON) oraz, czy należy do właściwego jej zakresu.

Przy wykonywaniu testów oczekujemy, że funkcje bazodanowe zwrócą kod błędu SC_BAD_REQUEST, jeśli oczekujemy błędu przy wykonywaniu testu, lub SC_SERVICE_UNAVAILABLE, jeśli oczekujemy, że test wykona się poprawnie. Takie podejście jest umotywowane tym, aby podczas testu nie łączyć się z bazą, a jedynie sprawdzać, czy warunki nałożone na wszystkie zmienne są spełnione. Wszystkie testowane funkcje kończą się próbą połączenia z bazą danych, więc w przypadku niepowodzenia zwrócą taki kod błędu.
<writing>