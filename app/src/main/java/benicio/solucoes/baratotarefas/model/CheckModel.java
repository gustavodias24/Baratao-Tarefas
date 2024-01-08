package benicio.solucoes.baratotarefas.model;

import java.util.ArrayList;
import java.util.List;

public class CheckModel {
    String id, idTarefa, checkNome;
    Boolean isChecked;

    List<FileModel> filesDoCheck = new ArrayList<>();
    List<CheckModel> subChecks = new ArrayList<>();

    public CheckModel() {
    }

    public CheckModel(String id, String idTarefa, String checkNome, Boolean isChecked) {
        this.id = id;
        this.idTarefa = idTarefa;
        this.checkNome = checkNome;
        this.isChecked = isChecked;
    }

    public List<FileModel> getFilesDoCheck() {
        return filesDoCheck;
    }

    public void setFilesDoCheck(List<FileModel> filesDoCheck) {
        this.filesDoCheck = filesDoCheck;
    }

    public List<CheckModel> getSubChecks() {
        return subChecks;
    }

    public void setSubChecks(List<CheckModel> subChecks) {
        this.subChecks = subChecks;
    }

    public String getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(String idTarefa) {
        this.idTarefa = idTarefa;
    }

    public List<FileModel> getfilesDoCheck() {
        return filesDoCheck;
    }

    public void setfilesDoCheck(List<FileModel> filesDoCheck) {
        this.filesDoCheck = filesDoCheck;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCheckNome(String checkNome) {
        this.checkNome = checkNome;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public String getCheckNome() {
        return checkNome;
    }

    public Boolean getChecked() {
        return isChecked;
    }
}
