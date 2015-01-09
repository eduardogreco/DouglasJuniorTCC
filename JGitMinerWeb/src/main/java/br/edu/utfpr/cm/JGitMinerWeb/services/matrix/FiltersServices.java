/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityIssue;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
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

        List<AuxRepositoryManyMetricas> results = new ArrayList<>();

        for (EntityRepository repo : repos) {
            AuxRepositoryManyMetricas aux = new AuxRepositoryManyMetricas();
            aux.setRepositoryName(repo.getName());

            for (String nameFilter : getSelectedFiltersParams()) {
                if (nameFilter.equals("STARS")) {
                    aux.setQtdStars(findStars(repo));
                } else if (nameFilter.equals("AGE_REPOSITORY")) {
                    aux.setIdade(findAgeRepository(repo));
                } else if (nameFilter.equals("NUMBER_CONTRIBUTOR")) {
                    aux.setQtdColabor(findNumberOFContributor(repo));
                } else if (nameFilter.equals("OPEN_ISSUE")) {
                    aux.setQtdOpenIssue(findIssue(repo, "open")); 
                } else if (nameFilter.equals("CLOSE_ISSUE")) {
                    aux.setQtdCloseIssue(findIssue(repo, "closed"));
                }
            }
            results.add(aux);
        }

        EntityMatrix matrix = new EntityMatrix();
        matrix.setNodes(objectsToNodes(results));
        matricesToSave.add(matrix);

    }

    private int findStars(EntityRepository repository) {
        int quant = 0;
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
                quant = usersStars.size();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return quant;
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
        int quant = 0;
        try {
            String jpql = "SELECT u "
                    + "FROM "
                    + "EntityIssue i JOIN u.collaboratedRepositories r "
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
                quant = users.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return quant;
    }

    private int findIssue(EntityRepository repository, String state) {
        int quant = 0;
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
                quant = issues.size();
            }
        } catch (Exception ex) {
             ex.printStackTrace();
        }
        return quant;
    }

    @Override
    public String getHeadCSV() {
        return "repositoryName;qtdStars;qtdColabor;idade;qtdOpenIssue;qtdCloseIssue";
    }

}
