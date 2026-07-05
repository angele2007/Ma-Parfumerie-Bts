package parfumerie.modeles;

public class Fournisseur {

    private int idFournisseur;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;

    public Fournisseur() {}

    public Fournisseur(int idFournisseur, String nom, String telephone,
                       String email, String adresse) {
        this.idFournisseur = idFournisseur;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Getters & Setters
    public int getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return nom + " - " + telephone;
    }
}