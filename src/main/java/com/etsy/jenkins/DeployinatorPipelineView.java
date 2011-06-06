package com.etsy.jenkins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.ViewDescriptor;
import hudson.util.ListBoxModel;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineModule;
import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView;
import au.com.centrumsystems.hudson.plugin.buildpipeline.ProjectFormModule;

import com.google.inject.Guice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class DeployinatorPipelineView extends BuildPipelineView {

  @DataBoundConstructor
  public DeployinatorPipelineView(
      final String name, 
      final String buildViewTitle,
      final String selectedJob,
      final String noOfDisplayedBuilds) {
    super(name, buildViewTitle, selectedJob, noOfDisplayedBuilds);
  }

  @Override
  public void doManualExecution(
      final StaplerRequest req, final StaplerResponse res) {
    AbstractProject<?, ?> triggerProject = (AbstractProject<?,?>)
        getJob(req.getParameter("triggerProjectName"));
    if (isRestricted(triggerProject)) {
      try {
        res.sendRedirect2("http://deployinator.etsycorp.com");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    super.doManualExecution(req, res);
  }

  @Override
  public void invokeBuild() {
    if (isRestricted(getSelectedProject())) {
      return;
    }

    super.invokeBuild();
  }

  private boolean isRestricted(AbstractProject<?, ?> project) {
    return new DeployinatorProjectUtil().isRestricted(project);
  }

  /**
   * Have to copy the whole thing since the original class was made final
   */
  @Extension
  public static class DescriptorImpl extends BuildPipelineView.DescriptorImpl {

    /**
     * descriptor impl constructor This empty constructor is required for stapler. If you remove this constructor, text name of
     * "Build Pipeline View" will be not displayed in the "NewView" page
     */
    public DescriptorImpl() {
      super(Guice.createInjector(new DeployinatorPipelineModule(), new BuildPipelineModule(), new ProjectFormModule()));
    }

    /**
     * get the display name
     * 
     * @return display name
     */
    @Override
    public String getDisplayName() {
      return "Deployinator Pipeline View";
    }
  }
}

