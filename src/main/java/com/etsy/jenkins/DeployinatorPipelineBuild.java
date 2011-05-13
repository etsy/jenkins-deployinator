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
          new PipelineBuild(returnedBuild, proj, currentBuild);
      pbList.add(newPB);
    }

    return pbList;
  }

  @Override
  public String getScmRevision() {
    DeployinatorCommand.CLICause cause = 
        getCurrentBuild().getCause(DeployinatorCommand.CLICause.class);
    if (cause != null) {
      return cause.getDeployVersion();
    }
    return super.getScmRevision();
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
