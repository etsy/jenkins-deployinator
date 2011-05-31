package com.etsy.jenkins;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryProvider;

import au.com.centrumsystems.hudson.plugin.buildpipeline.BuildForm;
import au.com.centrumsystems.hudson.plugin.util.BuildUtil;
import au.com.centrumsystems.hudson.plugin.util.ProjectUtil;

public class DeployinatorPipelineModule extends AbstractModule {

  protected void configure() {
      bind(ProjectUtil.class)
          .to(DeployinatorProjectUtil.class)
          .in(Singleton.class);
      bind(BuildUtil.class)
          .to(DeployinatorBuildUtil.class)
          .in(Singleton.class);
      bind(BuildForm.Factory.class)
          .toProvider(
              FactoryProvider.newFactory(
                  BuildForm.Factory.class, DeployinatorBuildForm.class));
  }
}

