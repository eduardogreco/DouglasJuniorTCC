/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matrix.auxiliary;

import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityMilestone;
import java.util.Objects;

/**
 *
 * @author douglas
 */
public class AuxUserUserFileMilestoneDouglas {

    private String user;
    private String user2;
    private String fileName;
    private String milestoneNumber;

    public AuxUserUserFileMilestoneDouglas(String user, String user2, String fileName) {
        this.user = user;
        this.user2 = user2;
        this.fileName = fileName;
        this.milestoneNumber = "";
    }

    public AuxUserUserFileMilestoneDouglas(String user, String user2, String fileName, EntityMilestone milestone) {
        this.user = user;
        this.user2 = user2;
        this.fileName = fileName;
        if (milestone != null) {
            this.milestoneNumber = milestone.getNumber() + "";
        } else {
            this.milestoneNumber = "";
        }
    }

    public AuxUserUserFileMilestoneDouglas(String userLogin, String userMail, String userLogin2, String userMail2, String fileName, EntityMilestone milestone) {
        this(
                userLogin == null || userLogin.isEmpty() ? userMail : userLogin,
                userLogin2 == null || userLogin2.isEmpty() ? userMail2 : userLogin2,
                fileName,
                milestone);
    }

    public AuxUserUserFileMilestoneDouglas(String userLogin, String userMail, String userLogin2, String userMail2, String fileName) {
        this(
                userLogin == null || userLogin.isEmpty() ? userMail : userLogin,
                userLogin2 == null || userLogin2.isEmpty() ? userMail2 : userLogin2,
                fileName,
                null);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AuxUserUserFileMilestoneDouglas) {
            AuxUserUserFileMilestoneDouglas other = (AuxUserUserFileMilestoneDouglas) obj;
            if (this.milestoneNumber.equals(other.milestoneNumber)
                    && this.fileName.equals(other.fileName)) {
                if (this.user.equals(other.user) && this.user2.equals(other.user2)) {
                    return true;
                }
                if (this.user.equals(other.user2) && this.user2.equals(other.user)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (Objects.hashCode(this.user) + Objects.hashCode(this.user2));
        hash = 23 * hash + Objects.hashCode(this.fileName);
        hash = 23 * hash + Objects.hashCode(this.milestoneNumber);
        return hash;
    }

    @Override
    public String toString() {
        return user + ";" + user2 + ";" + fileName + ";" + milestoneNumber;
    }
}
