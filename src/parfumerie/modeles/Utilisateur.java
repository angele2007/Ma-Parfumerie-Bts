package parfumerie.modeles;

/**
 *
 * @author Angele
 */
public class Utilisateur {

    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String login;
    private String motDePasse;
    private java.sql.Date dateCreation;

    // Constructeur par défaut
    public Utilisateur() {
    }

    // Constructeur complet
    public Utilisateur(int idUtilisateur, String nom, String prenom, String login, 
                       String motDePasse, java.sql.Date dateCreation) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public java.sql.Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(java.sql.Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + login + ")";
    }
}