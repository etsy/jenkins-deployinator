package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.Extension;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class DeployinatorColumn extends ListViewColumn {

  @DataBoundConstructor
  public DeployinatorColumn() {}

  @Override
  public String getColumnCaption() {
    return "Deploy Info";
  }

  public static DeployinatorCommand.CLICause getCause(Run run) {
    return (DeployinatorCommand.CLICause) 
        run.getCause(DeployinatorCommand.CLICause.class);
  }

  @Extension
  public static class DescriptorImpl extends ListViewColumnDescriptor {

    @Override
    public String getDisplayName() {
      return "Deployinator Info";
    }

    @Override
    public boolean shownByDefault() {
      return false;
    }
  }
}

