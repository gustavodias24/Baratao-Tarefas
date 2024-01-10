package benicio.solucoes.baratotarefas.model;

import java.util.ArrayList;
import java.util.List;

public class NotificacaoModel {

    String titulo, corpo;
    List<String> listaToken = new ArrayList<>();
    public NotificacaoModel() {
    }

    public NotificacaoModel(String titulo, String corpo) {
        this.titulo = titulo;
        this.corpo = corpo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public List<String> getListaToken() {
        return listaToken;
    }

    public void setListaToken(List<String> listaToken) {
        this.listaToken = listaToken;
    }
}
