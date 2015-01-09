/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrixNode;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 *
 * @author eduardogreco
 */
public class AgeRepositoryServices extends AbstractMatrixServices {

    public AgeRepositoryServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

   public AgeRepositoryServices(GenericDao dao, List<EntityRepository> repository, List<EntityMatrix> matricesToSave, Map params, List<String> selectedFiltersParams, OutLog out) {
        super(dao, repository, matricesToSave, params, selectedFiltersParams, out);
    }

    @Override
    public void run() {
        System.out.println(params);
        if (getRepositorys().get(0) == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        String jpql = "SELECT r "
                + "FROM "
                + "EntityRepository r "
                + "WHERE "
                + "r = :repo";
        String[] bdParams = new String[]{
            "repo"
        };

        Object[] bdObjects = new Object[]{
            getRepositorys().get(0)
        };

        List<EntityRepository> repository = dao.selectWithParams(jpql, bdParams, bdObjects);

        System.out.println(repository.size());
        System.out.println(repository);
        EntityMatrix matrix = new EntityMatrix();
        for (EntityRepository r : repository) {
            DateTime dateRepository = new DateTime(r.getCreatedAt());
            Period period = new Period(dateRepository, new DateTime(), PeriodType.months().withDaysRemoved());
            matrix.getNodes().add(new EntityMatrixNode(period.getMonths()));
        }
        matricesToSave.add(matrix);
    }

    @Override
    public String getHeadCSV() {
        return "createdAt";
    }

}
