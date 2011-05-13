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

import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildForm;
import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineForm;
import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView;
import au.com.centrumsystems.hudson.plugin.buildpipeline.ProjectForm;
import au.com.centrumsystems.hudson.plugin.util.ProjectUtil;

import com.google.common.collect.Lists;

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
  public BuildPipelineForm getBuildPipelineForm() throws URISyntaxException {
    AbstractProject<?, ?> project = getSelectedProject();
    BuildPipelineForm buildPipelineForm = null;
    if (project != null) {
      int maxNoOfDisplayBuilds = Integer.valueOf(getNoOfDisplayedBuilds());
      int rowsAppended = 0;
      List<BuildForm> buildForms = Lists.<BuildForm>newArrayList();
      for (AbstractBuild<?, ?> currentBuild : project.getBuilds()) {
        buildForms.add(
            new BuildForm(
            new DeployinatorPipelineBuild(currentBuild, project, null)));
        rowsAppended++;
        if (rowsAppended >= maxNoOfDisplayBuilds) {
          break;
        }
      }
      buildPipelineForm = new BuildPipelineForm(
          new ProjectForm(project), 
          buildForms);
    }
    return buildPipelineForm;
  }

  @Override
  public void doManualExecution(
      final StaplerRequest req, final StaplerResponse res) {
    AbstractProject<?, ?> triggerProject = (AbstractProject<?,?>)
        getJob(req.getParameter("triggerProjectName"));
    if (DeployinatorUtil.isRestricted(triggerProject)) {
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
    if (DeployinatorUtil.isRestricted(getSelectedProject())) {
      return;
    }

    super.invokeBuild();
  }

  /**
   * Have to copy the whole thing since the original class was made final
   */
  @Extension
  public static final class DescriptorImpl extends ViewDescriptor {

    /**
     * descriptor impl constructor This empty constructor is required for stapler. If you remove this constructor, text name of
     * "Build Pipeline View" will be not displayed in the "NewView" page
     */
    public DescriptorImpl() {
      super();
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

    /**
     * Display Job List Item in the Edit View Page
     * 
     * @return ListBoxModel
     */
    public ListBoxModel doFillSelectedJobItems() {
      ListBoxModel options = new ListBoxModel();
      for (String jobName : Hudson.getInstance().getJobNames()) {
        options.add(jobName);
      }
      return options;
    }

    /**
     * Display No Of Builds Items in the Edit View Page
     * 
     * @return ListBoxModel
     */
    public ListBoxModel doFillNoOfDisplayedBuildsItems() {
      ListBoxModel options = new ListBoxModel();
      List<String> noOfBuilds = Lists.<String>newArrayList();
      noOfBuilds.add("1");
      noOfBuilds.add("2");
      noOfBuilds.add("3");
      noOfBuilds.add("5");
      noOfBuilds.add("10");
      noOfBuilds.add("20");
      noOfBuilds.add("50");
      noOfBuilds.add("100");
      noOfBuilds.add("200");
      noOfBuilds.add("500");

      for (String noOfBuild : noOfBuilds) {
        options.add(noOfBuild);
      }
      return options;
    }
  }
}

