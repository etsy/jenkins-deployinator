package com.etsy.jenkins;

import com.etsy.jenkins.cli.DeployinatorCommand;

import hudson.model.AbstractBuild;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nullable;

import au.com.centrumsystems.hudson.plugin.buildpipeline.RevisionHeader;
import au.com.centrumsystems.hudson.plugin.util.revision.Extractor;

public class DeployinatorRevisionHeader extends RevisionHeader {

  private AbstractBuild<?, ?> build;

  @Inject
  public DeployinatorRevisionHeader(
      @Assisted AbstractBuild<?, ?> build,
      @Nullable Extractor.Factory extractorFactory) {
    super(build, extractorFactory);
    this.build = build;
  }

  public DeployinatorCommand.CLICause getCause() {
    return (DeployinatorCommand.CLICause)
        this.build.getCause(DeployinatorCommand.CLICause.class);
  }
}

