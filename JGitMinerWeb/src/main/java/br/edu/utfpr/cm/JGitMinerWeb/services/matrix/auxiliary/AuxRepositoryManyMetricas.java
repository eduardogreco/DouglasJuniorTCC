package br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary;

/**
 *
 * @author eduardo
 */
public class AuxRepositoryManyMetricas {

    private String repositoryName;
    private String amountStars;
    private String amountContributors;
    private String ageInMonths;
    private String amountOpenIssue;
    private String amountCloseIssue;
    private String amountCommentsIssues;
    private String averageAmountCommentsIssues;
    private String amountCommits;
    private String amountCommitsPullRequest;
    private String amountFollowersUsersProject;
    private String amountCommentsClosedPullRequest;
    private String amountCommitInclusionTest;
    private String averageAmountCommitInclusionTest;
    private String averagePullRequestSubmittedByTheSameUser;
    private String medianPullRequestSubmittedByTheSameUser;
    private String greaterAmountOfPullRequestByTheSameUser;
    private String lessAmountOfPullRequestByTheSameUser;

    public AuxRepositoryManyMetricas() {
        amountStars = "not_calculated";
        amountContributors = "not_calculated";
        ageInMonths = "not_calculated";
        amountOpenIssue = "not_calculated";
        amountCloseIssue = "not_calculated";
        amountCommentsIssues = "not_calculated";
        averageAmountCommentsIssues = "not_calculated";
        amountCommits = "not_calculated";
        amountCommitsPullRequest = "not_calculated";
        amountFollowersUsersProject = "not_calculated";
        amountCommentsClosedPullRequest = "not_calculated";
        amountCommitInclusionTest = "not_calculated";
        averageAmountCommitInclusionTest = "not_calculated";
        averagePullRequestSubmittedByTheSameUser = "not_calculated";
        medianPullRequestSubmittedByTheSameUser = "not_calculated";
        greaterAmountOfPullRequestByTheSameUser = "not_calculated";
        lessAmountOfPullRequestByTheSameUser = "not_calculated";
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getAmountStars() {
        return amountStars;
    }

    public void setAmountStars(String amountStars) {
        this.amountStars = amountStars;
    }

    public String getAmountContributors() {
        return amountContributors;
    }

    public void setAmountContributors(String amountContributors) {
        this.amountContributors = amountContributors;
    }

    public String getAgeInMonths() {
        return ageInMonths;
    }

    public void setAgeInMonths(String ageInMonths) {
        this.ageInMonths = ageInMonths;
    }

    public String getAmountOpenIssue() {
        return amountOpenIssue;
    }

    public void setAmountOpenIssue(String amountOpenIssue) {
        this.amountOpenIssue = amountOpenIssue;
    }

    public String getAmountCloseIssue() {
        return amountCloseIssue;
    }

    public void setAmountCloseIssue(String amountCloseIssue) {
        this.amountCloseIssue = amountCloseIssue;
    }

    public String getAmountCommentsIssues() {
        return amountCommentsIssues;
    }

    public void setAmountCommentsIssues(String amountCommentsIssues) {
        this.amountCommentsIssues = amountCommentsIssues;
    }

    public String getAverageAmountCommentsIssues() {
        return averageAmountCommentsIssues;
    }

    public void setAverageAmountCommentsIssues(String averageAmountCommentsIssues) {
        this.averageAmountCommentsIssues = averageAmountCommentsIssues;
    }

    public String getAmountCommits() {
        return amountCommits;
    }

    public void setAmountCommits(String amountCommits) {
        this.amountCommits = amountCommits;
    }

    public String getAmountFollowersUsersProject() {
        return amountFollowersUsersProject;
    }

    public void setAmountFollowersUsersProject(String amountFollowersUsersProject) {
        this.amountFollowersUsersProject = amountFollowersUsersProject;
    }

    public String getAmountCommentsClosedPullRequest() {
        return amountCommentsClosedPullRequest;
    }

    public void setAmountCommentsClosedPullRequest(String amountCommentsClosedPullRequest) {
        this.amountCommentsClosedPullRequest = amountCommentsClosedPullRequest;
    }

    public String getAmountCommitInclusionTest() {
        return amountCommitInclusionTest;
    }

    public void setAmountCommitInclusionTest(String amountCommitInclusionTest) {
        this.amountCommitInclusionTest = amountCommitInclusionTest;
    }

    public String getAverageAmountCommitInclusionTest() {
        return averageAmountCommitInclusionTest;
    }

    public void setAverageAmountCommitInclusionTest(String averageAmountCommitInclusionTest) {
        this.averageAmountCommitInclusionTest = averageAmountCommitInclusionTest;
    }

    public String getAmountCommitsPullRequest() {
        return amountCommitsPullRequest;
    }

    public void setAmountCommitsPullRequest(String amountCommitsPullRequest) {
        this.amountCommitsPullRequest = amountCommitsPullRequest;
    }

    public String getAveragePullRequestSubmittedByTheSameUser() {
        return averagePullRequestSubmittedByTheSameUser;
    }

    public void setAveragePullRequestSubmittedByTheSameUser(String averagePullRequestSubmittedByTheSameUser) {
        this.averagePullRequestSubmittedByTheSameUser = averagePullRequestSubmittedByTheSameUser;
    }

    public String getMedianPullRequestSubmittedByTheSameUser() {
        return medianPullRequestSubmittedByTheSameUser;
    }

    public void setMedianPullRequestSubmittedByTheSameUser(String medianPullRequestSubmittedByTheSameUser) {
        this.medianPullRequestSubmittedByTheSameUser = medianPullRequestSubmittedByTheSameUser;
    }

    public String getGreaterAmountOfPullRequestByTheSameUser() {
        return greaterAmountOfPullRequestByTheSameUser;
    }

    public void setGreaterAmountOfPullRequestByTheSameUser(String greaterAmountOfPullRequestByTheSameUser) {
        this.greaterAmountOfPullRequestByTheSameUser = greaterAmountOfPullRequestByTheSameUser;
    }

    public String getLessAmountOfPullRequestByTheSameUser() {
        return lessAmountOfPullRequestByTheSameUser;
    }

    public void setLessAmountOfPullRequestByTheSameUser(String lessAmountOfPullRequestByTheSameUser) {
        this.lessAmountOfPullRequestByTheSameUser = lessAmountOfPullRequestByTheSameUser;
    }

    @Override
    public String toString() {
        return repositoryName + ";" + amountStars + ";" + amountContributors + ";" + ageInMonths + ";" + amountOpenIssue + ";" + amountCloseIssue + ";" + amountCommentsIssues + ";" + averageAmountCommentsIssues + ";" + amountCommits + ";" + amountCommitsPullRequest + ";" + amountFollowersUsersProject + ";" + amountCommentsClosedPullRequest + ";" + amountCommitInclusionTest + ";" + averageAmountCommitInclusionTest + ";" + averagePullRequestSubmittedByTheSameUser + ";" + medianPullRequestSubmittedByTheSameUser + ";" + greaterAmountOfPullRequestByTheSameUser + ";" + lessAmountOfPullRequestByTheSameUser;
    }
}
