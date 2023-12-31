package benicio.solucoes.baratotarefas.model;

public class UserModel {
    String nome, email, login, senha, id;

    public UserModel(String nome, String email, String login, String senha, String id) {
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
        this.id = id;
    }

    public UserModel() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
