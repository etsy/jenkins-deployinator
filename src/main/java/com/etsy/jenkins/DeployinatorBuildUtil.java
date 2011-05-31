package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import com.google.inject.Inject;

import au.com.centrumsystems.hudson.plugin.util.BuildUtil;
import au.com.centrumsystems.hudson.plugin.util.HudsonResult;

import java.util.List;

public class DeployinatorBuildUtil extends BuildUtil {

  private final DeployinatorProjectUtil projectUtil;

  @Inject
  public DeployinatorBuildUtil(DeployinatorProjectUtil projectUtil) {
    super();
    this.projectUtil = projectUtil;
  }

  @Override
  public AbstractBuild<?, ?> getDownstreamBuild(
      final AbstractProject<?, ?> downstreamProject,
      final AbstractBuild<?, ?> upstreamBuild) {
    DeployinatorCommand.CLICause upstreamCause =
        upstreamBuild.getCause(DeployinatorCommand.CLICause.class);
    if (upstreamCause == null) {
      return super.getDownstreamBuild(downstreamProject, upstreamBuild);
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

