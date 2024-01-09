package benicio.solucoes.baratotarefas.model;

public class FileModel{
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



    public String getNomeBancoDeDados() {
        return nomeBancoDeDados;
    }



    public String getNomeReal() {
        return nomeReal;
    }


    public String getNomeExibicao() {
        return nomeExibicao;
    }


    public String getfileLink() {
        return fileLink;
    }

}
