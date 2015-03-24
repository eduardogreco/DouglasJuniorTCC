package br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary;

/**
 *
 * @author eduardo
 */
public class AuxRepositoryManyMetricas {

    private String repositoryName;
    private int amountStars;
    private int amountContributors;
    private int ageInMonths;
    private int amountOpenIssue;
    private int amountCloseIssue;
    private int amountCommentsIssues;
    private int amountCommits;
    private int amountFollowersUsersProject;
    private Long amountCommentsClosedPullRequest;

    public AuxRepositoryManyMetricas() {
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public int getAmountStars() {
        return amountStars;
    }

    public void setAmountStars(int amountStars) {
        this.amountStars = amountStars;
    }

    public int getAmountContributors() {
        return amountContributors;
    }

    public void setAmountContributors(int amountContributors) {
        this.amountContributors = amountContributors;
    }

    public int getAgeInMonths() {
        return ageInMonths;
    }

    public void setAgeInMonths(int ageInMonths) {
        this.ageInMonths = ageInMonths;
    }

    public int getAmountOpenIssue() {
        return amountOpenIssue;
    }

    public void setAmountOpenIssue(int amountOpenIssue) {
        this.amountOpenIssue = amountOpenIssue;
    }

    public int getAmountCloseIssue() {
        return amountCloseIssue;
    }

    public void setAmountCloseIssue(int amountCloseIssue) {
        this.amountCloseIssue = amountCloseIssue;
    }

    public int getAmountCommentsIssues() {
        return amountCommentsIssues;
    }

    public void setAmountCommentsIssues(int amountCommentsIssues) {
        this.amountCommentsIssues = amountCommentsIssues;
    }

    public int getAmountCommits() {
        return amountCommits;
    }

    public void setAmountCommits(int amountCommits) {
        this.amountCommits = amountCommits;
    }

    public int getAmountFollowersUsersProject() {
        return amountFollowersUsersProject;
    }

    public void setAmountFollowersUsersProject(int amountFollowersUsersProject) {
        this.amountFollowersUsersProject = amountFollowersUsersProject;
    }

    public Long getAmountCommentsClosedPullRequest() {
        return amountCommentsClosedPullRequest;
    }

    public void setAmountCommentsClosedPullRequest(Long amountCommentsClosedPullRequest) {
        this.amountCommentsClosedPullRequest = amountCommentsClosedPullRequest;
    }

    @Override
    public String toString() {
        return repositoryName + ";" + amountStars + ";" + amountContributors + ";" + ageInMonths + ";" + amountOpenIssue + ";" + amountCloseIssue + ";" + amountCommentsIssues + ";" + amountCommits + ";" + amountFollowersUsersProject + ";" + amountCommentsClosedPullRequest;
    }
}
