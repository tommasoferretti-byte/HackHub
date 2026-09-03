/*
Prima iterazione - HackHub
 */
package com.hackhub;

import java.time.LocalDate;
import java.util.UUID;

public class HackHub {

    public static void main(String[] args) {
        System.out.println("=== HackHub - Parte 1: utenti e ruoli ===\n");

        Utente alice = new Utente("alice", "alice@mail.it", "pw1", "Alice", "Rossi");
        Utente bob   = new Utente("bob",   "bob@mail.it",   "pw2", "Bob",   "Bianchi");
        Organizzatore org = new Organizzatore("org", "org@mail.it", "pw3", "Olga", "Verdi");
        org.setRuolo(RuoloUtente.ORGANIZZATORE);

        System.out.println("Utenti creati: " + alice + ", " + bob + ", " + org);
        System.out.println("Ruolo di " + org + ": " + org.getRuolo());
    }
}

enum RuoloUtente { VISITATORE, UTENTE, MEMBRO_TEAM, TEAM_LEADER, ORGANIZZATORE, GIUDICE, MENTORE }

class Utente {

    private UUID idUtente = UUID.randomUUID();
    private String username;
    private String email;
    private String passwordHash;
    private String nome;
    private String cognome;
    private LocalDate dataRegistrazione = LocalDate.now();
    private RuoloUtente ruolo = RuoloUtente.UTENTE;

    protected Utente() { }

    Utente(String username, String email, String passwordHash, String nome, String cognome) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nome = nome;
        this.cognome = cognome;
    }

    UUID getIdUtente() { return idUtente; }
    String getUsername() { return username; }
    String getEmail() { return email; }
    String getNome() { return nome; }
    String getCognome() { return cognome; }
    LocalDate getDataRegistrazione() { return dataRegistrazione; }
    RuoloUtente getRuolo() { return ruolo; }
    void setRuolo(RuoloUtente ruolo) { this.ruolo = ruolo; }

    @Override public String toString() { return username; }
}

class MembroDelTeam extends Utente {
    private LocalDate dataEntrataTeam = LocalDate.now();
    protected MembroDelTeam() { }
    MembroDelTeam(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
    LocalDate getDataEntrataTeam() { return dataEntrataTeam; }
}

class TeamLeader extends MembroDelTeam {
    protected TeamLeader() { }
    TeamLeader(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
}

class MembroDelloStaff extends Utente {
    protected MembroDelloStaff() { }
    MembroDelloStaff(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
}

class Organizzatore extends MembroDelloStaff {
    protected Organizzatore() { }
    Organizzatore(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
}

class Giudice extends MembroDelloStaff {
    protected Giudice() { }
    Giudice(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
}

class Mentore extends MembroDelloStaff {
    protected Mentore() { }
    Mentore(String u, String e, String p, String n, String c) { super(u, e, p, n, c); }
}