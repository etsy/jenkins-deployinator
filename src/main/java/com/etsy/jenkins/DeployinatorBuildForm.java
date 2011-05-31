package com.etsy.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;
import javax.annotation.Nullable;

import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildForm;
import au.com.centrumsystems.hudson.plugin.util.BuildUtil;
import au.com.centrumsystems.hudson.plugin.util.HudsonResult;
import au.com.centrumsystems.hudson.plugin.util.ProjectUtil;

public class DeployinatorBuildForm extends BuildForm {

  private final DeployinatorProjectUtil projectUtil;
  private final AbstractProject<?, ?> project;

  @Inject
  public DeployinatorBuildForm(
      @Assisted("currentBuild") @Nullable AbstractBuild<?, ?> currentBuild,
      @Assisted AbstractProject<?, ?> project,
      @Assisted List<BuildForm> dependencies,
      @Assisted("upstreamBuild") @Nullable AbstractBuild<?, ?> upstreamBuild,
      DeployinatorProjectUtil projectUtil,
      BuildUtil buildUtil) {
    super(
      currentBuild,
      project,
      dependencies,
      upstreamBuild,
      projectUtil,
      buildUtil);
    this.projectUtil = projectUtil;
    this.project = project;
  }

  @Override
  public String getStatus() {
     String status = super.getStatus();
     if (projectUtil.isRestricted(project)
         && HudsonResult.MANUAL.toString().equals(status)) {
       status = HudsonResult.PENDING.toString();
     }
     return status;
  }
}

