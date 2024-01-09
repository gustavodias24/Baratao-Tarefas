package benicio.solucoes.baratotarefas.model;

import java.util.ArrayList;
import java.util.List;

public class CheckModel {
    String id, idTarefa, checkNome, comentario;
    Boolean isChecked;

    List<FileModel> filesDoCheck = new ArrayList<>();
    List<CheckModel> subChecks = new ArrayList<>();


    public CheckModel() {
    }

    public CheckModel(String id, String idTarefa, String checkNome, String comentario, Boolean isChecked) {
        this.id = id;
        this.idTarefa = idTarefa;
        this.checkNome = checkNome;
        this.comentario = comentario;
        this.isChecked = isChecked;
    }



    public List<CheckModel> getSubChecks() {
        return subChecks;
    }


    public String getIdTarefa() {
        return idTarefa;
    }


    public List<FileModel> getfilesDoCheck() {
        return filesDoCheck;
    }

    public void setId(String id) {
        this.id = id;
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
