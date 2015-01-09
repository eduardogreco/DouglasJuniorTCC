package br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary;

/**
 *
 * @author douglas
 */
public class AuxRepositoryManyMetricas {

    private String repositoryName;
    private int qtdStars;
    private int qtdColabor;
    private int idade;
    private int qtdOpenIssue;
    private int qtdCloseIssue;

    public AuxRepositoryManyMetricas() {

    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public int getQtdStars() {
        return qtdStars;
    }

    public void setQtdStars(int qtdStars) {
        this.qtdStars = qtdStars;
    }

    public int getQtdColabor() {
        return qtdColabor;
    }

    public void setQtdColabor(int qtdColabor) {
        this.qtdColabor = qtdColabor;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getQtdOpenIssue() {
        return qtdOpenIssue;
    }

    public void setQtdOpenIssue(int qtdOpenIssue) {
        this.qtdOpenIssue = qtdOpenIssue;
    }

    public int getQtdCloseIssue() {
        return qtdCloseIssue;
    }

    public void setQtdCloseIssue(int qtdCloseIssue) {
        this.qtdCloseIssue = qtdCloseIssue;
    }

    @Override
    public String toString() {
        return repositoryName + ";" + qtdStars + ";" + qtdColabor + ";" + idade + ";" + qtdOpenIssue + ";" + qtdCloseIssue;
    }
}
