package com.etsy.jenkins;

import hudson.Plugin;
import hudson.util.PluginServletFilter;

public class DeployinatorPlugin extends Plugin {

  @Override
  public void start() throws Exception {
    super.start();
    PluginServletFilter.addFilter(new DeployinatorRestrictJobFilter());
  }
}

