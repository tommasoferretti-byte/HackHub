/*
Prima iterazione - HackHub
 */
package com.hackhub;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HackHub {

    public static void main(String[] args) {
        TeamService team = new TeamService();
        System.out.println("=== HackHub - Parte 2: team ===\n");

        Utente alice = new Utente("alice", "alice@mail.it", "pw1", "Alice", "Rossi");
        Utente bob   = new Utente("bob",   "bob@mail.it",   "pw2", "Bob",   "Bianchi");

        Team t = team.creaTeam(alice, "Byte Squad", "Team di prova", 4);
        System.out.println("[Crea team] " + t + " | leader = " + t.getLeader().getUsername());

        t.aggiungiMembro(bob);
        System.out.println("Membri: " + t.contaMembri() + " -> " + t.getMembri());

        team.lasciaTeam(bob);
        System.out.println("[Lascia team] membri = " + t.getMembri());
    }
}


enum RuoloUtente { VISITATORE, UTENTE, MEMBRO_TEAM, TEAM_LEADER, ORGANIZZATORE, GIUDICE, MENTORE }

enum StatoTeam { ATTIVO, COMPLETO, ELIMINATO }


class Utente {
    private UUID idUtente = UUID.randomUUID();
    private String username;
    private String email;
    private String passwordHash;
    private String nome;
    private String cognome;
    private LocalDate dataRegistrazione = LocalDate.now();
    private RuoloUtente ruolo = RuoloUtente.UTENTE;

    private Team team;

    protected Utente() { }

    Utente(String username, String email, String passwordHash, String nome, String cognome) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nome = nome;
        this.cognome = cognome;
    }

    boolean isLibero() { return team == null; }
    boolean appartieneATeam() { return team != null; }

    UUID getIdUtente() { return idUtente; }
    String getUsername() { return username; }
    String getEmail() { return email; }
    String getNome() { return nome; }
    String getCognome() { return cognome; }
    LocalDate getDataRegistrazione() { return dataRegistrazione; }
    RuoloUtente getRuolo() { return ruolo; }
    void setRuolo(RuoloUtente ruolo) { this.ruolo = ruolo; }
    Team getTeam() { return team; }
    void setTeam(Team team) { this.team = team; }

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

class Team {
    private UUID idTeam = UUID.randomUUID();
    private String nomeTeam;
    private String descrizione;
    private LocalDate dataCreazione = LocalDate.now();
    private int maxMembri;
    private StatoTeam stato = StatoTeam.ATTIVO;

    private final List<Utente> membri = new ArrayList<>();

    private Utente leader;

    protected Team() { }

    Team(String nomeTeam, String descrizione, int maxMembri) {
        this.nomeTeam = nomeTeam;
        this.descrizione = descrizione;
        this.maxMembri = maxMembri;
    }

    boolean verificaDisponibilitaPosti() {
        return stato != StatoTeam.ELIMINATO && membri.size() < maxMembri;
    }

    void aggiungiMembro(Utente membro) {
        if (!verificaDisponibilitaPosti()) {
            throw new TeamCompletoException("Team '" + nomeTeam + "' pieno o eliminato");
        }
        membri.add(membro);
        membro.setTeam(this);
        if (membri.size() == maxMembri) stato = StatoTeam.COMPLETO;
    }

    void rimuoviMembro(Utente membro) {
        membri.remove(membro);
        membro.setTeam(null);
        membro.setRuolo(RuoloUtente.UTENTE);
        if (membri.isEmpty()) stato = StatoTeam.ELIMINATO;
        else if (stato == StatoTeam.COMPLETO) stato = StatoTeam.ATTIVO;
    }

    int contaMembri() { return membri.size(); }

    void eliminaTeam() {
        for (Utente m : new ArrayList<>(membri)) rimuoviMembro(m);
        stato = StatoTeam.ELIMINATO;
    }

    UUID getIdTeam() { return idTeam; }
    String getNomeTeam() { return nomeTeam; }
    void setNomeTeam(String n) { this.nomeTeam = n; }
    String getDescrizione() { return descrizione; }
    void setDescrizione(String d) { this.descrizione = d; }
    int getMaxMembri() { return maxMembri; }
    StatoTeam getStato() { return stato; }
    List<Utente> getMembri() { return List.copyOf(membri); }
    Utente getLeader() { return leader; }
    void setLeader(Utente leader) { this.leader = leader; }

    @Override public String toString() { return "Team(" + nomeTeam + ")"; }
}

class TeamService {

    Team creaTeam(Utente creatore, String nome, String descrizione, int maxMembri) {
        if (creatore.appartieneATeam()) {
            throw new GiaInTeamException(creatore.getUsername() + " appartiene gia' a un team");
        }
        Team t = new Team(nome, descrizione, maxMembri);
        t.setLeader(creatore);
        t.aggiungiMembro(creatore);
        creatore.setRuolo(RuoloUtente.TEAM_LEADER);
        return t;
    }

    void lasciaTeam(Utente utente) {
        Team t = utente.getTeam();
        if (t == null) throw new HackHubException(utente.getUsername() + " non appartiene a nessun team");
        t.rimuoviMembro(utente);
    }
}


class HackHubException extends RuntimeException {
    HackHubException(String messaggio) { super(messaggio); }
}
class GiaInTeamException extends HackHubException {
    GiaInTeamException(String m) { super(m); }
}
class TeamCompletoException extends HackHubException {
    TeamCompletoException(String m) { super(m); }
}