package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import au.com.centrumsystems.hudson.plugin.util.BuildUtil;

import java.util.List;

public class DeployinatorUtil {

  public static boolean isRestricted(AbstractProject<?, ?> project) {
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

  public static AbstractBuild<?, ?> getDownstreamBuild(
      AbstractProject<?, ?> downstreamProject, 
      AbstractBuild<?, ?> upstreamBuild) {
    DeployinatorCommand.CLICause upstreamCause =
        upstreamBuild.getCause(DeployinatorCommand.CLICause.class);
    if (upstreamCause == null) {
      return BuildUtil.getDownstreamBuild(downstreamProject, upstreamBuild);
    }
    for (AbstractBuild<?, ?> build : downstreamProject.getBuilds()) {
      DeployinatorCommand.CLICause cause =
          build.getCause(DeployinatorCommand.CLICause.class);
      if ((cause != null)
          && upstreamCause.getDeployVersion()
              .equals(cause.getDeployVersion())) {
        return build;
      }
    }
    return null;
  }
}

