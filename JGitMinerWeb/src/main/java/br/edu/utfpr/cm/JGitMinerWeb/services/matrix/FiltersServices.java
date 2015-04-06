/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityComment;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityIssue;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepositoryCommit;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityUser;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxRepositoryManyMetricas;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import br.edu.utfpr.cm.JGitMinerWeb.util.Util;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 * @author eduardogreco
 */
public class FiltersServices extends AbstractMatrixServices {

    public FiltersServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public FiltersServices(GenericDao dao, List<EntityRepository> repository, List<EntityMatrix> matricesToSave, Map params, List<String> selectedFiltersParams, OutLog out) {
        super(dao, repository, matricesToSave, params, selectedFiltersParams, out);
    }

    public Date getBeginDate() {
        return getDateParam("beginDate");
    }

    public Date getEndDate() {
        return getDateParam("endDate");
    }

    List<EntityRepository> repos = getRepositorys();

    @Override
    public void run() {
        System.out.println(params);

        if (repos == null || repos.isEmpty()) {
            throw new IllegalArgumentException("Repository parameter can not be null!");
        }

        if ((getBeginDate() == null) || (getEndDate() == null)) {
            throw new IllegalArgumentException("Enter the date range!");
        }

        List<AuxRepositoryManyMetricas> results = new ArrayList<>();

        for (EntityRepository repo : repos) {
            AuxRepositoryManyMetricas aux = new AuxRepositoryManyMetricas();
            aux.setRepositoryName(repo.getName());

            for (String nameFilter : getSelectedFiltersParams()) {

                switch (nameFilter) {
                    case "STARS":
                        aux.setAmountStars(findStars(repo) + "");
                        break;
                    case "AGE_REPOSITORY":
                        aux.setAgeInMonths(findAgeRepository(repo) + "");
                        break;
                    case "NUMBER_CONTRIBUTOR":
                        aux.setAmountContributors(findNumberOFContributor(repo) + "");
                        break;
                    case "OPEN_ISSUE":
                        aux.setAmountOpenIssue(findIssue(repo, "open") + "");
                        break;
                    case "CLOSE_ISSUE":
                        aux.setAmountCloseIssue(findIssue(repo, "closed") + "");
                        break;
                    case "NUMBER_COMMENT_ISSUE":
                        aux.setAmountCommentsIssues(findCommentIssue(repo) + "");
                        aux.setAverageAmountCommentsIssues(findAverageCommentInIssue(repo) + "");
                        break;
                    case "NUMBER_COMMITS":
                        aux.setAmountCommits(findNumberCommits(repo) + "");
                        break;
                    case "NUMBER_COMMITS_PULL_REQUEST":
                        aux.setAmountCommitsPullRequest(findNumberCommitsPullRequest(repo) + "");
                        break;
                    case "NUMBER_FOLLOWERS_USERS_PROJECT":
                        aux.setAmountFollowersUsersProject(findFollowersUsersProject(repo) + "");
                        break;
                    case "NUMBER_COMMENTS_CLOSED_PULL":
                        aux.setAmountCommentsClosedPullRequest(findNumberCommentsClosedPull(repo) + "");
                        break;
                    case "NUMBER_TEST_CASES":
                        aux.setAmountCommitInclusionTest(findNumberCommitInclusionTest(repo) + "");
                        aux.setAverageAmountCommitInclusionTest(findAverageNumberCommitInclusionTest(repo, Integer.parseInt(aux.getAmountCommitInclusionTest())) + "");
                        break;
                    case "NUMBER_AVERAGE_PULL_REQUEST_SUBMITTED_SAME_USER":
                        aux.setAveragePullRequestSubmittedByTheSameUser(findNumberAveragePullRequestSubmittedByTheSameUser(repo) + "");
                        aux.setMedianPullRequestSubmittedByTheSameUser(findNumberMedianPullRequestSubmittedByTheSameUser(repo) + "");
                        aux.setGreaterAmountOfPullRequestByTheSameUser(findNumberGreaterAmountOfPullRequestSubmittedByTheSameUser(repo) + "");
                        aux.setLessAmountOfPullRequestByTheSameUser(findNumberLessAmountOfPullRequestSubmittedByTheSameUser(repo) + "");
                        break;
                    case "COMMIT_SIZE":
                        aux.setCommitSizeAddedLines(findCommitSize(repo, "additions") + "");
                        aux.setCommitSizeChangedlines(findCommitSize(repo, "changes") + "");
                        aux.setCommitSizeDeletedLines(findCommitSize(repo, "deletions") + "");
                        break;
                    default:
                        System.out.println("Option not found !!!");
                }

            }
            results.add(aux);
        }

        EntityMatrix matrix = new EntityMatrix();
        matrix.setNodes(objectsToNodes(results));
        matricesToSave.add(matrix);

    }

    private int findStars(EntityRepository repository) { // QUANTIDADE DE ESTRELAS DO REPOSITORIO
        String jpql = "SELECT u "
                + "FROM "
                + "EntityUser u JOIN u.starredRepositories r "
                + "WHERE "
                + "r = :repo";
        String[] bdParams = new String[]{
            "repo"
        };

        Object[] bdObjects = new Object[]{
            repository
        };

        List<EntityUser> usersStars = dao.selectWithParams(jpql, bdParams, bdObjects);
        if ((usersStars != null) && (!usersStars.isEmpty())) {
            return usersStars.size();
        }

        return 0;
    }

    private int findAgeRepository(EntityRepository repository) { // IDADE DO REPOSITORIO EM MESES
        Period period = null;
        DateTime dateRepository = new DateTime(repository.getCreatedAt());
        period = new Period(dateRepository, new DateTime(), PeriodType.months().withDaysRemoved());
        return period.getMonths();
    }

    private int findNumberOFContributor(EntityRepository repository) {
        String jpql = "SELECT u "
                + "FROM "
                + "EntityUser u JOIN u.collaboratedRepositories r "
                + "WHERE "
                + "r = :repo";
        String[] bdParams = new String[]{
            "repo"
        };

        Object[] bdObjects = new Object[]{
            repository
        };

        List<EntityUser> users = dao.selectWithParams(jpql, bdParams, bdObjects);

        if ((users != null) && (!users.isEmpty())) {
            return users.size();
        }

        return 0;
    }

    private int findIssue(EntityRepository repository, String state) { // QUANTIDADE DE ISSUE EM UM PERIODO
        String jpql = "SELECT i "
                + "FROM "
                + "EntityIssue i "
                + "WHERE i.repository = :repo "
                + "AND i.stateIssue = :state "
                + "AND i.createdAt BETWEEN :beginDate AND :endDate";

        String[] bdParams = new String[]{
            "repo", "state", "beginDate", "endDate"
        };

        Object[] bdObjects = new Object[]{
            repository, state, getBeginDate(), getEndDate()
        };

        List<EntityIssue> issues = dao.selectWithParams(jpql, bdParams, bdObjects);
        if ((issues != null) && (!issues.isEmpty())) {
            return issues.size();
        }

        return 0;
    }

    private int findCommentIssue(EntityRepository repository) { // QUANTIDADE DE COMENTARIOS EM ISSUE EM UM PERIODO
        String jpql = "SELECT c "
                + "FROM "
                + "EntityIssue i JOIN i.comments c "
                + "WHERE i.repository = :repo "
                + "AND c.createdAt BETWEEN :beginDate AND :endDate";

        String[] bdParams = new String[]{
            "repo", "beginDate", "endDate"
        };

        Object[] bdObjects = new Object[]{
            repository, getBeginDate(), getEndDate()
        };

        List<EntityComment> comments = dao.selectWithParams(jpql, bdParams, bdObjects);
        if ((comments != null) && (!comments.isEmpty())) {
            return comments.size();
        }
        return 0;
    }

    private float findAverageCommentInIssue(EntityRepository repository) { // MÉDIA DE COMENTARIOS EM ISSUES
        int comments = findCommentIssue(repository);

        if (comments == 0) {
            return 0;
        } else {
            int issueOpen = findIssue(repository, "open");
            int issueClosed = findIssue(repository, "closed");

            if ((comments != 0) && (issueOpen != 0 || issueClosed != 0)) {
                return (comments / (issueOpen + issueClosed));
            }
        }
        return 0;
    }

    private int findNumberCommits(EntityRepository repository) { // QUANTIDADE DE COMMITS EM UM PERIODO
        String jpql = "SELECT erc "
                + "FROM "
                + "EntityRepositoryCommit erc "
                + "LEFT JOIN erc.commit c "
                + "LEFT JOIN c.committer cm "
                + "WHERE erc.repository = :repo "
                + "AND cm.dateCommitUser BETWEEN :beginDate AND :endDate";

        String[] bdParams = new String[]{
            "repo", "beginDate", "endDate"
        };

        Object[] bdObjects = new Object[]{
            repository, getBeginDate(), getEndDate()
        };

        List<EntityRepositoryCommit> commits = dao.selectWithParams(jpql, bdParams, bdObjects);
        if ((commits != null) && (!commits.isEmpty())) {
            return commits.size();
        }

        return 0;
    }

    private int findNumberCommitsPullRequest(EntityRepository repository) {
        String jpql = "select rc.commit_id, gc.entitypullrequest_id "
                + "from gitRepositoryCommit rc, gitpullrequest_gitrepositorycommit gc "
                + "where rc.repository_id = " + repository.getId().toString()
                + " and gc.repositorycommits_id = rc.commit_id";

        List<Long> commitsPullRequest = dao.selectNativeWithParams(jpql, null);
        if ((commitsPullRequest != null) && (!commitsPullRequest.isEmpty())) {
            return commitsPullRequest.size();
        }
        return 0;
    }

    private int findFollowersUsersProject(EntityRepository repository) { //QUANTIDADE DE SEGUIDORES DOS USUARIOS DO PROJETO
        int amount = 0;

        String jpql = "SELECT u "
                + "FROM "
                + "EntityUser u JOIN u.collaboratedRepositories r "
                + "WHERE "
                + "r = :repo";

        String[] bdParams = new String[]{
            "repo"
        };

        Object[] bdObjects = new Object[]{
            repository
        };

        List<EntityUser> usersFollowers = dao.selectWithParams(jpql, bdParams, bdObjects);

        for (EntityUser u : usersFollowers) {
            amount += u.getFollowers();
        }

        return amount;
    }

    private Long findNumberCommentsClosedPull(EntityRepository repository) { // QUANTIDADE DE COMENTARIOS EM PULL REQUEST FECHADO
        Long amount = 0L;
        String jpql = "select sum(i.commentscount) "
                + "from gitPullRequest g, gitissue i "
                + "where g.repository_id =  " + repository.getId().toString()
                + " and g.statePullRequest = 'closed' "
                + "and i.number = g.number";

        List<Long> comments = dao.selectNativeWithParams(jpql, null);
        if ((comments != null) && (!comments.isEmpty())) {
            for (Long value : comments) {
                amount = value;
            }
        }
        return amount;
    }

    private int findNumberCommitInclusionTest(EntityRepository repository) { // QUANTIDADE DE COMMITS COM ARQUIVO DE TESTE
        String jpql = "select rc.id as id_pai, count(cf.id) as valor "
                + "from gitRepositoryCommit rc "
                + "left join gitCommitFile cf on rc.id = cf.repositoryCommit_id "
                + "where rc.repository_id = " + repository.getId().toString()
                + " and cf.filename like '%test%' "
                + "group by rc.id, rc.repository_id";

        List<Long> tests = dao.selectNativeWithParams(jpql, null);
        if ((tests != null) && (!tests.isEmpty())) {
            return tests.size();
        }
        return 0;
    }

    private float findAverageNumberCommitInclusionTest(EntityRepository repository, int AmountInclusionTest) { // MEDIA DE COMMITS COM ARQUIVO DE TESTE
        String jpql = "select rc.id as id_pai, count(cf.id) as valor "
                + "from gitRepositoryCommit rc "
                + "left join gitCommitFile cf on rc.id = cf.repositoryCommit_id "
                + "where rc.repository_id = " + repository.getId().toString()
                + " group by rc.id, rc.repository_id";

        List<Long> commits = dao.selectNativeWithParams(jpql, null);
        if ((commits != null) && (!commits.isEmpty()) && AmountInclusionTest != 0) {
            return (commits.size() / AmountInclusionTest);
        }
        return 0;
    }

    private float findNumberAveragePullRequestSubmittedByTheSameUser(EntityRepository repository) {
        int vAmountUser = 0;
        BigDecimal vAmountPullRequest = new BigDecimal(0);

        String amountUser = "SELECT count(*) as quantidade, gp.user_id "
                + "FROM gitPullRequest gp "
                + "where gp.repository_id = " + repository.getId().toString()
                + " and gp.user_id <> 0 "
                + "GROUP BY gp.user_id "
                + "ORDER BY quantidade";

        String amountPullRequest = "select sum(result.quantidade) from (SELECT count(*) as quantidade, gp.user_id "
                + "FROM gitPullRequest gp "
                + "where gp.repository_id = " + repository.getId().toString()
                + "and gp.user_id <> 0 "
                + "GROUP BY gp.user_id "
                + "ORDER BY quantidade) result";

        List<Long> listAmountUser = dao.selectNativeWithParams(amountUser, null);
        if ((listAmountUser != null) && (!listAmountUser.isEmpty())) {
            vAmountUser = listAmountUser.size();
        }

        List<BigDecimal> listAmountPullRequest = dao.selectNativeWithParams(amountPullRequest, null);
        if ((listAmountPullRequest != null) && (!listAmountPullRequest.isEmpty())) {
            for (BigDecimal value : listAmountPullRequest) {
                vAmountPullRequest = value;
            }
        }

        if (vAmountPullRequest != null) {
            return ((BigDecimal) vAmountPullRequest).intValue() / vAmountUser;
        }
        return 0;
    }

    private float findNumberMedianPullRequestSubmittedByTheSameUser(EntityRepository repository) {
        String amountPullRequest = "SELECT count(*) as quantidade "
                + "FROM gitPullRequest gp "
                + "where gp.repository_id = " + repository.getId().toString()
                + " and gp.user_id <> 0 "
                + "GROUP BY gp.user_id "
                + "ORDER BY quantidade";

        List<Long> listAmountUser = dao.selectNativeWithParams(amountPullRequest, null);
        if ((listAmountUser != null) && (!listAmountUser.isEmpty())) {
            if (Util.ePar(listAmountUser.size())) {
                int value1 = (listAmountUser.size() / 2);
                int value2 = (listAmountUser.size() / 2 + 1);

                Long element1 = listAmountUser.get(value1);
                Long element2 = listAmountUser.get(value2);

                return ((element1 + element2) / 2);
            } else {
                int value = (listAmountUser.size() + 1) / 2;
                return listAmountUser.get(value);
            }
        }

        return 0;
    }

    private Long findNumberGreaterAmountOfPullRequestSubmittedByTheSameUser(EntityRepository repository) {
        String amountPullRequest = "SELECT count(*) as quantidade "
                + "FROM gitPullRequest gp "
                + "where gp.repository_id = " + repository.getId().toString()
                + " and gp.user_id <> 0 "
                + "GROUP BY gp.user_id "
                + "ORDER BY quantidade";

        List<Long> listAmountUser = dao.selectNativeWithParams(amountPullRequest, null);
        if ((listAmountUser != null) && (!listAmountUser.isEmpty())) {
            return listAmountUser.get(listAmountUser.size() - 1);
        }

        return 0L;
    }

    private Long findNumberLessAmountOfPullRequestSubmittedByTheSameUser(EntityRepository repository) {
        String amountPullRequest = "SELECT count(*) as quantidade "
                + "FROM gitPullRequest gp "
                + "where gp.repository_id = " + repository.getId().toString()
                + " and gp.user_id <> 0 "
                + "GROUP BY gp.user_id "
                + "ORDER BY quantidade";

        List<Long> listAmountUser = dao.selectNativeWithParams(amountPullRequest, null);
        if ((listAmountUser != null) && (!listAmountUser.isEmpty())) {
            return listAmountUser.get(0);
        }

        return 0L;
    }

    private Long findCommitSize(EntityRepository repository, String type) {

        if (type.equals("additions")) {

            String additions = "select sum(cf.additions) "
                    + "from gitRepositoryCommit rc "
                    + "left join gitCommitFile cf on rc.id = cf.repositoryCommit_id "
                    + "where rc.repository_id = " + repository.getId().toString();

            List<Long> listAdditions = dao.selectNativeWithParams(additions, null);
            if ((listAdditions != null) && (!listAdditions.isEmpty())) {
                return listAdditions.get(0);
            }

        } else if (type.equals("changes")) {

            String changes = "select sum(cf.changes) "
                    + "from gitRepositoryCommit rc "
                    + "left join gitCommitFile cf on rc.id = cf.repositoryCommit_id "
                    + "where rc.repository_id = " + repository.getId().toString();

            List<Long> listChanges = dao.selectNativeWithParams(changes, null);
            if ((listChanges != null) && (!listChanges.isEmpty())) {
                return listChanges.get(0);
            }

        } else if (type.equals("deletions")) {

            String deletions = "select sum(cf.deletions) "
                    + "from gitRepositoryCommit rc "
                    + "left join gitCommitFile cf on rc.id = cf.repositoryCommit_id "
                    + "where rc.repository_id = " + repository.getId().toString();

            List<Long> listDeletions = dao.selectNativeWithParams(deletions, null);
            if ((listDeletions != null) && (!listDeletions.isEmpty())) {
                return listDeletions.get(0);
            }
        }

        return 0L;
    }

    @Override
    public String getHeadCSV() {
        return "Repository_Name;Amount_Of_Stars;Amount_Of_Contributors;Repository_Age_In_Months;Amount_Of_Open_Issue;Amount_Of_Close_Issue;Amount_Of_Comments_Issues;Average_Amount_Comments_Issues;Amount_Of_Commits;Amount_Of_Commits_Pull_Request;Amount_Of_Followers_Users_Project;Amount_Of_Comments_Closed_Pull_Request;Amount_Of_Commit_Inclusion_Test;Average_Amount_Of_Commit_Inclusion_Test;Average_Pull_Request_Submitted_By_The_Same_User;Median_Pull_Request_Submitted_By_The_Same_User;Greater_Amount_Of_Pull_Request_Submitted_By_The_Same_User;Less_Amount_Of_Pull_Request_Submitted_By_The_Same_User;Amount_Added_Lines;Amount_Changed_Lines;Amount_Deleted_Lines";

    }

}
