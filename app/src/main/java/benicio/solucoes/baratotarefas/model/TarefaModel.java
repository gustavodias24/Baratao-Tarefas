package benicio.solucoes.baratotarefas.model;

import java.util.ArrayList;
import java.util.List;

public class TarefaModel {
    String id;
    List<UserModel> usuariosResponsaveis = new ArrayList<>();
    List<CheckModel> checks = new ArrayList<>();
    String descri;
    List<FileModel> arquivos = new ArrayList<>();
    List<UserModel> usuariosObservadores = new ArrayList<>();
    String data;
    String hora;

    int status;

    public TarefaModel(String id, List<UserModel> usuariosResponsaveis, List<CheckModel> checks, String descri, List<FileModel> arquivos, List<UserModel> usuariosObservadores, String data, String hora, int status) {
        this.id = id;
        this.usuariosResponsaveis = usuariosResponsaveis;
        this.checks = checks;
        this.descri = descri;
        this.arquivos = arquivos;
        this.usuariosObservadores = usuariosObservadores;
        this.data = data;
        this.hora = hora;
        this.status = status;
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
