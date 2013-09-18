package org.breizhjug.clientcloudbees;

import org.kohsuke.args4j.Option;

/**
 *
 * @author lhuet
 */
public class CmdLineOptions {

    @Option(name = "-key", usage = "CloudBees API Key", required = true)
    public String apiKey;
    
    @Option(name = "-secret", usage = "CloudBees Secret Key", required = true)
    public String apiSecret;
    
    @Option(name = "-url", usage = "CloudBees URL endpoint")
    public String apiUrl = "https://api.cloudbees.com/api";
    
    @Option(name = "-jenkinsurl", usage = "Jenkins XML API URL")
    public String jenkinsUrl = "https://breizhjug.ci.cloudbees.com/api/xml";
    
}
