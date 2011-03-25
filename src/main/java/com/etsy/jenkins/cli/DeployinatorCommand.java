package com.etsy.jenkins.cli;

import com.etsy.jenkins.cli.handlers.ProxyUserOptionHandler;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.console.HyperlinkNote;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.EnvironmentContributor;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.util.EditDistance;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.stapler.export.Exported;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Extension
public class DeployinatorCommand extends CLICommand {

  @Override
  public String getShortDescription() {
    return "Builds a project from deployinator,"
        + " and optionally waits until its completion.";
  }

  @Argument(
      metaVar="JOB", usage="Name of job to build.")
  public AbstractProject<?, ?> job;

  @Argument(
      metaVar="PROXY_USER",
      usage="LDAP of user that this build is being triggered for.",
      required=false,
      handler=ProxyUserOptionHandler.class,
      index=1)
  public User user = User.getUnknown();

  @Argument(
      metaVar="DEPLOY_TYPE",
      usage="Name of the service being deployed.",
      required=true,
      index=2)
  public String deployType;

  @Argument(
      metaVar="DEPLOY_VERSION",
      usage="Unique build version of recent deployinator build.",
      required=true,
      index=3)
  public String deployVersion;

  @Argument(
      metaVar="OLD_REVISION",
      usage="The last revision in the previous build.",
      required=true,
      index=4)
  public String oldRevision;

  @Argument(
      metaVar="NEW_REVISION",
      usage="The last revision included in this build.",
      required=true,
      index=5)
  public String newRevision;

  @Option(
      name="-s",
      usage="Wait until the completion/abortion of the command.")
  public boolean sync = false;

  @Option(
      name="-p",
      usage="Specify the build parameters in the key=value format.")
  public Map<String, String> parameters = Maps.<String, String>newHashMap();

  @Override
  protected int run() throws Exception {
    job.checkPermission(Item.BUILD);

    ParametersAction a = null;
    if (!parameters.isEmpty()) {
      ParametersDefinitionProperty pdp = 
          job.getProperty(ParametersDefinitionProperty.class);
      if (pdp == null) {
        throw new AbortException(
            String.format(
                "%s is not parameterized but the -p option was specified.",
                job.getDisplayName()));
      }

      List<ParameterValue> values = Lists.<ParameterValue>newArrayList();

      for (Map.Entry<String, String> e : parameters.entrySet()) {
        String name = e .getKey();
        ParameterDefinition pd = pdp.getParameterDefinition(name);
        if (pd == null) {
          throw new AbortException(
              String.format(
                  "\'%s\' is not a valid parameter. Did you mean %s?",
                  name,
                  EditDistance.findNearest(
                      name,
                      pdp.getParameterDefinitionNames())));
         }
         values.add(pd.createValue(this, e.getValue()));
       }

       a = new ParametersAction(values);
     }

     CLICause cause = 
         new CLICause(
             user.getDisplayName(),
             deployType,
             deployVersion,
             oldRevision,
             newRevision);

     Future<? extends AbstractBuild> f = 
         job.scheduleBuild2(0, cause, a);
     if (!sync) return 0;

     AbstractBuild build = null;
     do {
       build = findBuild(job, cause);
       stdout.println(
           String.format("......... %s (pending)\n", job.getDisplayName()));
       rest();
     } while(build == null);

     stdout.println(
         String.format("......... %s ( %s%s%s )\n", 
          job.getDisplayName(),
          Hudson.getInstance().getRootUrl(),
          build.getUrl(),
          "console"));
     build.setDescription(
         String.format(
             "<h3>%s</h3><br/><h4>%s&rarr;%s</h4><a href='%s'>diff</a> ", 
             cause.getUserName(),
             cause.getOldRevision(),
             cause.getNewRevision(),
             cause.getDeployinatorDiffUrl()));

     AbstractBuild b = f.get();  // wait for completion
     stdout.println(
         String.format(
             "Completed %s : %s",
             b.getFullDisplayName(),
             b.getResult()));
     return b.getResult().ordinal;
  }

  private AbstractBuild findBuild(AbstractProject project, Cause cause) {
    List<Run> builds = project.getBuilds();
    for (Run build : builds) {
      List<Cause> causes = build.getCauses();
      if (causes.contains(cause)) {
        return (AbstractBuild) build;
      }
    }
    return null;
  }

  private void rest() {
    try {
      Thread.sleep(7);
    } catch (InterruptedException ignore) {}
  }

  @Override
  protected void printUsageSummary(PrintStream stderr) {
    stderr.println(
        "Starts a build from deployinator, and optionally waits for a"
        + " completion.\n\n"
        + "Aside from general scripting use, this command can be\n"
        + "used to invoke another job from within a build of one job.\n"
        + "With the -s option, this command changes the exit code based on\n"
        + "the outcome of the build (exit code 0 indicates success.)\n");
  }

  public static class CLICause extends Cause {

    private final String user;
    private final String deployType;
    private final String deployVersion;
    private final String oldRevision;
    private final String newRevision;

    public CLICause(
        String user,
        String deployType,
        String deployVersion,
        String oldRevision,
        String newRevision) {
      this.user = user;
      this.deployType = deployType;
      this.deployVersion = deployVersion;
      this.oldRevision = oldRevision;
      this.newRevision = newRevision;
    }

    @Exported(visibility=3)
    public String getUserName() {
      User u = User.get(user, false);
      return (u == null) ? user : u.getDisplayName();
    }

    @Exported(visibility=3)
    public String getUserUrl() {
      User u = User.get(user, false);
      if (u == null) {
        u = User.getUnknown();
      }
      return u.getAbsoluteUrl();
    }

    @Exported(visibility=3)
    public String getDeployVersion() {
      return this.deployVersion;
    }

    @Exported(visibility=3)
    public String getOldRevision() {
      return this.oldRevision;
    }

    @Exported(visibility=3)
    public String getNewRevision() {
      return this.newRevision;
    }

    @Exported(visibility=3)
    public String getDeployinatorDiffUrl() {
      // TODO Add a global config var for deployinator host
      return String.format(
          "http://deployinator.etsycorp.com/diff/%s/%s/%s",
          this.deployType,
          this.oldRevision,
          this.newRevision);
    }

    @Override
    public String getShortDescription() {
      return "Started by command line";
    }

    @Override
    public void print(TaskListener listener) {
        listener.getLogger().println(String.format(
            "Started by deployinator for %s\n"
            + "Old Revision: %s, New Revision: %s %s\n",
            HyperlinkNote.encodeTo(getUserUrl(), getUserName()), 
            getOldRevision(),
            getNewRevision(),
            HyperlinkNote.encodeTo(getDeployinatorDiffUrl(), "diff")));
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CLICause)) {
        return false;
      }
      CLICause other = (CLICause) o;
      return Objects.equal(this.user, other.user)
          && Objects.equal(this.deployVersion, other.deployVersion);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(this.user, this.deployVersion);
    }
  }

  @Extension
  public static class CLIEnvironmentContributor
  extends EnvironmentContributor {

    @Override
    public void buildEnvironmentFor(Run r, EnvVars envs, TaskListener listener) {
      CLICause cause = (CLICause) r.getCause(CLICause.class);
      if (cause == null) return;
      envs.put("TRIGGERING_USER", cause.getUserName());
      envs.put("DEPLOY_VERSION", cause.getDeployVersion());
      envs.put("DEPLOY_OLD_REVISION", cause.getOldRevision());
      envs.put("DEPLOY_NEW_REVISION", cause.getNewRevision());
    }
  }
}

