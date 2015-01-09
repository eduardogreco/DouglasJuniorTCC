/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matrix.EntityMatrix;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary.AuxUserUserFile;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author douglas
 */
public class UserModifySameFileInDateServices extends AbstractMatrixServices {

    public UserModifySameFileInDateServices(GenericDao dao, OutLog out) {
        super(dao, out);
    }

    public UserModifySameFileInDateServices(GenericDao dao, List<EntityRepository> repository, List<EntityMatrix> matricesToSave, Map params, List<String> selectedFiltersParams, OutLog out) {
        super(dao, repository, matricesToSave, params, selectedFiltersParams, out);
    }

    private String getPrefixFile() {
        return params.get("prefixFile") + "%";
    }

    private String getSuffixFile() {
        return "%" + params.get("suffixFile");
    }

    private List<String> getFilesName() {
        List<String> filesName = new ArrayList<>();
        for (String fileName : (params.get("filesName") + "").split("\n")) {
            fileName = fileName.trim();
            if (!fileName.isEmpty()) {
                filesName.add(fileName);
            }
        }
        return filesName;
    }

    public Date getBeginDate() {
        return getDateParam("beginDate");
    }

    public Date getEndDate() {
        return getDateParam("endDate");
    }

    @Override
    public void run() {
        System.out.println(params);

        if (getRepositorys() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }

        List<String> filesName = getFilesName();
        String prefix = getPrefixFile();
        String suffix = getSuffixFile();

        String jpql = "SELECT DISTINCT NEW " + AuxUserUserFile.class.getName() + "(rc.committer.login, rc.commit.committer.email, rc2.committer.login, rc2.commit.committer.email, f.filename) "
                + "FROM "
                + "EntityRepositoryCommit rc JOIN rc.files f,  "
                + "EntityRepositoryCommit rc2 JOIN rc2.files f2 "
                + "WHERE "
                + "rc.repository = :repo AND "
                + "rc2.repository = :repo AND "
                + "rc.commit.committer.dateCommitUser BETWEEN :beginDate AND :endDate AND "
                + "rc2.commit.committer.dateCommitUser BETWEEN :beginDate AND :endDate AND "
                + (prefix.length() > 1 ? "f.filename LIKE :prefix AND " : "")
                + (prefix.length() > 1 ? "f2.filename LIKE :prefix AND " : "")
                + (suffix.length() > 1 ? "f.filename LIKE :suffix AND " : "")
                + (suffix.length() > 1 ? "f2.filename LIKE :suffix AND " : "")
                + (!filesName.isEmpty() ? "f.filename IN :filesName AND " : "")
                + "f.filename = f2.filename AND "
                + "rc.commit.committer.email <> rc2.commit.committer.email ";

        System.out.println(jpql);

        String[] bdParams = new String[]{
            "repo",
            (prefix.length() > 1 ? "prefix" : "#none#"),
            (suffix.length() > 1 ? "suffix" : "#none#"),
            (!filesName.isEmpty() ? "filesName" : "#none#"),
            "beginDate",
            "endDate"
        };
        Object[] bdObjects = new Object[]{
            getRepositorys(),
            prefix,
            suffix,
            filesName,
            getBeginDate(),
            getEndDate()
        };

        List<AuxUserUserFile> result = dao.selectWithParams(jpql, bdParams, bdObjects);

        System.out.println("Result: " + result.size());

        result = removeDuplicade(result);

        System.out.println("Result distinct: " + result.size());

        EntityMatrix matrix = new EntityMatrix();
        matrix.setNodes(objectsToNodes(result));
        matricesToSave.add(matrix);
    }

    @Override
    public String getHeadCSV() {
        return "user;user2;file";
    }

    private List<AuxUserUserFile> removeDuplicade(List<AuxUserUserFile> result) {
        List<AuxUserUserFile> newResult = new ArrayList<>(result.size());
        for (AuxUserUserFile aux : result) {
            if (!newResult.contains(aux)) {
                newResult.add(aux);
            }
        }
        result.clear();
        result = null;
        return newResult;
    }
}
