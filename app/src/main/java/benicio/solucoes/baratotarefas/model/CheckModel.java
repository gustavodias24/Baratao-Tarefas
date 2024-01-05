package benicio.solucoes.baratotarefas.model;

public class CheckModel {
    String id, checkNome;
    Boolean isChecked;

    public CheckModel() {
    }

    public CheckModel(String id, String checkNome, Boolean isChecked) {
        this.id = id;
        this.checkNome = checkNome;
        this.isChecked = isChecked;
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
