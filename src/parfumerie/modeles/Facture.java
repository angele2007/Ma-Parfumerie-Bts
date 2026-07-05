package parfumerie.modeles;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Facture {

    // ── Enum Statut avec accents (correspond exactement aux valeurs MySQL) ───
    public enum Statut {
        Payée, Impayée, Annulée
    }

    private int        idFacture;
    private String     numeroFacture;
    private LocalDate  dateFacture;
    private String     nomClient;
    private String     telephoneClient;
    private BigDecimal montantTotal;
    private BigDecimal remise;
    private BigDecimal montantNet;
    private Statut     statut;

    public Facture() {}

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int        getIdFacture()               { return idFacture; }
    public void       setIdFacture(int v)           { this.idFacture = v; }

    public String     getNumeroFacture()            { return numeroFacture; }
    public void       setNumeroFacture(String v)    { this.numeroFacture = v; }

    public LocalDate  getDateFacture()              { return dateFacture; }
    public void       setDateFacture(LocalDate v)   { this.dateFacture = v; }

    public String     getNomClient()                { return nomClient; }
    public void       setNomClient(String v)        { this.nomClient = v; }

    public String     getTelephoneClient()          { return telephoneClient; }
    public void       setTelephoneClient(String v)  { this.telephoneClient = v; }

    public BigDecimal getMontantTotal()              { return montantTotal; }
    public void       setMontantTotal(BigDecimal v)  { this.montantTotal = v; }

    public BigDecimal getRemise()                    { return remise; }
    public void       setRemise(BigDecimal v)        { this.remise = v; }

    public BigDecimal getMontantNet()                { return montantNet; }
    public void       setMontantNet(BigDecimal v)    { this.montantNet = v; }

    public Statut     getStatut()                   { return statut; }
    public void       setStatut(Statut v)           { this.statut = v; }

    @Override
    public String toString() { return numeroFacture + " – " + nomClient; }
}