package com.etsy.jenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import net.sf.json.JSONObject;

public class StageNumberJobProperty
extends JobProperty<AbstractProject<?, ?>> {

  public int number;

  @DataBoundConstructor
  public StageNumberJobProperty(int number) {
    this.number = number;
  }

  public int getStageNumber() {
    return number;
  }

  @Override
  public JobPropertyDescriptor getDescriptor() {
    return DESCRIPTOR;
  }

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  public static class DescriptorImpl extends JobPropertyDescriptor {

    public DescriptorImpl() {
      super(StageNumberJobProperty.class);
      load();
    }

    @Override
    public String getDisplayName() {
      return "Set the number of the stage in a Deployinator pipeline.";
    }

    @Override
    public boolean isApplicable(Class<? extends Job> jobType) {
      return AbstractProject.class.isAssignableFrom(jobType);
    }

    @Override
    public JobProperty<?> newInstance(
        StaplerRequest req, JSONObject formData) {
      StageNumberJobProperty prop = 
          req.bindJSON(StageNumberJobProperty.class, formData);
      if (prop.getStageNumber() == 0) {
        return null;
      }
      return prop;
    }
  }
}

