package benicio.solucoes.baratotarefas.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaModel {
    String nomeTarefa;
    String id;

    String idCriador;
    List<UserModel> usuariosResponsaveis = new ArrayList<>();
    List<UserModel> usuariosObservadores = new ArrayList<>();

    List<CheckModel> checks = new ArrayList<>();
    String descri;
    List<FileModel> arquivos = new ArrayList<>();
    String data;
    String hora;

    int status;

    public TarefaModel(String nomeTarefa, String id, List<UserModel> usuariosResponsaveis, List<UserModel> usuariosObservadores, List<CheckModel> checks, String descri, List<FileModel> arquivos, String data, String hora, int status, String idCriador) {
        this.nomeTarefa = nomeTarefa;
        this.id = id;
        this.usuariosResponsaveis = usuariosResponsaveis;
        this.usuariosObservadores = usuariosObservadores;
        this.checks = checks;
        this.descri = descri;
        this.arquivos = arquivos;
        this.data = data;
        this.hora = hora;
        this.status = status;
        this.idCriador = idCriador;
    }

    public String getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(String idCriador) {
        this.idCriador = idCriador;
    }

    public String getNomeTarefa() {
        return nomeTarefa;
    }

    public void setNomeTarefa(String nomeTarefa) {
        this.nomeTarefa = nomeTarefa;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public TarefaModel() {
    }

    public void setChecks(List<CheckModel> checks) {
        this.checks = checks;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public List<FileModel> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<FileModel> arquivos) {
        this.arquivos = arquivos;
    }

    public List<UserModel> getUsuariosObservadores() {
        return usuariosObservadores;
    }

    public void setUsuariosObservadores(List<UserModel> usuariosObservadores) {
        this.usuariosObservadores = usuariosObservadores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<UserModel> getUsuariosResponsaveis() {
        return usuariosResponsaveis;
    }

    public void setUsuariosResponsaveis(List<UserModel> usuariosResponsaveis) {
        this.usuariosResponsaveis = usuariosResponsaveis;
    }

    public List<CheckModel> getChecks() {
        return checks;
    }








    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
