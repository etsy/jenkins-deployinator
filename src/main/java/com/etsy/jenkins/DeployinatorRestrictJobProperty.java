package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import org.kohsuke.stapler.StaplerRequest;

import net.sf.json.JSONObject;

public class DeployinatorRestrictJobProperty 
extends JobProperty<AbstractProject<?, ?>> {

  private boolean enable;

  public DeployinatorRestrictJobProperty(boolean enable) {
    this.enable = enable;
  }

  public boolean isEnabled() {
    return enable;
  }

  @Override
  public boolean prebuild(AbstractBuild build, BuildListener listener) {
    if (!enable) {
      return true;
    }

    Cause cause = build.getCause(DeployinatorCommand.CLICause.class);
    if (cause == null) {
      listener.error("Build can only be started by Deployinator.");
      return false;
    }
    return true;
  }

  @Override
  public JobPropertyDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  public static class DescriptorImpl extends JobPropertyDescriptor {

    public DescriptorImpl() {
      super(DeployinatorRestrictJobProperty.class);
      load();
    }

    @Override
    public String getDisplayName() {
      return "Only allow builds invoked by Deployinator.";
    }

    @Override
    public boolean isApplicable(Class<? extends Job> jobType) {
      return AbstractProject.class.isAssignableFrom(jobType);
    }

    @Override
    public JobProperty<?> newInstance(
        StaplerRequest req, JSONObject formData) {
      JSONObject enable = formData.optJSONObject("deployinatorRestrict");
      DeployinatorRestrictJobProperty prop =
          new DeployinatorRestrictJobProperty(enable != null);
      if (!prop.isEnabled()) {
        return null;
      }
      return prop;
    }
  }
}

