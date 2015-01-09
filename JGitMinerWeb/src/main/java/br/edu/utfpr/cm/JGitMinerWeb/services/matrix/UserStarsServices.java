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
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityUser;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eduardogreco
 */
public class UserStarsServices extends AbstractMatrixServices {

    public UserStarsServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public UserStarsServices(GenericDao dao, List<EntityRepository> repository, List<EntityMatrix> matricesToSave, Map params, List<String> selectedFiltersParams, OutLog out) {
        super(dao, repository, matricesToSave, params, selectedFiltersParams, out);
    }

    @Override
    public void run() {
        System.out.println(params);
        if (getRepositorys() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }
        String jpql = "SELECT u "
                + "FROM "
                + "EntityUser u JOIN u.starredRepositories r "
                + "WHERE "
                + "r = :repo";
        String[] bdParams = new String[]{
            "repo"
        };
        Object[] bdObjects = new Object[]{
            getRepositorys().get(0)
        };

        List<EntityUser> usersStars = dao.selectWithParams(jpql, bdParams, bdObjects);

        System.out.println(usersStars.size());
        System.out.println(usersStars);
        EntityMatrix matrix = new EntityMatrix();
        for (EntityUser u : usersStars) {
            matrix.getNodes().add(new EntityMatrixNode(u.getLogin() != null ? u.getLogin() : u.getEmail()));
        }
        matricesToSave.add(matrix);
    }

    @Override
    public String getHeadCSV() {
        return "user";
    }

}
