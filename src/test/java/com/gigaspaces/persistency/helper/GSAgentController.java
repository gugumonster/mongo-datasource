package com.gigaspaces.persistency.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

	private Thread thread;
	private String GS_AGENT = (isWin()) ? "gs-agent.bat" : "gs-agent.sh";

	public void start() {

		List<String> args = new ArrayList<String>();

		args.add(GS_AGENT);

		GS_AGENT_PROCESS = new CommandLineProcess(args);

		GS_AGENT_PROCESS.addEnvironmentVariable(LOOKUPGROUPS, QA_GROUP);

		thread = new Thread(GS_AGENT_PROCESS);

		thread.start();

		admin.getGridServiceManagers().waitForAtLeastOne();

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
