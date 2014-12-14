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
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxRepositoryManyMetricas;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eduardogreco
 */
public class RepositoryCurintiaServices extends AbstractMatrixServices {

    public RepositoryCurintiaServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public RepositoryCurintiaServices(GenericDao dao, EntityRepository repository, List<EntityMatrix> matricesToSave, Map params, OutLog out) {
        super(dao, repository, matricesToSave, params, out);
    }
    List<EntityRepository> repos = new ArrayList<>();

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
            if (true){ // calcula start?
                // calcula aqui
                aux.setQtdStars(0);
            }
            if (true) { // calcula idade?
                aux.setIdade(0);
            }
            
            results.add(aux);
        }

        matricesToSave.add(null);
    }

    @Override
    public String getHeadCSV() {
        return "user;followers";
    }

}
