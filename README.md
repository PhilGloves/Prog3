# Prog3
Progetto Programmazione 3

# Applicazione di Posta Elettronica in Java

Si sviluppi un'applicazione Java che implementi un servizio di posta elettronica organizzato con un mail server che gestisce le caselle di posta elettronica degli utenti e i mail client necessari per permettere agli utenti di accedere alle proprie caselle di posta.

## Descrizione del Progetto

Il mail server gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza utilizzando file (txt o binari, a vostra scelta, non si possono usare database) per memorizzare i messaggi in modo permanente.

Il mail server ha un'interfaccia grafica sulla quale viene visualizzato il log delle azioni effettuate dai mail clients e degli eventi che occorrono durante l'interazione tra i client e il server.

Per esempio: apertura/chiusura di una connessione tra mail client e server, invio di messaggi da parte di un client, ricezione di messaggi da parte di un client, errori nella consegna di messaggi.

NB: NON fare log di eventi locali al client come per esempio il fatto che ha schiacciato un bottone, aperto una finestra o simili in quanto non sono di pertinenza del server.

### Struttura delle Caselle di Posta

Una casella di posta elettronica contiene:

- Nome dell'account di mail associato alla casella postale (es.giorgio@mia.mail.com).
- Lista (eventualmente vuota) di messaggi. I messaggi di posta elettronica sono istanze di una classe Email che specifica ID, mittente, destinatario/i, argomento, testo e data di spedizione del messaggio.

### Interfaccia del Mail Client

Il mail client, associato a un particolare account di posta elettronica, ha un'interfaccia grafica così caratterizzata:

L'interfaccia permette di:

- Creare e inviare un messaggio a uno o più destinatari (destinatari multipli di un solo messaggio di posta elettronica).
- Leggere i messaggi della casella di posta.
- Rispondere a un messaggio ricevuto, in Reply (al mittente del messaggio) e/o in Reply-all (al mittente e a tutti i destinatari del messaggio ricevuto).
- Girare (forward) un messaggio a uno o più account di posta elettronica.
- Rimuovere un messaggio dalla casella di posta.

L'interfaccia mostra sempre la lista aggiornata dei messaggi in casella e, quando arriva un nuovo messaggio, notifica l'utente attraverso una finestra di dialogo.

### Requisiti Tecnici

Per la dimostrazione si assuma di avere 3 utenti di posta elettronica che comunicano tra loro. Si progetti però il sistema in modo da renderlo scalabile a molti utenti.

- L'applicazione deve essere sviluppata in Java (JavaFXML) e basata su architettura MVC, con Controller + viste e Model, seguendo i principi del pattern Observer Observable. Non deve esserci comunicazione diretta tra viste e model: ogni tipo di comunicazione tra questi due livelli deve essere mediato dal controller o supportata dal pattern Observer Observable.
- L'applicazione deve permettere all'utente di correggere eventuali input errati.
- I client e il server dell'applicazione devono parallelizzare le attività che non necessitano di esecuzione sequenziale e gestire gli eventuali problemi di accesso a risorse in mutua esclusione.
- L'applicazione deve essere distribuita attraverso l'uso di Socket Java.

### Requisiti dell'Interfaccia Utente

L'interfaccia utente deve essere:

- Comprensibile e trasparente.
- Ragionevolmente efficiente per permettere all'utente di eseguire le operazioni con un numero minimo di click e di inserimenti di dati.
- Deve essere implementata utilizzando JavaFXML e, se necessario, Thread java. Non è richiesto, ma consigliato, l'uso di Java Beans, properties e binding di properties.

### Note Finali

- Il progetto SW può essere svolto in gruppo (max 3 persone) o individualmente.
- La discussione potrà essere fatta nelle date di appello orale dell'insegnamento, che saranno distribuite su tutto l'Anno Accademico.
- Il voto finale si ottiene come media del voto della prova orale e della discussione di laboratorio (i due voti hanno ugual peso nella media).
- Il voto finale deve essere registrato entro fine settembre 2022, data oltre la quale non è possibile mantenere i voti parziali. Leggere il regolamento d'esame sulla pagina web dell'insegnamento per ulteriori dettagli.

# Modifiche

### Cambiamenti nei File .java

- Parto con la cartella `prog_client`. `Client.java` inizio a cambiare `replay` in `reply` e in tutte le altre classi nella stessa cartella (`ControllerMain`, `ControllerStart`, ecc.).
- Cartella `prog_server`, ho cambiato tutti i `Replay` con `Reply` e `isReaded` (o `Readed`) in `isRead` (o `Read`). `Sended` in `sent` e altri typo.
- Cartella `progetto_server`, stesso cambiamento.
- Cartella `progGrafica`, uguale.

### Cambio dei Dati in `prog_server/data`

- Cambio gli utenti nel file `users.json`.
- Cancellare tutte le cartelle degli utenti presenti (che contengono ognuno i file `inbox` e `sent`) e le rimpiazzo con i nuovi utenti creati.
- Provo ad aprire e se funziona mando qualche mail in giro.

### Altri Cambiamenti

- Dovrei cambiare anche il `conf.json` con ip e porte diverse?
- Tolgo qualche metodo che non è stato utilizzato o non è necessario per il progetto, come i metodi per la registrazione che non sono stati utilizzati in un po' di classi (da fare).
