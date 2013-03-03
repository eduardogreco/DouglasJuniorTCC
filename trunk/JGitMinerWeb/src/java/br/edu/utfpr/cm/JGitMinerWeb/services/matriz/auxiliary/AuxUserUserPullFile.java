/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matriz.auxiliary;

import br.edu.utfpr.cm.JGitMinerWeb.pojo.miner.EntityCommitUser;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.miner.EntityPullRequest;

/**
 *
 * @author douglas
 */
public class AuxUserUserPullFile {

    private EntityCommitUser commitUser;
    private EntityCommitUser commitUser2;
    private EntityPullRequest pull;
    private String file;

    public AuxUserUserPullFile(EntityCommitUser commitUser, EntityCommitUser commitUser2, EntityPullRequest pull, String file) {
        this.commitUser = commitUser;
        this.commitUser2 = commitUser2;
        this.pull = pull;
        this.file = file;
    }

    public EntityPullRequest getPull() {
        return pull;
    }

    public void setPull(EntityPullRequest pull) {
        this.pull = pull;
    }

    public String getFile() {
        return file;
    }

    public EntityCommitUser getCommitUser() {
        return commitUser;
    }

    public void setCommitUser(EntityCommitUser commitUser) {
        this.commitUser = commitUser;
    }

    public EntityCommitUser getCommitUser2() {
        return commitUser2;
    }

    public void setCommitUser2(EntityCommitUser commitUser2) {
        this.commitUser2 = commitUser2;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AuxUserUserPullFile) {
            AuxUserUserPullFile other = (AuxUserUserPullFile) obj;
            if (this.pull.equals(other.pull) && this.file.equals(other.file)) {
                if (this.commitUser.equals(other.commitUser) && this.commitUser2.equals(other.commitUser2)) {
                    return true;
                }
                if (this.commitUser.equals(other.commitUser2) && this.commitUser2.equals(other.commitUser)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.commitUser != null ? this.commitUser.hashCode() : 0);
        hash += (this.commitUser2 != null ? this.commitUser2.hashCode() : 0);
        hash += (this.pull != null ? this.pull.hashCode() : 0);
        hash += (this.file != null ? this.file.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return commitUser + " | " + commitUser2 + " | " + pull + " | " + file;
    }
}
