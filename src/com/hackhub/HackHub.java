package com.hackhub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class HackHub {

    public static void main(String[] args) {
        TeamService team = new TeamService();
        InvitoService inviti = new InvitoService();
        HackathonService hackathon = new HackathonService();
        System.out.println("=== HackHub - hackathon e iscrizioni ===\n");

        Utente alice = new Utente("alice", "alice@mail.it", "pw1", "Alice", "Rossi");
        Utente bob   = new Utente("bob",   "bob@mail.it",   "pw2", "Bob",   "Bianchi");
        Organizzatore org = new Organizzatore("org", "org@mail.it", "pw3", "Olga", "Verdi");

        org.setRuolo(RuoloUtente.ORGANIZZATORE);

        Team t = team.creaTeam(alice, "Byte Squad", "Team di prova", 4);
        Invito invito = team.invita(t, alice, bob);
        inviti.accetta(invito);
        System.out.println("[Team] " + t + " -> membri " + t.getMembri());

        Hackathon h = hackathon.creaHackathon(org, "AI Challenge", "Regolamento...",
                LocalDateTime.now().plusDays(7), LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12), "Milano", 1000.0, 4, 10);
        System.out.println("[Crea hackathon] " + h + " (stato = " + h.getStato() + ")");

        Iscrizione iscr = hackathon.iscriveTeam(t, h);
        System.out.println("[Iscrive team] " + iscr + " (stato = " + iscr.getStato() + ")");
        System.out.println("Iscritti hackathon = " + h.getNumeroIscritti());

    }
}


enum RuoloUtente { VISITATORE, UTENTE, MEMBRO_TEAM, TEAM_LEADER, ORGANIZZATORE, GIUDICE, MENTORE }
enum StatoTeam { ATTIVO, COMPLETO, ELIMINATO }
enum StatoIscrizione { IN_ATTESA, CONFERMATA, RIFIUTATA, ANNULLATA }
enum StatoHackathon { PIANIFICATO, APERTO, IN_CORSO, CHIUSO, ANNULLATO }



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

    private final List<Invito> invitiRicevuti = new ArrayList<>();

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

    List<Invito> visualizzaInvitiRicevuti() { return List.copyOf(invitiRicevuti); }
    void aggiungiInvitoRicevuto(Invito invito) { invitiRicevuti.add(invito); }

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
    private final List<Invito> inviti = new ArrayList<>();
    private final List<Iscrizione> iscrizioni = new ArrayList<>();
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
    List<Invito> getInviti() { return inviti; }
    Utente getLeader() { return leader; }
    void setLeader(Utente leader) { this.leader = leader; }

    @Override public String toString() { return "Team(" + nomeTeam + ")"; }
}

interface StatoInvito {
    String nome();
    void accetta(Invito invito);
    void rifiuta(Invito invito);
}


class InAttesaStato implements StatoInvito {
    public String nome() { return "IN_ATTESA"; }
    public void accetta(Invito invito) { invito.setStato(new AccettatoStato()); }
    public void rifiuta(Invito invito) { invito.setStato(new RifiutatoStato()); }
}


class AccettatoStato implements StatoInvito {
    public String nome() { return "ACCETTATO"; }
    public void accetta(Invito i) { throw new InvitoNonValidoException("Invito gia' accettato"); }
    public void rifiuta(Invito i) { throw new InvitoNonValidoException("Invito gia' accettato"); }
}

class RifiutatoStato implements StatoInvito {
    public String nome() { return "RIFIUTATO"; }
    public void accetta(Invito i) { throw new InvitoNonValidoException("Invito gia' rifiutato"); }
    public void rifiuta(Invito i) { throw new InvitoNonValidoException("Invito gia' rifiutato"); }
}

class ScadutoStato implements StatoInvito {
    public String nome() { return "SCADUTO"; }
    public void accetta(Invito i) { throw new InvitoNonValidoException("Invito scaduto"); }
    public void rifiuta(Invito i) { throw new InvitoNonValidoException("Invito scaduto"); }
}


class Invito {
    private UUID idInvito = UUID.randomUUID();
    private StatoInvito stato = new InAttesaStato();
    private LocalDate dataInvio = LocalDate.now();
    private LocalDateTime dataScadenza;

    private Utente mittente;
    private Utente destinatario;
    private Team team;

    protected Invito() { }

    Invito(Utente mittente, Utente destinatario, Team team, LocalDateTime dataScadenza) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.team = team;
        this.dataScadenza = dataScadenza;
    }


    void accetta() { stato.accetta(this); }
    void rifiuta() { stato.rifiuta(this); }

    boolean verificaScadenza() { return LocalDateTime.now().isAfter(dataScadenza); }

    UUID getIdInvito() { return idInvito; }
    StatoInvito getStato() { return stato; }
    void setStato(StatoInvito stato) { this.stato = stato; }
    LocalDateTime getDataScadenza() { return dataScadenza; }
    Utente getMittente() { return mittente; }
    Utente getDestinatario() { return destinatario; }
    Team getTeam() { return team; }

    @Override public String toString() {
        return "Invito(" + mittente + " -> " + destinatario + ", team " + team.getNomeTeam() + ")";
    }
}

class Hackathon {
    private UUID idHackathon = UUID.randomUUID();
    private String nome;
    private String regolamento;
    private LocalDateTime scadenzaIscrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String luogo;
    private double premio;
    private int dimensioneTeam;             // dimensione massima di un team
    private int maxTeam;                     // numero massimo di team iscritti
    private StatoHackathon stato = StatoHackathon.PIANIFICATO;

    private final List<Iscrizione> iscrizioni = new ArrayList<>();
    private Organizzatore organizzatore;

    protected Hackathon() { }

    Hackathon(String nome, String regolamento, LocalDateTime scadenzaIscrizione,
              LocalDate dataInizio, LocalDate dataFine, String luogo,
              double premio, int dimensioneTeam, int maxTeam) {
        this.nome = nome;
        this.regolamento = regolamento;
        this.scadenzaIscrizione = scadenzaIscrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.luogo = luogo;
        this.premio = premio;
        this.dimensioneTeam = dimensioneTeam;
        this.maxTeam = maxTeam;
    }

    boolean verificaScadenzaIscrizioni() { return LocalDateTime.now().isAfter(scadenzaIscrizione); }
    boolean verificaDisponibilitaPosti() { return iscrizioni.size() < maxTeam; }
    int getNumeroIscritti() { return iscrizioni.size(); }

    UUID getIdHackathon() { return idHackathon; }
    String getNome() { return nome; }
    int getDimensioneTeam() { return dimensioneTeam; }
    int getMaxTeam() { return maxTeam; }
    StatoHackathon getStato() { return stato; }
    void setStato(StatoHackathon stato) { this.stato = stato; }
    List<Iscrizione> getIscrizioni() { return iscrizioni; }
    Organizzatore getOrganizzatore() { return organizzatore; }
    void setOrganizzatore(Organizzatore o) { this.organizzatore = o; }

    @Override public String toString() { return "Hackathon(" + nome + ")"; }
}

class Iscrizione {
    private UUID idIscrizione = UUID.randomUUID();
    private LocalDateTime dataIscrizione = LocalDateTime.now();
    private StatoIscrizione stato = StatoIscrizione.IN_ATTESA;
    private Team team;
    private Hackathon hackathon;

    protected Iscrizione() { }

    Iscrizione(Team team, Hackathon hackathon) {
        this.team = team;
        this.hackathon = hackathon;
    }

    void conferma() { this.stato = StatoIscrizione.CONFERMATA; }

    boolean verificaValidita() {
        return !hackathon.verificaScadenzaIscrizioni() && team.getStato() != StatoTeam.ELIMINATO;
    }

    UUID getIdIscrizione() { return idIscrizione; }
    StatoIscrizione getStato() { return stato; }
    Team getTeam() { return team; }
    Hackathon getHackathon() { return hackathon; }

    @Override public String toString() {
        return "Iscrizione(" + team.getNomeTeam() + " -> " + hackathon.getNome() + ")";
    }
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

    Invito invita(Team team, Utente mittente, Utente destinatario) {
        if (mittente.getTeam() != team) {
            throw new HackHubException("Solo un membro del team puo' invitare");
        }
        Invito invito = new Invito(mittente, destinatario, team,
                LocalDateTime.now().plusDays(7));
        team.getInviti().add(invito);
        destinatario.aggiungiInvitoRicevuto(invito);
        return invito;
    }

    void lasciaTeam(Utente utente) {
        Team t = utente.getTeam();
        if (t == null) throw new HackHubException(utente.getUsername() + " non appartiene a nessun team");
        t.rimuoviMembro(utente);
    }
}


class InvitoService {

    void accetta(Invito invito) {

        if (invito.verificaScadenza()) {
            invito.setStato(new ScadutoStato());
            throw new InvitoNonValidoException("Invito scaduto");
        }
        Utente destinatario = invito.getDestinatario();
        Team team = invito.getTeam();


        if (!destinatario.isLibero()) {
            throw new GiaInTeamException(destinatario.getUsername() + " appartiene gia' a un team");
        }

        if (!team.verificaDisponibilitaPosti()) {
            throw new TeamCompletoException("Team '" + team.getNomeTeam() + "' completo");
        }

        invito.accetta();

        team.aggiungiMembro(destinatario);
        destinatario.setRuolo(RuoloUtente.MEMBRO_TEAM);
    }

    void rifiuta(Invito invito) {
        if (invito.verificaScadenza()) {
            invito.setStato(new ScadutoStato());
            throw new InvitoNonValidoException("Invito scaduto");
        }
        invito.rifiuta();
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
class InvitoNonValidoException extends HackHubException {
    InvitoNonValidoException(String m) { super(m); }
}


