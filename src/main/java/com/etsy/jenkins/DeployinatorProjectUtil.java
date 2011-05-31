package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.model.AbstractProject;

import au.com.centrumsystems.hudson.plugin.util.ProjectUtil;

public class DeployinatorProjectUtil extends ProjectUtil {

  public boolean isRestricted(AbstractProject<?, ?> project) {
    if (project != null) {
      DeployinatorRestrictJobProperty property =
          (DeployinatorRestrictJobProperty)
              project.getProperty(DeployinatorRestrictJobProperty.class);
      if ((property != null) && property.isEnabled()) {
        return true;
      }
    }
    return false;
  }
}

