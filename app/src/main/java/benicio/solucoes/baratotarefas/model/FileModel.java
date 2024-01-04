package benicio.solucoes.baratotarefas.model;

import android.net.Uri;

public class FileModel {
    String nomeReal, nomeBancoDeDados, nomeExibicao;
    Uri uriArquivo;

    public FileModel() {
    }

    public FileModel(String nomeReal, String nomeBancoDeDados, String nomeExibicao, Uri uriArquivo) {
        this.nomeReal = nomeReal;
        this.nomeBancoDeDados = nomeBancoDeDados;
        this.nomeExibicao = nomeExibicao;
        this.uriArquivo = uriArquivo;
    }

    public String getNomeBancoDeDados() {
        return nomeBancoDeDados;
    }

    public void setNomeBancoDeDados(String nomeBancoDeDados) {
        this.nomeBancoDeDados = nomeBancoDeDados;
    }

    public String getNomeReal() {
        return nomeReal;
    }

    public void setNomeReal(String nomeReal) {
        this.nomeReal = nomeReal;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public void setNomeExibicao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public Uri getUriArquivo() {
        return uriArquivo;
    }

    public void setUriArquivo(Uri uriArquivo) {
        this.uriArquivo = uriArquivo;
    }
}
