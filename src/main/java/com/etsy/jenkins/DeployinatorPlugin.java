package com.etsy.jenkins;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.util.PluginServletFilter;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

public class DeployinatorPlugin
extends Plugin
implements Describable<DeployinatorPlugin> {

  private String deployinatorServer;

  public String getDeployinatorServer() {
      return deployinatorServer;
  }

  public void setDeployinatorServer(String deployinatorServer) {
      this.deployinatorServer = deployinatorServer;
  }

  public String getDeployinatorServerToUse() {
    if (deployinatorServer == null) {
        return ((DescriptorImpl) getDescriptor()).getDeployinatorServer();
    }
    return deployinatorServer;
  }

  @Override
  public void start() throws Exception {
    super.start();
    PluginServletFilter.addFilter(new DeployinatorRestrictJobFilter());
  }

  public DescriptorImpl getDescriptor() {
    return DESCRIPTOR;
  }

  @Extension
  public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
  public static final class DescriptorImpl
  extends Descriptor<DeployinatorPlugin> {

    String deployinatorServer;

    public DescriptorImpl() {
      super(DeployinatorPlugin.class);
      load();
    }

    public String getDisplayName() {
      return "Deployinator";
    }

    public String getDeployinatorServer() {
      return deployinatorServer;
    }

    public void setDeployinatorServer(String deployinatorServer) {
      this.deployinatorServer = deployinatorServer;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData)
        throws FormException {
      deployinatorServer = req.getParameter("deployinator_plugin.deployinatorServer");
      save();
      return super.configure(req, formData);
    }
  }
}

