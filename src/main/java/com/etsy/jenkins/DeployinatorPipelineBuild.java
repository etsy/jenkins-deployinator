package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;

import au.com.centrumsystems.hudson.plugin.buildpipeline.PipelineBuild;
import au.com.centrumsystems.hudson.plugin.util.BuildUtil;
import au.com.centrumsystems.hudson.plugin.util.HudsonResult;
import au.com.centrumsystems.hudson.plugin.util.ProjectUtil;

import com.google.common.collect.Lists;

import java.util.List;

public class DeployinatorPipelineBuild extends PipelineBuild {

  public DeployinatorPipelineBuild() { super(); }

  public DeployinatorPipelineBuild(
      final AbstractBuild<?, ?> build, 
      final AbstractProject<?, ?> project, 
      final AbstractBuild<?, ?> previousBuild) {
    super(build, project, previousBuild);
  }

  public DeployinatorPipelineBuild(final FreeStyleProject project) {
    super(project);
  }

  @Override
  public List<PipelineBuild> getDownstreamPipeline() {
    List<PipelineBuild> pbList = Lists.<PipelineBuild>newArrayList();

    AbstractProject<?, ?> currentProject = getProject();

    List<AbstractProject<?, ?>> downstreamProjects 
        = ProjectUtil.getDownstreamProjects(currentProject);
    for (AbstractProject<?, ?> proj : downstreamProjects) {
      AbstractBuild<?, ?> returnedBuild = null;
      AbstractBuild<?, ?> currentBuild = getCurrentBuild();
      if (currentBuild != null) {
        returnedBuild = DeployinatorUtil.getDownstreamBuild(proj, currentBuild);
      }
      PipelineBuild newPB =
          new DeployinatorPipelineBuild(returnedBuild, proj, currentBuild);
      pbList.add(newPB);
    }

    return pbList;
  }

  @Override
  public String getScmRevision() {
    DeployinatorCommand.CLICause cause = getCause();
    if (cause == null) {
      return super.getScmRevision();
    }
    return String.format(
        "%s &rarr; %s",
        cause.getOldRevision(),
        cause.getNewRevision());
  }

  public String getScmRevisionUrl() {
    DeployinatorCommand.CLICause cause = getCause();
    if (cause == null) {
      return "#";
    }
    return cause.getDeployinatorDiffUrl(); 
  }

  public DeployinatorCommand.CLICause getCause() {
    AbstractBuild<?, ?> build = getCurrentBuild();
    if (build == null) {
      return null;
    }
    return build.getCause(DeployinatorCommand.CLICause.class);
  }
  @Override
  public String getCurrentBuildResult() {
    String result = super.getCurrentBuildResult();
    if (DeployinatorUtil.isRestricted(getProject())
        && HudsonResult.MANUAL.toString().equals(result)) {
      return HudsonResult.PENDING.toString();
    }
    return result;
  }
}
