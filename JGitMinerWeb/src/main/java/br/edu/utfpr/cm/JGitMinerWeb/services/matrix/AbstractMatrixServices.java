/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrixNode;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.services.AbstractServices;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author douglas
 */
public abstract class AbstractMatrixServices extends AbstractServices {

    private final List<EntityRepository> repositorys;
    protected final List<EntityMatrix> matricesToSave;
    protected final List<String> selectedFiltersParams;

    public AbstractMatrixServices(GenericDao dao, OutLog out) {
        super(dao, out);
        this.repositorys = null;
        this.matricesToSave = null;
        this.selectedFiltersParams = null;
    }

    public AbstractMatrixServices(GenericDao dao, List<EntityRepository> repositorys, List<EntityMatrix> matricesToSave, Map params, List<String> selectedFiltersParams, OutLog out) {
        super(dao, params, out);
        this.repositorys = repositorys;
        this.matricesToSave = matricesToSave;
        this.selectedFiltersParams = selectedFiltersParams;
    }

    public List<EntityRepository> getRepositorys() {
        return repositorys;
    }

    public List<String> getSelectedFiltersParams() {
        return selectedFiltersParams;
    }

    @Override
    public abstract void run();

    /**
     * Name of columns separated by ";".
     *
     * @return column1;column2;column3;...
     */
    @Override
    public abstract String getHeadCSV();

    protected List<EntityMatrixNode> objectsToNodes(Collection list) {
        List<EntityMatrixNode> nodes = new ArrayList<>();
        for (Object obj : list) {
            nodes.add(new EntityMatrixNode(obj));
        }
        return nodes;
    }

}
