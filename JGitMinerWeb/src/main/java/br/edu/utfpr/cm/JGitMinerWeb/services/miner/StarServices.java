/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.miner;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityUser;
import static br.edu.utfpr.cm.JGitMinerWeb.services.miner.UserServices.getGitUserByLogin;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 *
 * @author douglas
 */
public class StarServices implements Serializable {

    public static List<User> getStarsByRepository(String ownerRepositoryLogin, String repositoryName) {
        // /repos/:owner/:repo/stargazers
        try {
            StringBuilder uri = new StringBuilder("/repos");
            uri.append('/').append(ownerRepositoryLogin);
            uri.append('/').append(repositoryName);
            uri.append("/stargazers");
            PagedRequest<User> request = new PagedRequest<User>(1, 100);
            request.setUri(uri);
            request.setType(new TypeToken<List<User>>() {
            }.getType());
            PageIterator<User> pageIterator = new PageIterator<User>(request, AuthServices.getGitHubClient());
            List<User> elements = new ArrayList<User>();
            try {
                while (pageIterator.hasNext()) {
                    elements.addAll(pageIterator.next());
                }
            } catch (NoSuchPageException pageException) {
                throw pageException;
            }
            return elements;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return getStarsByRepository(ownerRepositoryLogin, repositoryName);
        }
    }

    public static EntityUser createEntity(User gitUser, GenericDao dao, boolean firstMiner) {
        if (gitUser == null) {
            return null;
        }

        EntityUser user = getUserByLogin(gitUser.getLogin(), dao);

        if (firstMiner || user == null) {
            if (user == null) {
                user = new EntityUser();
            }
            if (gitUser.getLogin() != null
                    && (gitUser.getEmail() == null
                    || gitUser.getName() == null)) {
                gitUser = getGitUserByLogin(gitUser.getLogin());
            }
            user.setCreatedAt(gitUser.getCreatedAt());
            user.setCollaborators(gitUser.getCollaborators());
            user.setDiskUsage(gitUser.getDiskUsage());
            user.setFollowers(gitUser.getFollowers());
            user.setFollowing(gitUser.getFollowing());
            user.setOwnedPrivateRepos(gitUser.getOwnedPrivateRepos());
            user.setPrivateGists(gitUser.getPrivateGists());
            user.setPublicGists(gitUser.getPublicGists());
            user.setPublicRepos(gitUser.getPublicRepos());
            user.setTotalPrivateRepos(gitUser.getTotalPrivateRepos());
            user.setAvatarUrl(gitUser.getBlog());
            user.setCompany(gitUser.getCompany());
            user.setEmail(gitUser.getEmail());
            user.setHtmlUrl(gitUser.getHtmlUrl());
            user.setLocation(gitUser.getLocation());
            user.setName(gitUser.getName());
            user.setType(gitUser.getType());
        }

        user.setMineredAt(new Date());
        user.setGravatarId(gitUser.getGravatarId());
        user.setIdUser(gitUser.getId());
        user.setLogin(gitUser.getLogin());
        user.setUrl(gitUser.getUrl());

        if (user.getId() == null || user.getId().equals(0l)) {
            dao.insert(user);
        } else {
            dao.edit(user);
        }

        return user;
    }

    private static EntityUser getUserByLogin(String login, GenericDao dao) {
        List<EntityUser> users = dao.executeNamedQueryWithParams("User.findByLogin", new String[]{"login"}, new Object[]{login}, true);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

}
