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
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        if ((getBeginDate() == null) || (getEndDate() == null)) {
            throw new IllegalArgumentException("Informe o intervalo de datas.");
        }

        List<AuxRepositoryManyMetricas> results = new ArrayList<>();

        for (EntityRepository repo : repos) {
            AuxRepositoryManyMetricas aux = new AuxRepositoryManyMetricas();
            aux.setRepositoryName(repo.getName());

            for (String nameFilter : getSelectedFiltersParams()) {

                switch (nameFilter) {
                    case "STARS":
                        aux.setAmountStars(findStars(repo));
                        break;
                    case "AGE_REPOSITORY":
                        aux.setAgeInMonths(findAgeRepository(repo));
                        break;
                    case "NUMBER_CONTRIBUTOR":
                        aux.setAmountContributors(findNumberOFContributor(repo));
                        break;
                    case "OPEN_ISSUE":
                        aux.setAmountOpenIssue(findIssue(repo, "open"));
                        break;
                    case "CLOSE_ISSUE":
                        aux.setAmountCloseIssue(findIssue(repo, "closed"));
                        break;
                    case "NUMBER_COMMENT_ISSUE":
                        aux.setAmountCommentsIssues(findCommentIssue(repo));
                        break;
                    case "NUMBER_COMMITS":
                        aux.setAmountCommits(findNumberCommits(repo));
                        break;
                    case "NUMBER_FOLLOWERS_USERS_PROJECT":
                        aux.setAmountFollowersUsersProject(findFollowersUsersProject(repo));
                        break;
                    case "NUMBER_COMMENTS_CLOSED_PULL":
                        aux.setAmountCommentsClosedPullRequest(findNumberCommentsClosedPull(repo));
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

    private int findStars(EntityRepository repository) {
        int amount = 0;
        try {
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
                amount = usersStars.size();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private int findAgeRepository(EntityRepository repository) {
        Period period = null;
        try {
            DateTime dateRepository = new DateTime(repository.getCreatedAt());
            period = new Period(dateRepository, new DateTime(), PeriodType.months().withDaysRemoved());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return period.getMonths();
    }

    private int findNumberOFContributor(EntityRepository repository) {
        int amount = 0;
        try {
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
                amount = users.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private int findIssue(EntityRepository repository, String state) {
        int amount = 0;
        try {
            String jpql = "SELECT i "
                    + "FROM "
                    + "EntityIssue i "
                    + "WHERE i.repository = :repo "
                    + "AND i.stateIssue = :state";

            String[] bdParams = new String[]{
                "repo", "state"
            };

            Object[] bdObjects = new Object[]{
                repository, state
            };

            List<EntityIssue> issues = dao.selectWithParams(jpql, bdParams, bdObjects);
            if ((issues != null) && (!issues.isEmpty())) {
                amount = issues.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private int findCommentIssue(EntityRepository repository) {
        int amount = 0;
        try {
            String jpql = "SELECT c "
                    + "FROM "
                    + "EntityIssue i JOIN i.comments c "
                    + "WHERE i.repository = :repo";

            String[] bdParams = new String[]{
                "repo"
            };

            Object[] bdObjects = new Object[]{
                repository
            };

            List<EntityComment> comments = dao.selectWithParams(jpql, bdParams, bdObjects);
            if ((comments != null) && (!comments.isEmpty())) {
                amount = comments.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private int findNumberCommits(EntityRepository repository) {
        int amount = 0;
        try {
            String jpql = "SELECT i "
                    + "FROM "
                    + "EntityRepositoryCommit i "
                    + "WHERE i.repository = :repo";

            String[] bdParams = new String[]{
                "repo"
            };

            Object[] bdObjects = new Object[]{
                repository
            };

            List<EntityRepositoryCommit> commits = dao.selectWithParams(jpql, bdParams, bdObjects);
            if ((commits != null) && (!commits.isEmpty())) {
                amount = commits.size();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private int findFollowersUsersProject(EntityRepository repository) {
        int amount = 0;
        try {
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    private Long findNumberCommentsClosedPull(EntityRepository repository) {
        Long amount = 0L;
        try {
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return amount;
    }

    @Override
    public String getHeadCSV() {
        return "repositoryName;amountStars;amountContributors;ageInMonths;amountOpenIssue;amountCloseIssue;amountCommentsIssues;amountCommits;amountFollowersUsersProject;amountCommentsClosedPullRequest";

    }

}
