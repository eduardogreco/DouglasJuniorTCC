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
public class UserFollowersServices extends AbstractMatrixServices {

    public UserFollowersServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public UserFollowersServices(GenericDao dao, EntityRepository repository, List<EntityMatrix> matricesToSave, Map params, OutLog out) {
        super(dao, repository, matricesToSave, params, out);
    }

    @Override
    public void run() {
        System.out.println(params);
        if (getRepository() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        String jpql = "SELECT u "
                + "FROM "
                + "EntityUser u JOIN u.collaboratedRepositories r "
                + "WHERE "
                + "r = :repo";
        String[] bdParams = new String[]{
            "repo"
        };

        Object[] bdObjects = new Object[]{
            getRepository()
        };

        List<EntityUser> usersFollowers = dao.selectWithParams(jpql, bdParams, bdObjects);

        System.out.println(usersFollowers.size());
        System.out.println(usersFollowers);
        EntityMatrix matrix = new EntityMatrix();
        for (EntityUser u : usersFollowers) {
            String login = u.getLogin() != null ? u.getLogin() : u.getEmail();
            matrix.getNodes().add(new EntityMatrixNode(login + ";" + u.getFollowers()));
        }
        matricesToSave.add(matrix);
    }

    @Override
    public String getHeadCSV() {
        return "user;followers";
    }

}
