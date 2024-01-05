package benicio.solucoes.baratotarefas.model;

public class FileModel {
    String nomeReal, nomeBancoDeDados, nomeExibicao, idTarefa;
    String fileLink;

    public FileModel() {
    }

    public FileModel(String nomeReal, String nomeBancoDeDados, String nomeExibicao, String fileLink, String idTarefa) {
        this.nomeReal = nomeReal;
        this.nomeBancoDeDados = nomeBancoDeDados;
        this.nomeExibicao = nomeExibicao;
        this.fileLink = fileLink;
        this.idTarefa = idTarefa;
    }

    public String getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(String idTarefa) {
        this.idTarefa = idTarefa;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
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

    public String getfileLink() {
        return fileLink;
    }

    public void setfileLink(String fileLink) {
        this.fileLink = fileLink;
    }
}
