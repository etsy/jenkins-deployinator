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

  public boolean disabled;

  public DeployinatorRestrictJobProperty(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public boolean prebuild(AbstractBuild build, BuildListener listener) {
    if (disabled) {
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
      JSONObject restrict = formData.optJSONObject("deployinatorRestrict");
      return new DeployinatorRestrictJobProperty(restrict == null);
    }
  }
}

