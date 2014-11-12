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

    public UserStarsServices(GenericDao dao, EntityRepository repository, List<EntityMatrix> matricesToSave, Map params, OutLog out) {
        super(dao, repository, matricesToSave, params, out);
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getRepository() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        System.out.println("Rodou!");

        System.out.println(params.get("dataInicial"));
        System.out.println(params.get("dataFinal"));

//       "SELECT us.*"
//                + "  FROM gitrepository_userstarreds st, gituser us, gitrepository rep\n"
//                + "  WHERE us.id = st.starred_id"
//                + "  and rep.id = st.starredrepositories_id"
//                + "  and st.starredrepositories_id = :repo";

         
//        EntityMatrix matrix = new EntityMatrix();
//        matrix.getNodes().add(new EntityMatrixNode("eduaro;carro.java;5"));
//        matrix.getNodes().add(new EntityMatrixNode("douglas;pessoa.java;6"));
//        matricesToSave.add(matrix);
    }

    @Override
    public String getHeadCSV() {
        return "user;file;qtd";
    }

}
