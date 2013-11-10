package com.gigaspaces.persistency.helper;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsa.GridServiceAgent;

import com.gigaspaces.persistency.utils.CommandLineProcess;

public class GSAgentController {

	private static final String QA_GROUP = "qa_group";
	private static final String LOOKUPGROUPS = "LOOKUPGROUPS";

	private final Admin admin = new AdminFactory().addGroup(QA_GROUP)
			.createAdmin();

	private CommandLineProcess GS_AGENT_PROCESS;

	private String gs_home = "c:/temp/gigaspaces-xap-premium-9.6.0-ga/";
	private Thread thread;
	private String GS_AGENT = (isWin()) ? "/bin/gs-agent.bat"
			: "/bin/gs-agent.sh";

	public GSAgentController() {
		this.gs_home = System.getenv("GS_HOME");
	}

	public void start() {

		String imagePath = combinePath();

		GS_AGENT_PROCESS = new CommandLineProcess(imagePath);

		GS_AGENT_PROCESS.addEnvironmentVariable(LOOKUPGROUPS, QA_GROUP);

		thread = new Thread(GS_AGENT_PROCESS);

		thread.start();

		admin.getGridServiceManagers().waitForAtLeastOne();
		
		

	}

	private String combinePath() {

		return FilenameUtils.normalize(gs_home + GS_AGENT);
	}

	private boolean isWin() {
		return (File.separatorChar == '\\');

	}

	public void stop() {

		for (GridServiceAgent gsa : admin.getGridServiceAgents()) {
			gsa.shutdown();
		}

		GS_AGENT_PROCESS.stop();
	}
}
