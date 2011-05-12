package com.etsy.jenkins;

import hudson.model.AbstractProject;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DeployinatorRestrictJobFilter implements Filter {

  static final Hudson HUDSON = Hudson.getInstance();
  static final Pattern PATTERN = Pattern.compile(".*/(.*)/build(\\?.*)?");
    
  public void init(FilterConfig config) throws ServletException {}

  public void doFilter(
      ServletRequest req, 
      ServletResponse res, 
      FilterChain chain) throws IOException, ServletException {
    if ((req instanceof HttpServletRequest) 
        && (res instanceof HttpServletResponse)) {
      HttpServletRequest httpReq = (HttpServletRequest) req;
      HttpServletResponse httpRes = (HttpServletResponse) res;
      String uri = httpReq.getRequestURI();
      Matcher matcher = PATTERN.matcher(uri);
      if (matcher.matches()) {
        String projectName = matcher.group(1);
        AbstractProject project = 
            (AbstractProject) HUDSON.getItem(projectName);
        if (project != null) {
          DeployinatorRestrictJobProperty property
              = (DeployinatorRestrictJobProperty)
                  project.getProperty(DeployinatorRestrictJobProperty.class);
          if ((property != null) && property.isEnabled()) {
            // TODO Make a global setting for the Deployinator
            httpRes.sendRedirect("http://deployinator.etsycorp.com");
            return;
          }
        }
      }
    }
    chain.doFilter(req, res);
  }

  public void destroy() {}
}

